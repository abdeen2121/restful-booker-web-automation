package com.restfulbooker.utils;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * BaseTest - Parent class for all test classes.
 *
 * Responsibilities:
 * - @BeforeMethod: Initialize the WebDriver before each test.
 * - @AfterMethod: Quit the WebDriver after each test.
 *
 * Screenshot-on-failure is handled separately by AllureScreenshotListener
 * which is registered in testng.xml.
 */
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        driver = DriverManager.getDriver();
        driver.get("https://automationintesting.online/");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}
