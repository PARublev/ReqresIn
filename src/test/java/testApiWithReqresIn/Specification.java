package testApiWithReqresIn;

import io.restassured.RestAssured;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.with;

public class Specification {
    protected static final String BASE_URL = "https://reqres.in/";
    private static final String BASE_PATH = "/api/";


    //Создаем настройки для базового запроса
    public static RequestSpecification requestSpecification = with()
            .baseUri("https://reqres.in/")
            .basePath("/api")
            .header("x-api-key", "reqres-free-v1")
            .log().all()
            .contentType(ContentType.JSON);

    public static ResponseSpecification responseSpecОК200() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpecErr400() {
        return new ResponseSpecBuilder()
                .expectStatusCode(400)
                .build();
    }

    public static ResponseSpecification responseSpecUnique(int status) {
        return new ResponseSpecBuilder()
                .expectStatusCode(status)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpec = new ResponseSpecBuilder()
            .expectContentType(ContentType.JSON)
            .build();
}
