package ru.yandex.practicum;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class OrdersPage extends BaseHttpClient {

    private static final String GET_ALL_ORDERS_PATH = "orders/all";
    private static final String GET_USER_ORDERS_PATH = "orders";

    //Получить все последние заказы.

    public Response getAllOrders() {
        return doGetRequest(GET_ALL_ORDERS_PATH);
    }

    //Получить заказы конкретного пользователя с авторизацией.

    public Response getUserOrders(String token) {
        return given()
                .header("authorization", token)
                .when()
                .get(URL.getHost() +GET_USER_ORDERS_PATH);
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

    public void verifyGetAllOrdersResponse(Response response) {
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("orders", notNullValue());
        response.then().body("orders.size()", greaterThan(0));
        response.then().body("orders[0].ingredients", notNullValue());
        response.then().body("orders[0]._id", notNullValue());
        response.then().body("orders[0].status", equalTo("done"));
        response.then().body("orders[0].number", notNullValue());
        response.then().body("orders[0].createdAt", notNullValue());
        response.then().body("orders[0].updatedAt", notNullValue());
    }
}