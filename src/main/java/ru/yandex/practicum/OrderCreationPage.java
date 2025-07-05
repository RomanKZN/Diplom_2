package ru.yandex.practicum;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class OrderCreationPage extends BaseHttpClient {

    private static final String CREATE_ORDER_PATH = "orders";

    // Создание заказа с ингредиентами, без авторизации.

    public Response createOrder(String[] ingredientIds) {
        return doCreateOrder(ingredientIds, null);
    }

    // Создание заказа с ингредиентами и авторизацией.

    public Response createOrder(String[] ingredientIds, String token) {
        return doCreateOrder(ingredientIds, token);
    }

    //Внутренний метод для объединения логики создания заказа.

    private Response doCreateOrder(String[] ingredientIds, String token) {
        // Формируем тело запроса
        OrderRequest body = new OrderRequest(ingredientIds);
        io.restassured.specification.RequestSpecification request = given()
                .body(body);
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.when().post(CREATE_ORDER_PATH);
    }

    // Проверка успешного создания заказа.

    public boolean isOrderCreated(Response response) {
        return response.statusCode() == 200 && response.jsonPath().getBoolean("success");
    }

    // Получение номера заказа.

    public int getOrderNumber(Response response) {
        return response.jsonPath().getInt("order.number");
    }

    // Получение сообщения об ошибке.

    public String getErrorMessage(Response response) {
        if (response.statusCode() == 400 || response.statusCode() == 500) {
            return response.jsonPath().getString("message");
        } else if (response.statusCode() == 403) {
            return response.jsonPath().getString("message");
        }
        return null;
    }


    private static class OrderRequest {
        private String[] ingredients;

        public OrderRequest(String[] ingredients) {
            this.ingredients = ingredients;
        }

        public String[] getIngredients() { return ingredients; }
    }
}
