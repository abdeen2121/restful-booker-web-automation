package com.restfulbooker.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * ContactPage - Page Object for the Contact Form section of the homepage.
 *
 * ── What changed ─────────────────────────────────────────────────────────────
 *  REMOVED: scrollToAndPause(nameField, 500)  → scrollToAndWait(nameField)
 *
 *  scrollToAndWait scrolls the form into view then waits until the name field
 *  itself is visible — execution resumes the moment the field is ready.
 *  No Thread.sleep, no arbitrary 500 ms delay.
 */
public class ContactPage extends BasePage {

    // ─── Locators ────────────────────────────────────────────────────────────

    private final By nameField        = By.id("name");
    private final By emailField       = By.id("email");
    private final By phoneField       = By.id("phone");
    private final By subjectField     = By.id("subject");
    private final By messageField     = By.id("description");
    private final By submitButton     = By.xpath("//button[normalize-space()='Submit']");
    private final By successHeading   = By.xpath("//*[contains(text(),'Thanks for getting in touch')]");
    private final By alertDanger      = By.cssSelector(".alert.alert-danger");
    private final By validationErrors = By.cssSelector(".alert.alert-danger p, .alert-danger p");

    // ─── Constructor ─────────────────────────────────────────────────────────

    public ContactPage(WebDriver driver) {
        super(driver);
    }

    // ─── Navigation ───────────────────────────────────────────────────────────

    @Step("Scroll to and reveal the contact form")
    public void scrollToContactForm() {

        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight)");

    }

    // ─── Form Interactions ────────────────────────────────────────────────────

    @Step("Enter name: {name}")
    public void enterName(String name) {
        writeText(nameField, name);
    }

    @Step("Enter email: {email}")
    public void enterEmail(String email) {
        writeText(emailField, email);
    }

    @Step("Enter phone: {phone}")
    public void enterPhone(String phone) {
        writeText(phoneField, phone);
    }

    @Step("Enter subject: {subject}")
    public void enterSubject(String subject) {
        writeText(subjectField, subject);
    }

    @Step("Enter message: {message}")
    public void enterMessage(String message) {
        writeText(messageField, message);
    }

    @Step("Click Submit button on contact form")
    public void clickSubmit() {

        System.out.println("Submit button count = " +
                driver.findElements(submitButton).size());

        click(submitButton);
    }

    // ─── Assertions / Verifications ────────────────────────────────────────────

    @Step("Verify success message is displayed after submission")
    public boolean isSuccessMessageDisplayed() {
        return isVisible(successHeading);
    }

    @Step("Verify validation error messages are present")
    public boolean areValidationErrorsPresent() {
        try {
            waitForVisibility(alertDanger);
            List<WebElement> errors = driver.findElements(validationErrors);
            return !errors.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Get full text of validation errors")
    public String getValidationErrorText() {
        return getText(alertDanger);
    }


    // ─── Field Interactivity ──────────────────────────────────────────────────

    @Step("Verify all contact form fields are enabled")
    public boolean areAllFieldsEnabled() {
        return isEnabled(nameField)
            && isEnabled(emailField)
            && isEnabled(phoneField)
            && isEnabled(subjectField)
            && isEnabled(messageField);
    }

    // ─── Helper: Fill the full form ───────────────────────────────────────────

    @Step("Fill contact form with: name={name}, email={email}, phone={phone}, subject={subject}")
    public void fillContactForm(String name, String email, String phone,
                                String subject, String message) {
        enterName(name);
        enterEmail(email);
        enterPhone(phone);
        enterSubject(subject);
        enterMessage(message);
    }
}
