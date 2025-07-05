import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.RegistrationPage;
import ru.yandex.practicum.UserDeletionPage;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationTests {

    private String email ;
    private final String password = "TestPassword123";
    private final String name = "TestUser";

    private final RegistrationPage registrationPage = new RegistrationPage();
    private final UserDeletionPage deletionPage = new UserDeletionPage();

    private String authToken;


    @BeforeEach
    @Step("Создаём уникального тестового пользователя")
    public void setup() {
        email = generateUniqueEmail();
        RegistrationPage.RegistrationResult result = registrationPage.registerUser(email, password, name);
        assertTrue(result.success, "Не удалось зарегистрировать пользователя");
        authToken = result.accessToken;

    }

    @AfterEach
    @Step("Удаляем тестового пользователя")
    public void cleanup() {
        if (authToken != null) {
            Response deleteResponse = deletionPage.deleteUser(authToken);
            assertTrue(deletionPage.isDeletionSuccessful(deleteResponse), "Удаление пользователя не прошло успешно");
        }
    }

    @Test
    @DisplayName("Пользователь успешно создается и регистрируется")
    @Step("Проверка успешной регистрации пользователя")
    public void testRegisterUserSuccess() {
        assertNotNull(email);
        assertTrue(email.contains("@"));
    }

    @Test
    @DisplayName("Попытка регистрации с уже существующим email")
    @Step("Регистрация с существующим email")
    public void testRegisterExistingUser() {
        boolean exists = registrationPage.isUserExists(email, password, name);
        assertTrue(exists, "Пользователь существовует");
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Step("Создаём пользователя без имени")
    public void testCreateUserWithoutName() {
        Response response = registrationPage.createUserWithoutName(email, password);
        assertTrue(registrationPage.responseIndicatesMissingFields(response), "Должна быть ошибка о пропущенных полях");
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Step("Создаём пользователя без email")
    public void testCreateUserWithoutEmail() {
        Response response = registrationPage.createUserWithoutEmail(password, name);
        assertTrue(registrationPage.responseIndicatesMissingFields(response), "Должна быть ошибка о пропущенных полях");
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Step("Создаём пользователя без пароля")
    public void testCreateUserWithoutPassword() {
        Response response = registrationPage.createUserWithoutPassword(email, name);
        assertTrue(registrationPage.responseIndicatesMissingFields(response), "Должна быть ошибка о пропущенных полях");
    }

    // Генерация уникального email
    @Step("Генерировать уникальный email")
    private String generateUniqueEmail() {
        return "test" + System.currentTimeMillis() + "@mail.com";
    }


    }
