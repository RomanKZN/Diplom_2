package ru.yandex.practicum;

import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserProfilePage extends BaseHttpClient {
    private static final String ENDPOINT = "/auth/user";

    // Метод для получения данных пользователя
    public Response getUser(String token) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", token) // Передача токена
                .get(ENDPOINT)
                .then()
                .extract()
                .response();
    }

    // Метод для обновления данных пользователя
    public Response updateUser(String token, Map<String, String> updates) {
        return given()
                .spec(baseRequestSpec)
                .header("authorization", token) // Передача токена
                .body(updates) // Тело запроса с данными для обновления
                .patch(ENDPOINT)
                .then()
                .extract()
                .response();
    }



    // Метод для проверки успешности получения данных
    public boolean isGetUserSuccess(Response response) {
        return response.getStatusCode() == 200 && response.jsonPath().getBoolean("success");
    }

    // Попытка обновления данных без токена для проверки доступа

    public Response updateUserWithoutAuth(Map<String, String> updates) {
        return given()
                .spec(baseRequestSpec)
                // Не добавляем заголовок authorization
                .body(updates)
                .patch(ENDPOINT)
                .then()
                .extract()
                .response();
    }
}