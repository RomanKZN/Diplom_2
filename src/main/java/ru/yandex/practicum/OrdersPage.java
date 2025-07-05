package ru.yandex.practicum;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;


public class OrdersPage extends BaseHttpClient {

    private static final String GET_ALL_ORDERS_PATH = "orders/all";
    private static final String GET_USER_ORDERS_PATH = "orders";

    //Получить все последние заказы.

    public Response getAllOrders() {
        return given()
                .when()
                .get(GET_ALL_ORDERS_PATH);
    }

    //Получить заказы конкретного пользователя с авторизацией.

    public Response getUserOrders(String token) {
        return given()
                .header("authorization", token)
                .when()
                .get(GET_USER_ORDERS_PATH);
    }

    // Проверить успешный ответ.

    public boolean isSuccess(Response response) {
        return response.statusCode() == 200 && response.jsonPath().getBoolean("success");
    }

    // Получить список заказов.

    public Object getOrders(Response response) {

        return response.jsonPath().getList("orders");
    }

    // Получить общее число заказов.

    public int getTotal(Response response) {
        return response.jsonPath().getInt("total");
    }

    // Получить число заказов за сегодня.

    public int getTotalToday(Response response) {
        return response.jsonPath().getInt("totalToday");
    }

    // Обработка ошибок авторизации.

    public String getAuthErrorMessage(Response response) {
        if (response.statusCode() == 401) {
            return response.jsonPath().getString("message");
        }
        return null;
    }
}