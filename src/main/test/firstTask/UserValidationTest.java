package firstTask;

import io.restassured.RestAssured;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserValidationTest extends BaseTest{

    @Test
    public void validateEmailsOnPage() {

        RestAssured
                .given()
                    .spec(requestSpec)
                    .queryParam("page", 2)
                .when()
                    .get("/users")
                .then()
                    .statusCode(200)
                    .body("data.email", everyItem(endsWith("@reqres.in")));
    }
}
