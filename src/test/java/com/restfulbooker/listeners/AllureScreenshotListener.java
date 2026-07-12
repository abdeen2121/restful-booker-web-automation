package com.restfulbooker.listeners;

import com.restfulbooker.utils.DriverManager;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * AllureScreenshotListener - TestNG ITestListener that captures a screenshot
 * and attaches it to the Allure report whenever a test fails.
 *
 * Must be registered in testng.xml under <listeners>.
 */
public class AllureScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = DriverManager.getDriver();

        if (driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Allure.addAttachment(
                        "Screenshot on Failure - " + result.getName(),
                        "image/png",
                        new ByteArrayInputStream(screenshot),
                        ".png"
                );
            } catch (Exception e) {
                System.err.println("[AllureScreenshotListener] Failed to capture screenshot: " + e.getMessage());
            }
        }
    }

    // Unused lifecycle hooks — no-op implementations
    @Override public void onTestStart(ITestResult result) {}
    @Override public void onTestSuccess(ITestResult result) {}
    @Override public void onTestSkipped(ITestResult result) {}
}
