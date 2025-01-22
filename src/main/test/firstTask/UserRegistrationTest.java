package firstTask;

import io.restassured.RestAssured;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class UserRegistrationTest extends BaseTest{

    @Test
    public void successfulRegistrationTest() {
        String requestBody = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\" }";

        RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .post("/register")
                .then()
                    .spec(successResponseSpec)
                    .body("id", notNullValue())
                    .body("token", notNullValue());
    }

    @Test
    public void registrationWithoutPasswordTest() {
        String requestBody = "{ \"email\": \"sydney@fife\" }";

         RestAssured
                .given()
                    .spec(requestSpec)
                    .body(requestBody)
                .when()
                    .post("/register")
                .then()
                    .spec(badRequestResponseSpec);
    }
}
