package com.restfulbooker.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * BookingPage - Page Object for the Room Booking panel.
 *
 * ── What changed ─────────────────────────────────────────────────────────────
 *  REMOVED: pause(500) after the drag interaction.
 *
 *  The old code did Thread.sleep(500) to wait for the calendar state to settle
 *  after a click-and-hold drag. This is replaced with two explicit waits:
 *
 *  1. waitFor(ExpectedConditions.or(visibilityOfElementLocated(firstnameField),
 *                                   visibilityOfElementLocated(bookingErrorAlert)))
 *
 *     After a successful date drag the booking panel reveals the guest-detail
 *     fields; after an invalid/no-op drag it may show an error. Either element
 *     appearing signals the UI has settled — no sleep needed.
 *
 *  2. If neither appears within the timeout, we fall through gracefully.
 *     The subsequent writeText calls will themselves timeout with clear messages.
 */
public class BمسookingPage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    private final By bookThisRoomBtn   = By.xpath("//a[contains(@href,'/reservation/')]");

    private final By reserveNowBtn     = By.id("doReservation");

    private final By confirmReservationButton =
            By.xpath("//form[.//input[@name='firstname']]//button[contains(text(),'Reserve Now')]");
    // Calendar — react-big-calendar (rbc) style
    private final By calendarContainer = By.cssSelector(".rbc-calendar, .booking-dates, .react-calendar");
    private final By calendarCells     = By.cssSelector(
            ".rbc-date-cell:not(.rbc-off-range) a, .rbc-date-cell:not(.rbc-off-range)");

    // Guest details
    private final By firstnameField    = By.cssSelector("input[name='firstname'], input[placeholder*='Firstname']");
    private final By lastnameField     = By.cssSelector("input[name='lastname'],  input[placeholder*='Lastname']");
    private final By emailField        = By.cssSelector("input[name='email'],     input[placeholder*='Email']");
    private final By phoneField        = By.cssSelector("input[name='phone'],     input[placeholder*='Phone']");

    // Buttons
    private final By bookButton        = By.xpath("//button[normalize-space()='Book' and not(contains(.,'this room'))]");

    // Outcomes
    private final By confirmationMsg   = By.xpath("//*[contains(text(),'Booking Confirmed')]");
    private final By bookingErrorAlert = By.cssSelector(".alert.alert-danger, .booking-error");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public BookingPage(WebDriver driver) {
        super(driver);
    }

    // ─── Open Booking Panel ────────────────────────────────────────────────────

    @Step("Click 'Book this room' to open the booking panel")
    public void openBookingPanel() {
        WebElement btn = waitForClickable(bookThisRoomBtn);

        scrollIntoView(btn);

        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", btn);
    }

    @Step("Click 'Reserve now' to reserve room")
    public void clickReserveNow() {
        click(reserveNowBtn);
    }

    @Step("Click final Reserve Now button")
    public void clickFinalReserveNow() {
        click(confirmReservationButton);
    }
    // ─── Calendar Interaction ─────────────────────────────────────────────────

    /**
     * Selects a date range on the RBC calendar by dragging from the cell whose
     * visible text equals {@code startDayNum} to the cell with {@code endDayNum}.
     *
     * <p>After the drag, we wait for the guest-detail firstname field (or an error
     * alert) to become visible — this replaces the old {@code pause(500)} call.
     * The test proceeds as soon as the UI has reacted, not after an arbitrary
     * 500 ms delay.</p>
     *
     * @param startDayNum visible day number to start drag (e.g. 10)
     * @param endDayNum   visible day number to end drag   (e.g. 12)
     */
    @Step("Select booking dates: from day {startDayNum} to day {endDayNum}")
    public void selectDates(int startDayNum, int endDayNum) {
        // Wait for calendar to render
        wait.until(ExpectedConditions.presenceOfElementLocated(calendarContainer));

        List<WebElement> cells = waitForAllPresent(calendarCells);

        WebElement startCell = null;
        WebElement endCell   = null;

        for (WebElement cell : cells) {
            String text = cell.getText().trim();
            if (text.equals(String.valueOf(startDayNum)) && startCell == null) {
                startCell = cell;
            } else if (text.equals(String.valueOf(endDayNum)) && endCell == null) {
                endCell = cell;
            }
        }

        if (startCell != null && endCell != null) {
            scrollIntoView(startCell);
            actions.clickAndHold(startCell)
                   .moveToElement(endCell)
                   .release()
                   .perform();
        } else if (cells.size() >= 2) {
            // Fallback: positional indices when exact day numbers are not found
            WebElement fallbackStart = cells.get(startDayNum % cells.size());
            WebElement fallbackEnd   = cells.get(endDayNum   % cells.size());
            actions.clickAndHold(fallbackStart)
                   .moveToElement(fallbackEnd)
                   .release()
                   .perform();
        }

        // CHANGED: pause(500) → explicit wait for UI state after drag.
        // Wait until either the guest-detail fields appear (successful drag)
        // or an error alert appears (date conflict / invalid range).
        // This replaces Thread.sleep(500) — returns as soon as the DOM reacts.
        try {
            wait.until(ExpectedConditions.or(
                    ExpectedConditions.visibilityOfElementLocated(firstnameField),
                    ExpectedConditions.visibilityOfElementLocated(bookingErrorAlert)
            ));
        } catch (Exception ignored) {
            // If neither appears, subsequent waits in fillGuestDetails will
            // surface the problem with clear TimeoutException messages.
        }
    }


    // ─── Guest Details ────────────────────────────────────────────────────────

    @Step("Enter guest firstname: {firstname}")
    public void enterFirstname(String firstname) {
        writeText(firstnameField, firstname);
    }

    @Step("Enter guest lastname: {lastname}")
    public void enterLastname(String lastname) {
        writeText(lastnameField, lastname);
    }

    @Step("Enter guest email: {email}")
    public void enterEmail(String email) {
        writeText(emailField, email);
    }

    @Step("Enter guest phone: {phone}")
    public void enterPhone(String phone) {
        writeText(phoneField, phone);
    }

    @Step("Fill all guest details: firstname={firstname}, lastname={lastname}, email={email}, phone={phone}")
    public void fillGuestDetails(String firstname, String lastname, String email, String phone) {
        enterFirstname(firstname);
        enterLastname(lastname);
        enterEmail(email);
        enterPhone(phone);
    }

    // ─── Submit Booking ────────────────────────────────────────────────────────

    @Step("Click the 'Book' button to submit the booking")
    public void clickBook() {
        click(bookButton);
    }

    // ─── Assertions ────────────────────────────────────────────────────────────

    @Step("Verify booking confirmation message is displayed")
    public boolean isBookingConfirmationDisplayed() {
        return isVisible(confirmationMsg);
    }

    @Step("Verify booking error/validation message is displayed")
    public boolean isBookingErrorDisplayed() {
        return isVisible(bookingErrorAlert);
    }

    @Step("Get booking error message text")
    public String getBookingErrorText() {
        return getText(bookingErrorAlert);
    }

}
