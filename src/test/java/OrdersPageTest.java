import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.OrderCreationPage;
import ru.yandex.practicum.OrdersPage;
import ru.yandex.practicum.RegistrationPage;
import ru.yandex.practicum.UserDeletionPage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import static org.junit.jupiter.api.Assertions.*;


public class OrdersPageTest {


    private String email;
    private final String password = "Password123!";
    private final String name = "TestUser";
    private String authToken;
    private final OrderCreationPage orderPage = new OrderCreationPage();
    private final OrdersPage ordersPage = new OrdersPage();
    private final RegistrationPage registrationPage = new RegistrationPage();
    private final UserDeletionPage deletionPage = new UserDeletionPage();


    @BeforeEach
    @Step("Создаём уникального тестового пользователя")
    public void setup() {
        email = generateUniqueEmail();
        // создаем пользователя
        RegistrationPage.RegistrationResult result = registrationPage.registerUser(email, password, name);
        assertTrue(result.success, "Не удалось зарегистрировать пользователя");
        authToken = result.accessToken;
    }

    @AfterEach
    @Step("Удаляем тестового пользователя")
    public void cleanup() {
        if (authToken != null) {
            Response response = deletionPage.deleteUser(authToken);
            assertTrue(deletionPage.isDeletionSuccessful(response), "Удаление неуспешное");
        }
    }


    @Test
    @DisplayName("Проверка тела ответа")
    @Step("Проверяем что в ответе вернулись все поля")
    public void testGetAllOrdersBody() {
        Response response = ordersPage.getAllOrders();
        ordersPage.verifyGetAllOrdersResponse(response);
        assertTrue(ordersPage.isSuccess(response), "Ответ не успешен или структура неправильная");
        assertNotNull(ordersPage.getOrders(response), "Заказы не получены");
        assertTrue(ordersPage.getTotal(response) >= 0, "Некорректное число total");
        assertTrue(ordersPage.getTotalToday(response) >= 0, "Некорректное число totalToday");
    }




    @Test
    @DisplayName("Проверка получение заказов авторизованным пользователем")
    @Step("Предварительно создали несколько заказов, получили список и проверели с тем что создали")
    public void testGetUserOrdersReturnsCreatedOrders() {
        String token = authToken;
        // Получаем все ингредиенты
        List<String> ingredientIds = orderPage.getAllIngredientIds();

        // Создаем 3 заказа с помощью цикла
        int numberOfOrdersToCreate = 3;
        List<Integer> createdOrderNumbers = new ArrayList<>();
        for (int i = 0; i < numberOfOrdersToCreate; i++) {
            Response createResp = orderPage.createOrderWithIngredientsAuth(ingredientIds, token);
            createResp.then().statusCode(200);
            int orderNumber = createResp.jsonPath().getInt("order.number");
            createdOrderNumbers.add(orderNumber);
        }

        // Запрашиваем заказы пользователя
        Response response = ordersPage.getUserOrders(token);
        assertEquals(200, response.statusCode(), "Ошибка при получении заказов пользователя");

        // Проверяем success
        assertTrue(ordersPage.isSuccess(response), "Ответ содержит success=false");

        // Получаем список заказов
        List<?> orders = (List<?>) ordersPage.getOrders(response);
        assertNotNull(orders, "Список заказов пустой");
        assertTrue(orders.size() >= numberOfOrdersToCreate, "Недоступно ожидаемое количество заказов");

        // Проверка наличия заказов с номерами наших созданных
        List<Integer> orderNumbersFromResponse = new ArrayList<>();
        for (Object orderObj : orders) {
            Map<?, ?> orderMap = (Map<?, ?>) orderObj;
            Integer num = (Integer) orderMap.get("number");
            orderNumbersFromResponse.add(num);
        }

        for (Integer createdNumber : createdOrderNumbers) {
            assertTrue(orderNumbersFromResponse.contains(createdNumber),
                    "Заказ с номером " + createdNumber + " не найден среди заказов пользователя");
        }
    }



    @Test
    @DisplayName("Проверка получение заказов не авторизованным пользователем")
    @Step("Не успешное получение заказа без токена авторизации")
    public void testGetUserOrdersWithoutAuth() {

        // Запрашиваем заказы пользователя
        Response response = ordersPage.getUserOrders("");
        assertEquals(401, response.statusCode(), "Ожидался статус 401 без авторизации");
        assertFalse(ordersPage.isSuccess(response), "Статус успеха при отсутствии авторизации");
        String msg = ordersPage.getAuthErrorMessage(response);
        assertEquals("You should be authorised", msg);
    }

    // Вспомогательные методы


    @Step("Генерируем уникальный email")
    private String generateUniqueEmail() {
        return "user" + System.currentTimeMillis() + "@test.com";
    }
}