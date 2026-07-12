package com.restfulbooker.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * HomePage - Page Object for https://automationintesting.online/ (main landing page).
 */
public class HomePage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    private final By hotelLogo           = By.cssSelector("img[src*='rbp-logo']");
    private final By bookThisRoomBtn     = By.cssSelector("a.btn.btn-primary");


    private final By roomDescription     = By.cssSelector("#rooms .room-card .card-text");

    private final By roomPrice          = By.cssSelector("#rooms .fw-bold.fs-5");

    private final By roomAmenities       = By.cssSelector("#rooms .badge");
    private final By mapSection          = By.id("location");

    private final By contactFormHeading  = By.xpath("//h3[contains(text(),'Send Us a Message')]");


    // ─── Constructor ─────────────────────────────────────────────────────────

    public HomePage(WebDriver driver) {
        super(driver);
    }

    // ─── Action Methods ───────────────────────────────────────────────────────

    @Step("Verify hotel branding / logo is displayed")
    public boolean isHotelLogoDisplayed() {
        if (isVisible(hotelLogo)) {
            return true;
        }
        return driver.getTitle().toLowerCase().contains("restful") ||
                !driver.findElements(By.cssSelector("h1, h2, .navbar-brand")).isEmpty();
    }

    @Step("Verify contact info text is visible on homepage")
    public boolean isContactInfoVisible() {
        return isVisible(contactFormHeading);
    }

    @Step("Verify 'Book now' button is visible")
    public boolean isBookThisRoomButtonVisible() {
        try {
            return !waitForAllPresent(bookThisRoomBtn).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify room description is displayed")
    public boolean isRoomDescriptionVisible() {
        try {
            return waitForVisibility(roomDescription).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify room price is displayed")
    public boolean isRoomPriceVisible() {
        try {
            return waitForVisibility(roomPrice).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Verify room amenities are displayed")
    public boolean isRoomAmenitiesVisible() {
        try {
            return !driver.findElements(roomAmenities).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMapSectionVisible() {
        try {
            scrollToAndWait(mapSection);
            return waitForVisibility(mapSection).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Scroll to bottom of homepage")
    public void scrollToBottom() {
        super.scrollToBottom();
    }

    @Step("Scroll to contact section")
    public void scrollToContactSection() {
        scrollToAndWait(contactFormHeading);
    }
}