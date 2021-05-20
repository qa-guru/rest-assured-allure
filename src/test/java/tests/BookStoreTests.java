package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.Matchers.*;

public class BookStoreTests {
    @Test
    void noLogsTest() {
        given()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withAllLogsTest() {
        given()
                .log().all()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().all()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomeLogsTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomeLogsPostTest() {
        Map<String, String> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "asdsad#frew_DFS2");

        given()
                .contentType(JSON)
                .body(data.toString())
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withAllureListenerTest() {
        given()
                .filter(new AllureRestAssured())
                .contentType(JSON)
                .body("{ \"userName\": \"alex\", \"password\": \"asdsad#frew_DFS2\" }")
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }


    @Test
    void withCustomFilterTest() {
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(JSON)
                .body("{ \"userName\": \"alex\", \"password\": \"asdsad#frew_DFS2\" }")
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }


}
