package firstTask;

import io.restassured.RestAssured;
import org.junit.Test;


public class UserDeletionTest extends BaseTest{
    @Test
    public void deleteSecondUser() {

        RestAssured
                .given()
                    .spec(BaseTest.requestSpec)
                .when()
                    .delete("/users/2")
                .then()
                    .spec(noContentResponseSpec);
    }
}
