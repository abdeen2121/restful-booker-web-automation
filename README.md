# Restful Booker Web Automation Framework

**Target:** [https://automationintesting.online/](https://automationintesting.online/)  
**Stack:** Java 11 · Selenium 4 · TestNG · Maven · Allure Reports · WebDriverManager

---

## Project Structure

```
restful-booker-framework/
├── pom.xml
└── src/
    ├── main/java/com/restfulbooker/
    │   ├── pages/
    │   │   ├── HomePage.java
    │   │   ├── ContactPage.java
    │   │   ├── BasePage.java
    │   │   ├── LoginPage.java
    │   │   ├── AdminDashboardPage.java
    │   │   └── BookingPage.java
    │   └── utils/
    │       ├── BaseTest.java
    │       └── DriverManager.java
    └── test/
        ├── java/com/restfulbooker/
        │   ├── listeners/
        │   │   └── AllureScreenshotListener.java
        │   └── tests/
        │       ├── PositiveTests.java
        │       └── NegativeTests.java
        └── resources/
            ├── testng.xml
            └── allure.properties
```

---

## Prerequisites

- Java 11+
- Maven 3.6+
- Google Chrome (latest)
- Allure CLI (for HTML report generation): `brew install allure` or download from [allure.qatools.ru](https://docs.qameta.io/allure/)

---

## How to Run

### Run all tests
```bash
mvn clean test
```

### Run only Positive tests
```bash
mvn clean test -Dgroups="Positive Scenarios"
```

### Generate & open Allure report
```bash
# Option 1: via Maven plugin
mvn allure:serve

# Option 2: via Allure CLI
allure serve target/allure-results
```

---

## Admin Credentials

| Field    | Value    |
|----------|----------|
| Username | admin    |
| Password | password |

---

## Notes

- ChromeDriver is managed automatically by **WebDriverManager** — no manual driver download needed.
- To run headless (e.g. in CI), uncomment the `--headless=new` line in `DriverManager.java`.
- Screenshots on test failure are automatically attached to the Allure report by `AllureScreenshotListener`.
