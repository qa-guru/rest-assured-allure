package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.AuthorizationResponse;
import models.Books;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void withAssertJTest() {
        String response =
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
                        .extract().asString();

        assertThat(response).contains("\"status\":\"Success\"");
        assertThat(response).contains("\"result\":\"User authorized successfully.\"");
    }

    @Test
    void withModelTest() {
        AuthorizationResponse response =
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
                        .extract().as(AuthorizationResponse.class);
        /*
        {
            "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyTmFtZSI6ImFsZXgiLCJwYXNzd29yZCI6ImFzZHNhZCNmcmV3X0RGUzIiLCJpYXQiOjE2MjE1MzQ1NzJ9.IXZNuhKHg901sJXcCf66TKirqqaL0ATJXNRcj_14iDg",
            "expires": "2021-05-27T18:16:12.901Z",
            "status": "Success",
            "result": "User authorized successfully."
        }
         */
        assertThat(response.getStatus()).isEqualTo("Success");
        assertThat(response.getResult()).isEqualTo("User authorized successfully.");
    }

    @Test
    void booksModelTest() {
        Books books =
                given()
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().body()
                        .extract().as(Books.class);
        System.out.println(books);
    }

    @Test
    void booksJsonShemaTest() {
        given()
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .log().body()
                        .body(matchesJsonSchemaInClasspath("jsonshemas/booklist_response.json"));
    }
}
