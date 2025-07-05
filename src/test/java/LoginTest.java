import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.LoginPage;
import ru.yandex.practicum.RegistrationPage;
import ru.yandex.practicum.UserDeletionPage;

import static org.junit.jupiter.api.Assertions.*;



public class LoginTest {

    private String email;
    private final String password = "Password123!";
    private final String name = "TestUser";

    private String authToken;

    private final RegistrationPage registrationPage = new RegistrationPage();
    private final UserDeletionPage deletionPage = new UserDeletionPage();
    private final LoginPage loginPage = new LoginPage();

    @BeforeEach
    @Step("Создаём уникального тестового пользователя")
    public void setup() {
        email = generateUniqueEmail();
        // создаем пользователя
        RegistrationPage.RegistrationResult result = registrationPage.registerUser(email, password, name);
        assertTrue(result.success, "Не удалось зарегистрировать пользователя");
        authToken = result.accessToken; // сохраняем токен для удаления
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
    @DisplayName("Авторизация с существующим пользователем")
    @Step("Логинимся и проверяем получение токена")
    public void testLoginSuccess() {
        LoginPage.LoginResult result = loginPage.loginAndGetTokens(email, password);
        assertTrue(result.success, "Должен быть успешный вход");
        assertNotNull(result.accessToken);

    }

    @Test
    @DisplayName("Авторизация с неправильным паролем")
    @Step("Логинимся с неправильным паролем")
    public void testLoginWrongPassword() {
        String wrongPassword = "WrongPassword!";
        Response response = loginPage.login(email, wrongPassword);
        String errorMsg = loginPage.getErrorMessage(response);
        assertEquals("email or password are incorrect", errorMsg, "Сообщение об ошибке не совпадает");
        assertEquals(401, response.statusCode(), "Код ответа должен быть 401");
    }


    @Test
    @DisplayName("Авторизация с неправильным email")
    @Step("Логинимся с неправильным email")
    public void testLoginWrongEmail() {
        String wrongEmail = "wrong" + email; // например, добавим что-то
        Response response = loginPage.login(wrongEmail, password);
        String errorMsg = loginPage.getErrorMessage(response);
        assertEquals("email or password are incorrect", errorMsg, "Сообщение об ошибке не совпадает");
        assertEquals(401, response.statusCode(), "Код ответа должен быть 401");
    }

    // метод для генерации уникальных email
    @Step("Генерировать уникальный email")
    private String generateUniqueEmail() {
        return "test" + System.currentTimeMillis() + "@mail.com";
    }
}