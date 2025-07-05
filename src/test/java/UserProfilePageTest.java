import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import io.qameta.allure.Step;
import ru.yandex.practicum.RegistrationPage;
import ru.yandex.practicum.UserDeletionPage;
import ru.yandex.practicum.UserProfilePage;

import java.util.Map;

public class UserProfilePageTest {

    private String authToken;

    private final UserProfilePage userProfilePage = new UserProfilePage();
    private final RegistrationPage registrationPage = new RegistrationPage();
    private final UserDeletionPage deletionPage = new UserDeletionPage();


    private String email;
    private final String password = "Password123!";
    private final String name = "TestUser";
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
    @DisplayName("Получение данных пользователя с авторизацией")
    @Step("Проверка получения данных")
    public void testGetUserInfo() {
        Response response = userProfilePage.getUser(authToken);
        assertTrue(userProfilePage.isGetUserSuccess(response), "Получение данных не прошло успешно");
    }

    @Test
    @DisplayName("Обновление данных пользователя и пароля с авторизацией")
    void shouldUpdateUserDataAndPassword() {
        performUserUpdate();
    }

    @Step("Обновление данных пользователя с новым именем, email и паролем")
    @DisplayName("Обновление пользователя с новым паролем")
    public void performUserUpdate() {

        String newName = "Обновленное имя";
        String newEmail = generateUniqueEmail();;

        String newPassword = "Pass123";

        Response response = userProfilePage.updateUser(authToken, Map.of(
                "name", newName,
                "email", newEmail,

                "newPassword", newPassword
        ));

        // Проверка
        assertEquals(200, response.statusCode(), "Ответ от API должен быть 200");
        String returnedName = response.jsonPath().getString("user.name");
        String returnedEmail = response.jsonPath().getString("user.email");

        assertEquals(newName, returnedName, "Имя должно быть обновлено");
        assertEquals(newEmail, returnedEmail, "Email должен быть обновлен");
    }

    @Test
    @DisplayName("Попытка обновления данных без авторизации")
    public void shouldNotUpdateWithoutAuth() {
        performUpdateWithoutAuth();
    }

    @Step("Проверка, что обновление без токена запрещено")
    public void performUpdateWithoutAuth() {

        Response response = userProfilePage
                .updateUserWithoutAuth(Map.of("name", "test"));

        // Проверка статус-кода
        assertEquals(401, response.statusCode(), "Должен возвращаться статус 401 Unauthorized");

        // Проверка сообщения
        String message = response.jsonPath().getString("message");
        assertEquals("You should be authorised", message, "Сообщение должно быть о необходимости авторизации");
    }


    @Step("Генерировать уникальный email")
    private String generateUniqueEmail() {
        return "test" + System.currentTimeMillis() + "@mail.com";
    }
}