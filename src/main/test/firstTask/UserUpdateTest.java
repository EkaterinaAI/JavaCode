package firstTask;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserUpdateTest extends BaseTest {
    @Test
    public void updateUserInfoAndCompareUpdateDate() {
        String requestBody = "{ \"name\": \"morpheus\", \"job\": \"zion resident\" }";

        Response response = RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .patch("/users/2")
                .then()
                    .spec(patchResponseSpec)
                    .body("name", equalTo("morpheus"))
                    .body("job", equalTo("zion resident"))
                    .extract()
                    .response();

        String updatedAt = response.jsonPath().getString("updatedAt");
        LocalDateTime updatedDateTime = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_DATE_TIME);
        LocalDateTime currentDateTime = LocalDateTime.now();

        long differenceInSeconds = Math.abs(Duration.between(updatedDateTime, currentDateTime).getSeconds());
        assertTrue(differenceInSeconds <= 10, "Дата обновления слишком отличается от текущей даты");
    }
}
