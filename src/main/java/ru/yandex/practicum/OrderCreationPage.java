package ru.yandex.practicum;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;


public class OrderCreationPage extends BaseHttpClient {

    private static final String CREATE_ORDER_PATH = "orders";
    private static final String GET_INGREDIENTS_PATH = "ingredients";
    public Response getIngredients() {
        return doGetRequest(GET_INGREDIENTS_PATH);
    }
    public List<String> getAllIngredientIds() {
        Response response = getIngredients(); // запрос данных ингредиентов
        return response.jsonPath().getList("data._id"); // получаем список _id
    }

    // Не авторизованный пользователь
    public Response createOrderWithIngredients(List<String> ingredientIds) {
        // Создаём тело запроса в виде JSON-объекта
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);

        return given()
                .spec(baseRequestSpec)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER_PATH)
                .then()
                .extract()
                .response();
    }
    // Автоизованный пользователь
    public Response createOrderWithIngredientsAuth(List<String> ingredientIds, String token) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("ingredients", ingredientIds);

        return given()
                .spec(baseRequestSpec)
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER_PATH)
                .then()
                .extract()
                .response();
    }



}
