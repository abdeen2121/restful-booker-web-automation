package com.restfulbooker.tests;

import com.restfulbooker.pages.AdminDashboardPage;
import com.restfulbooker.pages.BookingPage;
import com.restfulbooker.pages.ContactPage;
import com.restfulbooker.pages.LoginPage;
import com.restfulbooker.utils.BaseTest;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * NegativeTests - 21 negative test scenarios for automationintesting.online.
 *
 * All tests extend BaseTest which handles driver setup (@BeforeMethod)
 * and teardown (@AfterMethod).
 * Screenshot on failure is handled by AllureScreenshotListener.
 */
@Epic("Restful Booker - Web Automation")
@Feature("Negative Scenarios")
public class NegativeTests extends BaseTest {
    private AdminDashboardPage dashboardPage;

    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();
        loginPage.login("admin", "password");
        loginPage.isLoginSuccessful();
        dashboardPage = new AdminDashboardPage(driver);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N01 - Contact Form - Empty Submission
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N01 - Submit contact form with all fields empty and assert validation errors appear")
    @Description("Leave all contact form fields empty, click Submit, and assert that validation error messages are displayed.")
    @Story("Contact Form - Validation")
    public void testContactFormEmptySubmission() {
        ContactPage contactPage = new ContactPage(driver);
        contactPage.scrollToContactForm();
        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.areValidationErrorsPresent(),
                "Validation error messages were not displayed after empty form submission."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N02 - Contact Form - Invalid Email
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N02 - Submit contact form with invalid email format and assert validation error")
    @Description("Fill the contact form with an invalid email (e.g. 'user@com') and verify the system rejects it with an error.")
    @Story("Contact Form - Validation")
    public void testContactFormInvalidEmail() {
        ContactPage contactPage = new ContactPage(driver);
        contactPage.scrollToContactForm();

        contactPage.fillContactForm(
                "Mahmoud Abdeen",
                "user.com",            // Invalid email — no TLD / proper domain
                "01234567890",
                "Valid Subject",
                "This message is long enough to pass the message validation check easily."
        );
        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.areValidationErrorsPresent(),
                "No validation error was shown for the invalid email format 'user@com'."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N03 - Contact Form - Short Phone Number
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N03 - Submit contact form with a phone number shorter than 11 chars and assert validation error")
    @Description("Enter a phone number with fewer than 11 characters and verify the phone validation error is displayed.")
    @Story("Contact Form - Validation")
    public void testContactFormShortPhoneNumber() {
        ContactPage contactPage = new ContactPage(driver);
        contactPage.scrollToContactForm();

        contactPage.fillContactForm(
                "Mahmoud Abdeen",
                "valid@example.com",
                "0123",                // Too short — less than 11 characters
                "Valid Subject",
                "This message is long enough to pass the message length validation check."
        );
        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.areValidationErrorsPresent(),
                "No validation error was shown for the short phone number (< 11 characters)."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N04 - Contact Form - Short Message
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N04 - Submit contact form with a message shorter than 20 characters and assert error")
    @Description("Enter a message with fewer than 20 characters and assert the error: 'Message must be between 20 and 2000 characters'.")
    @Story("Contact Form - Validation")
    public void testContactFormShortMessage() {
        ContactPage contactPage = new ContactPage(driver);
        contactPage.scrollToContactForm();

        contactPage.fillContactForm(
                "Mahmoud Abdeen",
                "valid@example.com",
                "01234567890",
                "Valid Subject",
                "Short msg"            // Less than 20 characters
        );
        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.areValidationErrorsPresent(),
                "Validation error was not shown for a message shorter than 20 characters."
        );

        // Optionally assert the specific error message text
        String errors = contactPage.getValidationErrorText();
        Assert.assertTrue(
                errors.toLowerCase().contains("message must be between"),
                "Expected message length validation text not found. Actual errors: " + errors
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N05 - Contact Form - Short Subject
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N05 - Submit contact form with a subject shorter than 5 characters and assert error")
    @Description("Enter a subject with fewer than 5 characters and assert the subject validation error is displayed.")
    @Story("Contact Form - Validation")
    public void testContactFormShortSubject() {
        ContactPage contactPage = new ContactPage(driver);
        contactPage.scrollToContactForm();

        contactPage.fillContactForm(
                "Mahmoud Abdeen",
                "valid@example.com",
                "01234567890",
                "Hi",                  // Only 2 characters — less than 5
                "This message is long enough to pass the message length validation check."
        );
        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.areValidationErrorsPresent(),
                "Validation error was not shown for a subject shorter than 5 characters."
        );
    }



    // ─────────────────────────────────────────────────────────────────────────
    // N06 - Room Booking - Missing Calendar Dates
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N06 - Open booking panel, fill guest info but skip date selection, assert booking error")
    @Description("Click 'Book this room', fill all guest details but do NOT select dates, click Book, and assert an error is shown.")
    @Story("Room Booking - Validation")
    public void testRoomBookingEmptyFirstAndLastNameField() {
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openBookingPanel();

        bookingPage.selectDates(25, 27);

        bookingPage.clickReserveNow();

        bookingPage.fillGuestDetails(
                "",
                "",
                "ahmed@test.com",
                "01122334455"
        );
        bookingPage.clickFinalReserveNow();

        Assert.assertTrue(
                bookingPage.isBookingErrorDisplayed(),
                "Expected booking validation error when no Names were providing, but no error appeared."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N07 - Room Booking - Short Phone Number
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N07 - Select valid dates but enter a phone number < 11 chars and assert booking error")
    @Description("Select valid booking dates on the calendar, fill guest info with an invalid short phone number, and verify the booking error.")
    @Story("Room Booking - Validation")
    public void testRoomBookingShortPhoneNumber() {
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openBookingPanel();

        // Select dates first
        bookingPage.selectDates(25, 29);

        bookingPage.clickReserveNow();
        // Fill with a short phone number (< 11 digits)
        bookingPage.fillGuestDetails(
                "Ahmed",
                "Hassan",
                "ahmed@test.com",
                "0112"                 // Too short — less than 11 digits
        );
        bookingPage.clickFinalReserveNow();

        Assert.assertTrue(
                bookingPage.isBookingErrorDisplayed(),
                "Expected booking error for short phone number (< 11 digits) but no error appeared."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // N08 - Room Booking - Empty Guest Fields
    // ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N08 - Select valid dates but leave all guest detail fields empty, assert booking error")
    @Description("Select a valid date range on the calendar, leave all guest detail fields blank, click Book, and verify an error is displayed.")
    @Story("Room Booking - Validation")
    public void testRoomBookingEmptyGuestFields() {
        BookingPage bookingPage = new BookingPage(driver);
        bookingPage.openBookingPanel();

        // Select dates — but do NOT fill guest details
        bookingPage.selectDates(25, 28);

        bookingPage.clickReserveNow();
        // Click Book immediately without entering any guest info
        bookingPage.clickFinalReserveNow();

        Assert.assertTrue(
                bookingPage.isBookingErrorDisplayed(),
                "Expected booking validation error for empty guest fields, but no error appeared."
        );
    }

    // ===== INVALID USERNAME =====
// ─────────────────────────────────────────────────────────────────────────
// N09 - Login - Invalid Username
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N09 - Login with invalid username and assert invalid credentials message")
    @Description("Attempt to login using an incorrect username while keeping the password valid and verify that the system displays 'Invalid credentials'.")
    @Story("Login - Validation")
    public void testLoginInvalidUsername() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("dmin", "password");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N10 - Login - Invalid Password
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N10 - Login with invalid password and assert invalid credentials message")
    @Description("Attempt to login using a valid username and incorrect password and verify that login is rejected.")
    @Story("Login - Validation")
    public void testLoginInvalidPassword() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin", "wrongPass");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N11 - Login - Empty Username
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N11 - Login with empty username and assert invalid credentials message")
    @Description("Leave the username field empty, provide a valid password, and verify that login is rejected.")
    @Story("Login - Validation")
    public void testLoginEmptyUsername() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("", "password");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N12 - Login - Empty Password
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N12 - Login with empty password and assert invalid credentials message")
    @Description("Enter a valid username but leave the password field empty and verify login is rejected.")
    @Story("Login - Validation")
    public void testLoginEmptyPassword() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin", "");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N13 - Login - Empty Username and Password
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N13 - Login with both fields empty and assert invalid credentials message")
    @Description("Leave both username and password fields empty and verify that login is rejected.")
    @Story("Login - Validation")
    public void testLoginBothFieldsEmpty() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("", "");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N14 - Login - SQL Injection Attempt
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N14 - Attempt SQL injection in login form and verify login is not granted")
    @Description("Enter common SQL injection payloads in username and password fields and verify that authentication is not bypassed.")
    @Story("Login - Security")
    public void testLoginSqlInjectionAttempt() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("' OR '1'='1", "' OR '1'='1");

        boolean stillOnLoginPage =
                driver.getCurrentUrl().contains("/admin");

        Assert.assertTrue(
                stillOnLoginPage,
                "SQL injection should not allow login."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N15 - Login - Very Long Username
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N15 - Login with username exceeding normal length and verify rejection")
    @Description("Enter a username with 500 characters and verify that login is rejected.")
    @Story("Login - Validation")
    public void testLoginVeryLongUsername() {

        LoginPage loginPage = new LoginPage(driver);

        String longUsername = "a".repeat(500);

        loginPage.login(longUsername, "password");

        Assert.assertEquals(
                loginPage.getErrorMessage(),
                "Invalid credentials",
                "Expected invalid credentials message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N16 - Create Room - Empty Data
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N16 - Create room without entering any data and verify error message")
    @Description("Attempt to create a room while leaving all required fields empty and verify the operation fails.")
    @Story("Room Management - Validation")
    public void testCreateRoomWithNoData() {

        login();

        dashboardPage.clickCreateRoom();

        Assert.assertEquals(
                dashboardPage.getErrorMessage(),
                "Failed to create room",
                "Expected room creation failure message was not displayed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N17 - Create Room - Duplicate Room Number
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N17 - Create room using a duplicate room number and verify validation error")
    @Description("Attempt to create a room using an existing room number and verify that duplication is not allowed.")
    @Story("Room Management - Validation")
    public void testCreateDuplicateRoom() {

        login();

        dashboardPage.createRoom("101", "100");

        Assert.assertNotEquals(
                dashboardPage.getErrorCount(),
                0,
                "Duplicate room number should not be allowed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N18 - Create Room - Negative Price
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N18 - Create room with negative price and verify validation error")
    @Description("Attempt to create a room with a negative price value and verify that the system rejects it.")
    @Story("Room Management - Validation")
    public void testCreateRoomNegativePrice() {

        login();

        dashboardPage.createRoom("303", "-50");

        Assert.assertNotEquals(
                dashboardPage.getErrorCount(),
                0,
                "Negative room price should not be allowed."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N19 - Create Room - Zero Price
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N19 - Create room with zero price and verify validation error")
    @Description("Attempt to create a room with a price value equal to zero and verify that an error message is displayed.")
    @Story("Room Management - Validation")
    public void testCreateRoomZeroPrice() {

        login();

        dashboardPage.createRoom("404", "0");

        Assert.assertTrue(
                dashboardPage.isErrorAlertDisplayed(),
                "Error alert should be displayed for zero room price."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N20 - Create Room - Alphabetic Price
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N20 - Create room with alphabetic characters in price field and verify validation error")
    @Description("Attempt to create a room using letters instead of numbers in the price field and verify the operation is rejected.")
    @Story("Room Management - Validation")
    public void testCreateRoomWithLettersInPrice() {

        login();

        dashboardPage.createRoom("505", "abc");

        Assert.assertTrue(
                dashboardPage.isErrorAlertDisplayed(),
                "Error alert should be displayed for non-numeric room price."
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
// N21 - Create Room - Special Characters In Room Name
// ─────────────────────────────────────────────────────────────────────────
    @Test(description = "N21 - Create room using special characters in room name and verify validation error")
    @Description("Attempt to create a room using special characters as the room name and verify that the system rejects the input.")
    @Story("Room Management - Validation")
    public void testCreateRoomWithSpecialCharactersInName() {

        login();

        dashboardPage.createRoom("!@#$", "100");

        Assert.assertNotEquals(
                dashboardPage.getErrorCount(),
                0,
                "Room name containing only special characters should not be allowed."
        );
    }


}
