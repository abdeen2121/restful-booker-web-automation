package com.restfulbooker.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

/**
 * DriverManager - Manages WebDriver instance lifecycle.
 * Uses ThreadLocal to ensure thread safety if tests are run in parallel.
 */
public class DriverManager {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    // Prevent instantiation
    private DriverManager() {}

    /**
     * Initializes and returns the WebDriver instance.
     * Sets up ChromeDriver via WebDriverManager with optimized options for Chrome 148+.
     */
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            WebDriverManager.chromedriver().setup();

            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            options.addArguments("--disable-popup-blocking");


            options.setPageLoadStrategy(PageLoadStrategy.EAGER);


            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--remote-allow-origins=*");

            // ====================================================================

            // Uncomment the line below to run headless in CI environments
            // options.addArguments("--headless=new");

            WebDriver webDriver = new ChromeDriver(options);
            webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));


            webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

            driver.set(webDriver);
        }
        return driver.get();
    }

    /**
     * Quits the WebDriver and removes the ThreadLocal instance.
     * Must be called in @AfterMethod to prevent resource leaks.
     */
    public static void quitDriver() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.remove();
        }
    }
}