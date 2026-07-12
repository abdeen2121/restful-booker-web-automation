package com.restfulbooker.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class AdminDashboardPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // ===== Rooms Locators =====
    private final By roomsNav     = By.cssSelector("[class*='nav-link active']");
    private final By createButton = By.id("createRoom");
    private final By errorMsg     = By.cssSelector(".alert.alert-danger");
    private final By roomIdField  = By.id("roomName");
    private final By roomPrice    = By.id("roomPrice");
    private final By reportLink   = By.cssSelector("a[href*='report']");
    private final By messageLink  = By.cssSelector("a[href='/admin/message']");
    private final By brandingLink = By.id("brandingLink");

    // ===== Modal Locators =====
    private final By modalOverlay  = By.cssSelector(".ReactModal__Overlay");
    private final By modalCloseBtn = By.cssSelector(".ReactModal__Content button.btn-danger");

    // ===== Message Locators =====
    private final By messageItems   = By.cssSelector(".row.detail");
    private final By messageContent = By.cssSelector("[class*='row detail read-true']");
    private final By messageDelete  = By.cssSelector("[class*='fa fa-remove roomDelete']");

    // ===== Branding Locators =====
    private final By hotelNameField  = By.id("name");
    private final By logoUrlField    = By.id("logoUrl");
    private final By saveBrandingBtn = By.id("updateBranding");
    private final By brandingError   = By.cssSelector(".alert.alert-danger");

    public AdminDashboardPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ===== Modal Helper =====
    public void closeModalIfOpen() {
        try {
            WebElement modal = driver.findElement(modalOverlay);
            if (modal.isDisplayed()) {
                try {
                    driver.findElement(modalCloseBtn).click();
                } catch (Exception e) {
                    driver.findElement(By.tagName("body"))
                            .sendKeys(org.openqa.selenium.Keys.ESCAPE);
                }
                wait.until(ExpectedConditions
                        .invisibilityOfElementLocated(modalOverlay));
            }
        } catch (Exception e) {

        }
    }

    // ===== JavaScript Click Helper =====
    private void jsClick(By locator) {
        WebElement el = wait.until(ExpectedConditions
                .presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }

    // ===== Rooms =====
    public boolean isRoomsNavDisplayed() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(roomsNav))
                .isDisplayed();
    }

    public void clickCreateRoom() {
        wait.until(ExpectedConditions
                        .elementToBeClickable(createButton))
                .click();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(errorMsg))
                .getText();
    }

    public int getErrorCount() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(errorMsg));
            return driver.findElements(errorMsg).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void createRoom(String id, String price) {
        clickCreateRoom();
        wait.until(ExpectedConditions
                .visibilityOfElementLocated(roomIdField));
        driver.findElement(roomIdField).sendKeys(id);
        driver.findElement(roomPrice).sendKeys(price);
        clickCreateRoom();
    }

    public boolean isRoomInList(String roomId) {
        By roomLocator = By.id("roomName" + roomId);
        try {
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(roomLocator));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteRoom(String roomId) {
        By deleteBtn = By.xpath(
                "//div[@data-testid='roomlisting'][.//p[@id='roomName" + roomId + "']]//span"
        );
        wait.until(ExpectedConditions
                        .elementToBeClickable(deleteBtn))
                .click();
    }

    public boolean isErrorAlertDisplayed() {
        try {
            return wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorMsg)
            ).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // ===== Report =====
    public boolean isReportNavReady() {
        WebElement el = wait.until(ExpectedConditions
                .elementToBeClickable(reportLink));
        return el.isDisplayed() && el.isEnabled();
    }

    public void clickReportNav() {
        closeModalIfOpen();
        jsClick(reportLink);
    }

    public boolean isReportPageLoaded() {
        return wait.until(ExpectedConditions
                .urlContains("report"));
    }

    // ===== Message =====
    public boolean isMessageNavReady() {
        WebElement el = wait.until(ExpectedConditions
                .elementToBeClickable(messageLink));
        return el.isDisplayed() && el.isEnabled();
    }

    public void clickMessageNav() {
        closeModalIfOpen();
        jsClick(messageLink);
        wait.until(ExpectedConditions.urlContains("message"));
    }

    public boolean isMessagesListDisplayed() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(messageItems))
                .isDisplayed();
    }

    public void openFirstMessage() {
        List<WebElement> messages = wait.until(ExpectedConditions
                .visibilityOfAllElementsLocatedBy(messageItems));
        messages.get(0).click();
    }

    public boolean isMessageContentDisplayed() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(messageContent))
                .isDisplayed();
    }

    public int getMessagesCount() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOfAllElementsLocatedBy(messageItems));
            return driver.findElements(messageItems).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public void deleteFirstMessage() {
        int beforeCount = getMessagesCount();
        List<WebElement> deleteBtns = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(messageDelete));
        deleteBtns.get(0).click();
        wait.until(driver -> getMessagesCount() < beforeCount);
    }

    public void closeMessageDetail() {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".btn.btn-outline-primary")
        )).click();
    }

    // ===== Branding =====
    public boolean isBrandingNavReady() {
        WebElement el = wait.until(ExpectedConditions
                .elementToBeClickable(brandingLink));
        return el.isDisplayed() && el.isEnabled();
    }

    public void clickBrandingNav() {
        closeModalIfOpen();
        jsClick(brandingLink);
        wait.until(ExpectedConditions.urlContains("branding"));
    }
    public void updateHotelName(String name) {
        WebElement field = wait.until(
                ExpectedConditions.visibilityOfElementLocated(hotelNameField));

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].value='';", field);

        field.sendKeys(name);
    }

    public void updateLogoUrl(String url) {
        WebElement field = wait.until(ExpectedConditions
                .visibilityOfElementLocated(logoUrlField));
        field.clear();
        field.sendKeys(url);
    }

    public void saveBranding() {
        wait.until(ExpectedConditions
                        .elementToBeClickable(saveBrandingBtn))
                .click();
    }

    public String getHotelName() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(hotelNameField))
                .getAttribute("value");
    }

    public String getLogoUrl() {
        return wait.until(ExpectedConditions
                        .visibilityOfElementLocated(logoUrlField))
                .getAttribute("value");
    }

    public int getBrandingErrorCount() {
        try {
            wait.until(ExpectedConditions
                    .visibilityOfElementLocated(brandingError));
            return driver.findElements(brandingError).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isBrandingValidationErrorVisible() {
        try {
            WebElement error = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//*[contains(text(),'Name should not be blank') or " +
                                    "contains(text(),'Url should be a correct url format') or " +
                                    "contains(text(),'size must be between')]")
                    )
            );
            return error.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}
