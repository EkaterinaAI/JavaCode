package secondTask.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    WebDriver driver;

    //Столбец с названием товара в корзине
    @FindBy(xpath = "//tr/td[2]")
    private List<WebElement> productNamesInCart;

    //Столбец с ценой товара в корзине
    @FindBy(xpath = "//tr/td[3]")
    private List<WebElement> productPricesInCart;

    //Поле с общей суммой заказа
    @FindBy(xpath = "//h3[@id='totalp']")
    private WebElement totalPriceElement;

    //Кнопка Place Order
    @FindBy(xpath = "//button[contains(text(),'Place Order')]")
    private WebElement placeOrderButton;

    //Кнопка для итогового подтверждения заказа
    @FindBy(xpath = "//button[contains(text(),'Purchase')]")
    private WebElement purchaseButton;

    //Ввод данных для заказа
    @FindBy(xpath = "//input[@id='name']")
    private WebElement nameField;

    @FindBy(xpath = "//input[@id='country']")
    private WebElement countryField;

    @FindBy(xpath = "//input[@id='city']")
    private WebElement cityField;

    @FindBy(xpath = "//input[@id='card']")
    private WebElement cardField;

    @FindBy(xpath = "//input[@id='month']")
    private WebElement monthField;

    @FindBy(xpath = "//input[@id='year']")
    private WebElement yearField;

    @FindBy(xpath = "//p[contains(@class,'lead text-muted')]")
    private WebElement orderDateElement;

    // Элемент, в котором выводится информация о заказе (после нажатия Purchase)
    @FindBy(xpath = "//p[@class='lead text-muted ']")
    private WebElement confirmationText;

    // Кнопка OK в модальном окне после оформления заказа
    @FindBy(xpath = "//button[contains(text(),'OK')]")
    private WebElement okButton;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public String getProductPriceFromCart(String productName) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//tr/td[2]")));
        //System.out.println("Продукты в корзине: " + productNamesInCart.stream().map(WebElement::getText).collect(Collectors.toList()));

        for (int i = 0; i < productNamesInCart.size(); i++) {
            String actualProductName = productNamesInCart.get(i).getText().trim(); // Удаляем лишние пробелы
            if (actualProductName.equalsIgnoreCase(productName.trim())) { // Игнорируем регистр и лишние пробелы
                return productPricesInCart.get(i).getText().trim(); // Возвращаем цену без лишних пробелов
            }
        }
        throw new RuntimeException("Продукт с именем \"" + productName + "\" не найден в корзине.");
    }

    public int getTotalPrice() {
        String totalPrice = totalPriceElement.getText().trim();
        return Integer.parseInt(totalPrice.replaceAll("[^\\d]", ""));
    }

    public String getOrder(String name, String country, String city,
                           String card, String month, String year) {
        placeOrderButton.click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(nameField));
        nameField.sendKeys(name);
        countryField.sendKeys(country);
        cityField.sendKeys(city);
        cardField.sendKeys(card);
        monthField.sendKeys(month);
        yearField.sendKeys(year);

        purchaseButton.click();

        // Дожидаемся появления итогового сообщения с Id, Amount и т.п.
        wait.until(ExpectedConditions.visibilityOf(confirmationText));

        String result = confirmationText.getText();

        // Парсим текст, чтобы найти строку с датой
        String[] lines = result.split("\n");
        String dateLine = null;
        for (String line : lines) {
            if (line.trim().startsWith("Date:")) {
                dateLine = line.trim();
                break;
            }
        }
        if (dateLine == null) {
            throw new RuntimeException("В подтверждении заказа не найдена строка, начинающаяся с 'Date:'");
        }

        // Извлекаем дату из строки вида "Date: 2025-01-22"
        String dateInConfirmation = dateLine.replace("Date:", "").replaceAll("/", "-").trim();
        // Формируем текущую системную дату в таком же формате
        String currentSystemDate = java.time.LocalDate.now().toString();

        if (!currentSystemDate.equals(dateInConfirmation)) {
            throw new RuntimeException("Дата в подтверждении заказа (" + dateInConfirmation +
                    ") не совпадает с датой в системе (" + currentSystemDate + ")");
        }

        okButton.click();
        return result;
    }
}