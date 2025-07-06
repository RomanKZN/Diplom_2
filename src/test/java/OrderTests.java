import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.OrderCreationPage;
import ru.yandex.practicum.RegistrationPage;
import ru.yandex.practicum.UserDeletionPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderTests {

    private String email;
    private final String password = "Password123!";
    private final String name = "TestUser";
    private String authToken;

    private final OrderCreationPage orderPage = new OrderCreationPage();

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
    @DisplayName("Проверка что ответ валиден")
    @Step("Проверка что ответ валиден")
    public void testGetIngredientsResponseIsValid() {

        Response response = orderPage.getIngredients();

        response.then().statusCode(200);

        response.then().body("success", equalTo(true));
        response.then().body("data", notNullValue());
        response.then().body("data.size()", greaterThan(0));


        String firstId = response.jsonPath().getString("data[0]._id");
        assertNotNull(firstId);
    }

    @Test
    @DisplayName("Получение списка ингридиентов")
    @Step("Проверка список возвращается с ингридиентами")

    public void testGetAllIngredientIdsReturnsNonEmptyList() {

        List<String> ingredientIds = orderPage.getAllIngredientIds();


        assertNotNull(ingredientIds);
        assertFalse(ingredientIds.isEmpty());


        for (String id : ingredientIds) {
            assertNotNull(id);
            assertTrue(id instanceof String);

        }
    }

    @Test
    @DisplayName("Создание заказа не авторизованным пользователем")
    @Step("Проверка с рандомными данными")

    public void testCreateOrderWithRandomIngredients() {
        // Получаем все ID ингредиентов
        List<String> allIngredientIds = orderPage.getAllIngredientIds();


        int count = ThreadLocalRandom.current().nextInt(1, allIngredientIds.size() + 3);

        // Перемешиваем список и берем первые 'count' элементов
        List<String> randomIngredientIds = new ArrayList<>(allIngredientIds);
        java.util.Collections.shuffle(randomIngredientIds);
        List<String> selectedIngredientIds = randomIngredientIds.subList(0, count);

        // Создаем заказ с случайными ингредиентами
        Response response = orderPage.createOrderWithIngredients(selectedIngredientIds);

        // Проверяем статус
        response.then().statusCode(200);
        response.then().body("success", equalTo(true));
        response.then().body("order", notNullValue());

    }
    @Test
    @DisplayName("Создание заказа авторизованным пользователем")
    @Step("Проверка с рандомными данными и авторизованным пользователем")

    public void testCreateOrderWithAuth() {

        String token = authToken;

        // Получаем все ингредиенты
        List<String> allIngredientIds = orderPage.getAllIngredientIds();

        // выбираем случайные ингредиенты
        java.util.Collections.shuffle(allIngredientIds);
        int count = Math.min(3, allIngredientIds.size());
        List<String> selectedIds = allIngredientIds.subList(0, count);

        // Создаем заказ с авторизацией
        Response response = orderPage.createOrderWithIngredientsAuth(selectedIds, token);

        // Проверяем статус ответа
        response.then().statusCode(200);

        // Проверяем что успешно создан заказ
        response.then().body("success", equalTo(true));
        response.then().body("order", notNullValue());
        Integer orderNumber = response.jsonPath().getInt("order.number");
        assertNotNull(orderNumber);
    }

    @Test
    @DisplayName("Передача пустого массива ингредиентов")
    @Step("Проверка получении ошибки если массив пустой")

    public void testCreateOrderWithoutIngredients() {

        Response response = orderPage.createOrderWithIngredientsAuth(Collections.emptyList(), authToken);


        response.then().statusCode(400);
        response.then().body("success", equalTo(false));
        response.then().body("message", equalTo("Ingredient ids must be provided"));
    }


    @Test
    @DisplayName("Передача невалидного ID")
    @Step("Проверка получении ошибки если передан невалидный ID")

    public void testCreateOrderWithInvalidIngredientHash() {

        Response response = orderPage.createOrderWithIngredients(Collections.singletonList("invalid_id_123456"));

        response.then().statusCode(500);


    }


    @Step("Генерируем уникальный email")
    private String generateUniqueEmail() {
        return "user" + System.currentTimeMillis() + "@test.com";
    }

}