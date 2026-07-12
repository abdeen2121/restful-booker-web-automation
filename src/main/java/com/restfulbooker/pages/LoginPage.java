package com.restfulbooker.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By usernameField = By.id("username");
    private final By passwordField = By.id("password");
    private final By loginButton   = By.id("doLogin");
    private final By errorMsg      = By.cssSelector("[class*='alert']");
    private final By logoutBtn     = By.cssSelector("[class*='btn-outline-danger']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    // ===== Navigate safely =====
    public void navigateTo() {
        driver.get("https://automationintesting.online/admin");
        wait.until(ExpectedConditions.presenceOfElementLocated(usernameField));
    }

    // ===== Login  =====
    public void login(String username, String password) {

        navigateTo();

        wait.until(ExpectedConditions.presenceOfElementLocated(usernameField));

        driver.findElement(usernameField).clear();
        driver.findElement(usernameField).sendKeys(username);

        driver.findElement(passwordField).clear();
        driver.findElement(passwordField).sendKeys(password);

        driver.findElement(loginButton).click();
    }

    // ===== Success check =====
    public boolean isLoginSuccessful() {
        return wait.until(ExpectedConditions.urlContains("/admin/rooms"))
                && driver.findElements(logoutBtn).size() > 0;
    }

    // ===== Error message =====
    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(errorMsg))
                .getText();
    }

    // ===== Logout =====
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutBtn)).click();
    }

    // ===== Check login state =====
    public boolean isLoggedIn() {
        return driver.getCurrentUrl().contains("/admin/rooms");
    }
}
