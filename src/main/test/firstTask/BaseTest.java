package firstTask;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class BaseTest {
    protected static final String BASE_URL = "https://reqres.in/api";

    protected static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri(BASE_URL)
            .setContentType("application/json")
            .build();

    protected static ResponseSpecification successResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200) // Успешный статус-код
            .build();

    protected static ResponseSpecification badRequestResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(400) // Ошибка клиента
            .expectBody("error", equalTo("Missing password")) // Проверка ошибки
            .build();

    protected static ResponseSpecification noContentResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(204)
            .build();

    protected static ResponseSpecification patchResponseSpec = new ResponseSpecBuilder()
            .expectStatusCode(200) // Ожидаем успешный статус-код
            .expectBody("updatedAt", notNullValue()) // Поле updatedAt должно присутствовать
            .build();
}