package com.restfulbooker.tests;

import com.restfulbooker.pages.AdminDashboardPage;
import com.restfulbooker.pages.BookingPage;
import com.restfulbooker.pages.ContactPage;
import com.restfulbooker.pages.HomePage;
import com.restfulbooker.pages.LoginPage;
import com.restfulbooker.utils.BaseTest;
import io.qameta.allure.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * PositiveTests - 23 positive test scenarios for automationintesting.online.
 *
 * All tests extend BaseTest which handles driver setup (@BeforeMethod)
 * and teardown (@AfterMethod).
 * Screenshot on failure is handled by AllureScreenshotListener.
 */

@Epic("Restful Booker - Web Automation")
@Feature("Positive Scenarios")
public class PositiveTests extends BaseTest {

    private AdminDashboardPage dashboardPage;

    public void login() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.navigateTo();
        loginPage.login("admin", "password");
        loginPage.isLoginSuccessful();
        dashboardPage = new AdminDashboardPage(driver);
    }

    

    @Test(description = "P01 - Successful contact form submission")
    @Description("Fill contact form with valid data and verify success message appears.")
    @Story("Contact Form")
    public void testContactFormSubmission() {

        ContactPage contactPage = new ContactPage(driver);

        contactPage.scrollToContactForm();

        contactPage.fillContactForm(
                "Mahmoud Abdeen",
                "abdeen.test@example.com",
                "01234567890",
                "Test Inquiry",
                "This is a valid test message with enough characters."
        );

        contactPage.clickSubmit();

        Assert.assertTrue(
                contactPage.isSuccessMessageDisplayed(),
                "Success message should be displayed."
        );
    }

    @Test(description = "P02 - Contact form fields interactivity")
    @Description("Verify all contact form fields are enabled and interactable.")
    @Story("Contact Form")
    public void testContactFormInteractivity() {

        ContactPage contactPage = new ContactPage(driver);

        contactPage.scrollToContactForm();

        Assert.assertTrue(
                contactPage.areAllFieldsEnabled(),
                "All contact fields should be enabled."
        );
    }

    // ===================== HOMEPAGE =====================

    @Test(description = "P03 - Homepage UI elements visibility")
    @Description("Verify logo, contact info, and book button are visible.")
    @Story("Homepage UI")
    public void testHomepageUIElements() {

        HomePage homePage = new HomePage(driver);

        Assert.assertTrue(homePage.isHotelLogoDisplayed());
        homePage.scrollToContactSection();

        Assert.assertTrue(homePage.isContactInfoVisible());
        Assert.assertTrue(homePage.isBookThisRoomButtonVisible());
    }

    @Test(description = "P04 - Room details visibility")
    @Description("Verify room description, price, and amenities are visible.")
    @Story("Homepage UI")
    public void testRoomDetailsVisibility() {

        HomePage homePage = new HomePage(driver);

        Assert.assertTrue(homePage.isRoomDescriptionVisible());
        Assert.assertTrue(homePage.isRoomPriceVisible());
        Assert.assertTrue(homePage.isRoomAmenitiesVisible());
    }

    @Test(description = "P05 - Map section visibility")
    @Description("Verify map section is displayed on homepage.")
    @Story("Homepage UI")
    public void testMapVisibility() {

        HomePage homePage = new HomePage(driver);

        Assert.assertTrue(homePage.isMapSectionVisible());
    }

    // ===================== BOOKING =====================

    @Test(description = "P06 - Successful room booking")
    @Description("Book a room and verify confirmation appears.")
    @Story("Room Booking")
    public void testSuccessfulBooking() {

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.openBookingPanel();
        bookingPage.selectDates(25, 29);
        bookingPage.clickReserveNow();

        bookingPage.fillGuestDetails(
                "Ahmed",
                "Hassan",
                "ahmed.hassan@test.com",
                "01122334455"
        );

        bookingPage.clickFinalReserveNow();

        Assert.assertTrue(
                bookingPage.isBookingConfirmationDisplayed(),
                "Booking confirmation should appear."
        );
    }

    // ===================== AUTH =====================

    @Test(description = "P07 - Valid login")
    @Description("Login with valid credentials and verify success.")
    @Story("Authentication")
    public void testValidLogin() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "password");

        Assert.assertTrue(loginPage.isLoginSuccessful());
    }

    @Test(description = "P08 - Logout back button behavior")
    @Description("Logout and verify back button does not restore session.")
    @Story("Authentication")
    public void testLogoutBackButton() {

        LoginPage loginPage = new LoginPage(driver);

        loginPage.login("admin", "password");

        Assert.assertTrue(loginPage.isLoginSuccessful());

        loginPage.logout();

        driver.navigate().back();

        Assert.assertFalse(loginPage.isLoggedIn());
    }

    // ===================== ROOMS =====================

    @Test(description = "P09 - Rooms navigation visibility")
    @Description("Verify rooms navigation is visible after login.")
    @Story("Room Management")
    public void testRoomsNavVisible() {

        login();

        Assert.assertTrue(dashboardPage.isRoomsNavDisplayed());
    }

    @Test(description = "P10 - Create room")
    @Description("Create a valid room and verify it appears in list.")
    @Story("Room Management")
    public void testCreateRoom() {

        login();

        dashboardPage.createRoom("202", "150");

        Assert.assertTrue(dashboardPage.isRoomInList("202"));
    }

    @Test(description = "P11 - Delete room")
    @Description("Delete a room and verify it is removed.")
    @Story("Room Management")
    public void testDeleteRoom() {

        login();

        dashboardPage.createRoom("606", "200");
        dashboardPage.deleteRoom("606");

        Assert.assertFalse(dashboardPage.isRoomInList("606"));
    }

    // ===================== REPORTS =====================

    @Test(description = "P12 - Report navigation visible")
    @Description("Verify report navigation is available.")
    @Story("Reports")
    public void testReportNavVisible() {

        login();

        Assert.assertTrue(dashboardPage.isReportNavReady());
    }

    @Test(description = "P13 - Navigate to report page")
    @Description("Click report and verify navigation.")
    @Story("Reports")
    public void testReportNavigation() {

        login();

        dashboardPage.clickReportNav();

        boolean navigated = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("report"));

        Assert.assertTrue(navigated);
    }

    @Test(description = "P14 - Report page loads")
    @Description("Verify report page loads successfully.")
    @Story("Reports")
    public void testReportPageLoads() {

        login();

        dashboardPage.clickReportNav();

        Assert.assertTrue(dashboardPage.isReportPageLoaded());
    }

    // ===================== MESSAGES =====================

    @Test(description = "P15 - Message nav visible")
    @Description("Verify message navigation is visible.")
    @Story("Messages")
    public void testMessageNavVisible() {

        login();

        Assert.assertTrue(dashboardPage.isMessageNavReady());
    }

    @Test(description = "P16 - Navigate to messages")
    @Description("Verify navigation to messages page.")
    @Story("Messages")
    public void testMessageNavigation() {

        login();

        dashboardPage.clickMessageNav();

        Assert.assertTrue(driver.getCurrentUrl().contains("message"));
    }

    @Test(description = "P17 - Messages list visible")
    @Description("Verify messages list is displayed.")
    @Story("Messages")
    public void testMessagesList() {

        login();

        dashboardPage.clickMessageNav();

        Assert.assertTrue(dashboardPage.isMessagesListDisplayed());
    }

    @Test(description = "P18 - Open message")
    @Description("Open message and verify content.")
    @Story("Messages")
    public void testOpenMessage() {

        login();

        dashboardPage.clickMessageNav();

        dashboardPage.openFirstMessage();

        Assert.assertTrue(dashboardPage.isMessageContentDisplayed());

        dashboardPage.closeMessageDetail();
    }

    @Test(description = "P19 - Delete message")
    @Description("Delete message and verify count decreases.")
    @Story("Messages")
    public void testDeleteMessage() {

        login();

        dashboardPage.clickMessageNav();

        int before = dashboardPage.getMessagesCount();

        dashboardPage.deleteFirstMessage();

        int after = dashboardPage.getMessagesCount();

        Assert.assertTrue(after < before);
    }

    // ===================== BRANDING =====================

    @Test(description = "P20 - Branding nav visible")
    @Description("Verify branding navigation is visible.")
    @Story("Branding")
    public void testBrandingNavVisible() {

        login();

        Assert.assertTrue(dashboardPage.isBrandingNavReady());
    }

    @Test(description = "P21 - Navigate branding page")
    @Description("Verify navigation to branding page.")
    @Story("Branding")
    public void testBrandingNavigation() {

        login();

        dashboardPage.clickBrandingNav();

        Assert.assertTrue(driver.getCurrentUrl().contains("branding"));
    }

    @Test(description = "P22 - Update hotel name")
    @Description("Update hotel name and verify change.")
    @Story("Branding")
    public void testUpdateHotelName() {

        login();

        dashboardPage.clickBrandingNav();

        dashboardPage.updateHotelName("Test Hotel");

        Assert.assertEquals(dashboardPage.getHotelName(), "Test Hotel");
    }

    @Test(description = "P23 - Update logo URL")
    @Description("Update logo URL and verify it.")
    @Story("Branding")
    public void testUpdateLogoUrl() {

        login();

        dashboardPage.clickBrandingNav();

        dashboardPage.updateLogoUrl("https://example.com/logo.png");

        Assert.assertEquals(
                dashboardPage.getLogoUrl(),
                "https://example.com/logo.png"
        );
    }
}
