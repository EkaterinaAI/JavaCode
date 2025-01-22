package secondTask.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage {
    WebDriver driver;

    //ссылка на страницу регистрации
    @FindBy(xpath = "//a[@id='signin2']")
    private WebElement signUpLink;

    //ссылка на страницу авторизации
    @FindBy(xpath = "//a[@id='login2']")
    private WebElement loginLink;

    //
    @FindBy(xpath = "//div[@class='card-block']")
    private List<WebElement> productCards;

    //ссылка на корзину
    @FindBy(xpath = "//a[@id='cartur']")
    private WebElement cartLink;

    //username при регистрации
    @FindBy(xpath = "//input[@id='sign-username']")
    private WebElement usernameFieldSignUp;

    //password при регистрации
    @FindBy(xpath = "//input[@id='sign-password']")
    private WebElement passwordFieldSignUp;

    //кнопка Sign up в окне регистрации
    @FindBy(xpath = "//button[contains(text(),'Sign up')]")
    private WebElement signUpButton;

    //username при авторизации
    @FindBy(xpath = "//input[@id='loginusername']")
    private WebElement usernameFieldLogIn;

    //password при авторизации
    @FindBy(xpath = "//input[@id='loginpassword']")
    private WebElement passwordFieldLogIn;

    //кнопка Log in в окне регистрации
    @FindBy(xpath = "//button[contains(text(),'Log in')]")
    private WebElement loginButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void openSignUpPage() {
        signUpLink.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(usernameFieldSignUp));
    }

    public void signUp(String username, String password) {
        usernameFieldSignUp.sendKeys(username);
        passwordFieldSignUp.sendKeys(password);
        signUpButton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());

        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public void openLoginPage() {
        loginLink.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(usernameFieldLogIn));
    }

    public void login(String username, String password) {
        usernameFieldLogIn.sendKeys(username);
        passwordFieldLogIn.sendKeys(password);
        loginButton.click();
    }

    public Map<String, String> addProductsToCart() {
        Map<String, String> productDetails = new HashMap<>();
        String[] categories = {"Phones", "Laptops", "Monitors"};

        for (String category : categories) {
            boolean success = false;
            int attempts = 0;

            while (!success && attempts < 3) { // Повторяем до 3 раз, если возникает ошибка
                try {
                    // Переход в категорию
                    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                    WebElement categoryLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'" + category + "')]")));
                    categoryLink.click();

                    // Ожидание загрузки продуктов
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='card-block']")));

                    // Заново находим первый продукт и его данные
                    WebElement firstProduct = driver.findElement(By.xpath("(//div[@class='card-block']//h4/a)"));
                    String productName = firstProduct.getText();
                    String productPrice = driver.findElement(By.xpath("(//div[@class='card-block']//h5)")).getText();

                    productDetails.put(productName, productPrice);

                    // Кликаем по ссылке "Add to cart"
                    firstProduct.click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Add to cart')]")));
                    WebElement addToCart = driver.findElement(By.xpath("//a[contains(text(),'Add to cart')]"));
                    addToCart.click();

                    // Обработка модального окна
                    wait.until(ExpectedConditions.alertIsPresent());
                    driver.switchTo().alert().accept();

                    // Возврат на главную страницу
                    driver.get("https://www.demoblaze.com/");

                    success = true; // Завершаем цикл, если всё прошло успешно
                } catch (StaleElementReferenceException e) {
                    attempts++;
                    System.out.println("StaleElementReferenceException пойман. Повторная попытка: " + attempts);
                }
            }

            if (!success) {
                throw new RuntimeException("Не удалось выполнить действие для категории: " + category);
            }
        }

        return productDetails;
    }

    public CartPage openCartPage() {
        cartLink.click();
        return new CartPage(driver);
    }
}