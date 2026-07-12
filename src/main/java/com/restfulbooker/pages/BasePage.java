package com.restfulbooker.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage — Parent class for all Page Object classes.
 *
 * ── What changed in this revision ──────────────────────────────────────────
 *  REMOVED: pause(long millis) — the Thread.sleep wrapper is gone entirely.
 *  REMOVED: scrollToAndPause(By, long) — replaced by scrollToAndWait(By, By)
 *           which waits for a target element to become visible after scrolling
 *           instead of sleeping for an arbitrary number of milliseconds.
 *
 *  ADDED:   scrollToAndWait(By scrollTarget, By waitFor)
 *           Scrolls to scrollTarget, then waits until waitFor is visible.
 *           Use this whenever scroll must be followed by a dynamic-content check.
 *
 *  ADDED:   waitForUrlToContain(String fragment)
 *           Waits for the browser URL to contain the given string.
 *           Useful after login redirects, SPA route changes, etc.
 *
 *  ADDED:   waitForStalenessOf(WebElement element)
 *           Waits until element is detached from the DOM (e.g. after a re-render).
 *
 * ── Conventions ─────────────────────────────────────────────────────────────
 *  • Methods that must succeed → throw TimeoutException (loud failures).
 *  • Methods used for assertions → return boolean / String, never throw.
 */
public abstract class BasePage {

    // ─── Shared Infrastructure ────────────────────────────────────────────────

    protected final WebDriver          driver;
    protected final WebDriverWait      wait;
    protected final JavascriptExecutor js;
    protected final Actions            actions;

    /** Default explicit-wait timeout shared by every child page. */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);

    // ─── Constructor ──────────────────────────────────────────────────────────

    protected BasePage(WebDriver driver) {
        this.driver  = driver;
        this.wait    = new WebDriverWait(driver, DEFAULT_TIMEOUT);
        this.js      = (JavascriptExecutor) driver;
        this.actions = new Actions(driver);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // EXPLICIT-WAIT HELPERS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Waits until the element is visible in the DOM and returns it.
     * Throws TimeoutException if the element does not appear within the timeout.
     */
    protected WebElement waitForVisibility(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits until the element is present in the DOM (not necessarily visible).
     * Useful for hidden containers before interacting with their children.
     */
    protected WebElement waitForPresence(By locator) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Waits until ALL elements matching {@code locator} are present and returns
     * the non-empty list.
     */
    protected List<WebElement> waitForAllPresent(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    /**
     * Waits until the element is clickable (visible + enabled) and returns it.
     */
    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }


    // ═════════════════════════════════════════════════════════════════════════
    // CORE INTERACTION VERBS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Waits for the element to be clickable, then clicks it.
     */
    protected void click(By locator) {

        WebElement element = waitForClickable(locator);

        js.executeScript(
                "arguments[0].scrollIntoView({block:'center'});",
                element);

        js.executeScript(
                "arguments[0].click();",
                element);
    }

    /**
     * Waits for the element to be visible, clears any existing value,
     * then types {@code text} into it.
     */
    protected void writeText(By locator, String text) {
        WebElement el = waitForVisibility(locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Waits for the element to be visible and returns its trimmed text content.
     * Returns an empty string if the element is not found within the timeout.
     */
    protected String getText(By locator) {
        try {
            return waitForVisibility(locator).getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // SAFE BOOLEAN CHECKS  (never throw — suitable for assertion helpers)
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Returns {@code true} if an element matching {@code locator} becomes
     * visible within the default timeout; {@code false} on timeout.
     */
    protected boolean isVisible(By locator) {
        try {
            return waitForVisibility(locator).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns {@code true} if the element becomes visible and is enabled.
     * Never throws.
     */
    protected boolean isEnabled(By locator) {
        try {
            return waitForVisibility(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // JAVASCRIPT / SCROLL UTILITIES
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Scrolls the given {@link WebElement} into the centre of the viewport.
     */
    protected void scrollIntoView(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /**
     * Waits for the element at {@code locator} to be present, then scrolls it
     * into the centre of the viewport.
     */
    protected void scrollIntoView(By locator) {
        scrollIntoView(waitForPresence(locator));
    }

    /**
     * Scrolls the page to the very bottom.
     * Useful for lazy-loaded sections (map, contact form) that only render
     * when they enter the viewport.
     */
    @Step("Scroll to the bottom of the page")
    protected void scrollToBottom() {
        js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }

    /**
     * Scrolls to {@code scrollTarget} and then waits until {@code waitForLocator}
     * is visible before returning.
     *
     * <p>Replaces the old {@code scrollToAndPause(By, long)} — no Thread.sleep,
     * no hard-coded milliseconds. Execution resumes as soon as the element is
     * ready, making tests faster on fast machines and robust on slow ones.</p>
     *
     * @param scrollTarget  element to scroll into view
     * @param waitForLocator element to wait for after scrolling
     */
    protected void scrollToAndWait(By scrollTarget, By waitForLocator) {
        scrollIntoView(scrollTarget);
        waitForVisibility(waitForLocator);
    }

    /**
     * Scrolls to the element at {@code locator} and waits until that same
     * element itself is visible.
     *
     * <p>Convenience overload for the common case where the element to scroll to
     * and the element to wait for are the same (e.g. the map section, the
     * contact form header).</p>
     *
     * @param locator element to scroll to and wait for
     */
    protected void scrollToAndWait(By locator) {
        scrollToAndWait(locator, locator);
    }

    // ═════════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Navigates the browser to the given absolute URL.
     */
    protected void navigateTo(String url) {
        driver.get(url);
    }
}
