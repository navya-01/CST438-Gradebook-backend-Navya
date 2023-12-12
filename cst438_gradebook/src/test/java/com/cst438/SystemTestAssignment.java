package com.cst438;

import static org.assertj.core.api.Assertions.*;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SystemTestAssignment {

    public static final String CHROME_DRIVER_FILE_LOCATION = "C:/Users/navya/Downloads/chromedriver-win64/chromedriver-win64/chromedriver.exe";
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000; // 1 second.

    @Test
    public void addAssignmentTest() throws Exception {
        WebDriver driver = getWebDriver();

        try {
            // Navigate to the page where you add assignments
            driver.findElement(By.id("addbutton")).click();
            Thread.sleep(SLEEP_DURATION);

            // Fill out the form to add a new assignment
            WebElement assignmentNameInput = driver.findElement(By.id("assignmentname"));
            assignmentNameInput.sendKeys("SQL Commands");
            Thread.sleep(SLEEP_DURATION);

            WebElement assignmentCourseInput = driver.findElement(By.id("coursename"));
            assignmentCourseInput.sendKeys("CST 363 - Introduction to Database Systems");
            Thread.sleep(SLEEP_DURATION);

            WebElement assignmentIDInput = driver.findElement(By.id("courseID"));
            assignmentIDInput.sendKeys("31045");
            Thread.sleep(SLEEP_DURATION);

            WebElement assignmentDueDateInput = driver.findElement(By.id("assignmentdue"));
            assignmentDueDateInput.sendKeys("2023-12-31");
            Thread.sleep(SLEEP_DURATION);

            // Submit the form
            driver.findElement(By.id("saveassignment")).click();
            Thread.sleep(SLEEP_DURATION);

            // Check if the new assignment is added successfully
            WebElement addedAssignment = driver.findElement(By.xpath("//td[text()='SQL Commands']"));
            assertThat(addedAssignment).withFailMessage("The new assignment was not added.").isNotNull();
            Thread.sleep(SLEEP_DURATION);

        } finally {
            driver.quit();
            Thread.sleep(SLEEP_DURATION);
        }
    }

    @Test
    public void updateAssignmentTest() throws Exception {
        WebDriver driver = getWebDriver();

        try {
            // Navigate to the page where you view or edit assignments
            driver.findElement(By.id("editassignment")).click();
            Thread.sleep(SLEEP_DURATION);

            // Find and click on the assignment you want to update
            WebElement assignmentToUpdate = driver.findElement(By.xpath("//td[text()='db design']"));
            Thread.sleep(SLEEP_DURATION);

            // Update the assignment details
            WebElement assignmentNameInput = driver.findElement(By.id("assignmentname1"));
            assignmentNameInput.clear();
            assignmentNameInput.sendKeys("SQL Table");
            Thread.sleep(SLEEP_DURATION);

            WebElement assignmentCourseInput = driver.findElement(By.id("coursename1"));
            assignmentCourseInput.clear();
            assignmentCourseInput.sendKeys("CST 363 - Introduction to Database Systems");
            Thread.sleep(SLEEP_DURATION);

            WebElement assignmentDueDateInput = driver.findElement(By.id("assignmentdue1"));
            assignmentDueDateInput.clear();
            assignmentDueDateInput.sendKeys("2024-01-15");
            Thread.sleep(SLEEP_DURATION);

            // Submit the form to save changes
            driver.findElement(By.id("updatebutton")).click();
            Thread.sleep(SLEEP_DURATION);

            // Check if the assignment is updated successfully
            WebElement updatedAssignment = driver.findElement(By.xpath("//td[text()='SQL Table']"));
            assertThat(updatedAssignment).withFailMessage("The assignment was not updated.").isNotNull();
            Thread.sleep(SLEEP_DURATION);

        } finally {
            driver.quit();
            Thread.sleep(SLEEP_DURATION);
        }
    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        WebDriver driver = getWebDriver();

        try {

            driver.findElement(By.id("deletebutton")).click();
            Thread.sleep(SLEEP_DURATION);

            // Check if the assignment is deleted successfully
            WebElement deletedAssignment = driver.findElement(By.xpath("//td[text()='SQL Table']"));
            assertThat(deletedAssignment).withFailMessage("The assignment was not deleted.").isNotNull();

        } finally {
            driver.quit();
        }
    }


    private WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get(URL);
        try {
            Thread.sleep(SLEEP_DURATION);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return driver;
    }
}
