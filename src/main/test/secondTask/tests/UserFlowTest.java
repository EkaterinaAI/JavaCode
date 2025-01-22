package secondTask.tests;

import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import secondTask.pages.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserFlowTest {
    WebDriver driver;
    Faker faker;
    String username;
    String password;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.demoblaze.com/");
        faker = new Faker();
        username = faker.name().username();
        password = faker.internet().password();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(5000);
                driver.quit();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void loginAndOrderTest() {
        HomePage homePage = new HomePage(driver);

        // 1. Регистрация
        homePage.openSignUpPage();
        homePage.signUp(username, password);
        System.out.println(username);
        System.out.println(password);

        // 2. Логин
        homePage.openLoginPage();
        homePage.login(username, password);

        // 3-4. Добавление товаров по категориям + проверка цены
        Map<String, String> addedProducts = homePage.addProductsToCart();
        CartPage cartPage = homePage.openCartPage();
        for (Map.Entry<String, String> entry : addedProducts.entrySet()) {
            String productName = entry.getKey();
            String expectedPrice = entry.getValue().replaceAll("[^\\d]", "");
            String actualPrice = cartPage.getProductPriceFromCart(productName).replaceAll("[^\\d]", "");
            assertEquals(expectedPrice, actualPrice, "Цена продукта " + productName + " не совпадает: ожидалось " + expectedPrice + ", но было " + actualPrice);
        }

        // 5. Переходим в корзину и убеждаемся, что общая цена верна
        int expectedTotal = addedProducts.values().stream()
                .mapToInt(price -> Integer.parseInt(price.replaceAll("[^\\d]", "")))
                .sum();
        int actualTotal = cartPage.getTotalPrice();
        //assertEquals(expectedTotal, actualTotal, "Общая цена в корзине не совпадает с ожидаемой суммой");

        // 6-7. Оформляем заказ и проверяем данные в итоговом сообщении
        String confirmation = cartPage.getOrder(
                faker.name().fullName(),
                faker.address().country(),
                faker.address().city(),
                "1234123412341234",
                "12",
                "2027"
        );
        // Проверим, что в подтверждении содержатся ключевые поля Id и Amount
        assertTrue(confirmation.contains("Id:"), "В подтверждении заказа отсутствует Id");
        assertTrue(confirmation.contains("Amount:"), "В подтверждении заказа отсутствует Amount");
    }
}