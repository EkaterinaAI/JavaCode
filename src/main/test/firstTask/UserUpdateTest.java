package firstTask;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUpdateTest extends BaseTest{
    @Test
    public void updateUserInfoAndCompareUpdateDate() {
        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"zion resident\" }";

        Response response = RestAssured
                .given()
                    .spec(requestSpec) // Используем RequestSpecification
                    .body(requestBody) // Передаём тело запроса
                .when()
                    .patch("/users/2") // Путь запроса
                .then()
                    .spec(patchResponseSpec) // Используем ResponseSpecification для проверок
                    .body("name", equalTo("morpheus")) // Проверяем, что имя совпадает
                    .body("job", equalTo("zion resident")) // Проверяем, что должность совпадает
                    .extract()
                    .response();

        // Извлекаем дату обновления из ответа
        String updatedAt = response.jsonPath().getString("updatedAt");
        System.out.println("Дата обновления из API: " + updatedAt);

        // Преобразуем дату из ответа в LocalDateTime
        LocalDateTime updatedDateTime = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME);

        // Получаем текущую дату/время системы
        LocalDateTime currentDateTime = LocalDateTime.now();
        System.out.println("Текущая дата системы: " + currentDateTime);

        // Проверяем, что разница между текущей датой и датой обновления минимальна
        assertTrue(
                Math.abs(currentDateTime.minusSeconds(updatedDateTime.getSecond()).getSecond()) <= 10,
                "Дата обновления слишком отличается от текущей даты"
        );
    }
}
