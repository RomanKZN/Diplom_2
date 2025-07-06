package ru.yandex.practicum;

import io.restassured.response.Response;


import static io.restassured.RestAssured.given;


public class UserDeletionPage extends BaseHttpClient {

    private static final String DELETE_USER_PATH = "auth/user";



    // Удаление пользователя.


    public Response deleteUser(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Токен не установлен");
        }
        return given()
                .header("Authorization", token)
                .when()
                .delete(URL.getHost() + DELETE_USER_PATH);
    }

    public boolean isDeletionSuccessful(Response response) {
        int code = response.statusCode();
        return code == 200 || code == 202;
    }

    // Получить сообщение об ошибке.

    public String getErrorMessage(Response response) {
        if (response.statusCode() == 401 || response.statusCode() == 403) {
            return response.jsonPath().getString("message");
        }
        return null;
    }
}