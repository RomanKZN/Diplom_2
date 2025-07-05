package ru.yandex.practicum;

import io.restassured.response.Response;

public class RegistrationPage extends BaseHttpClient {

    private static final String REGISTER_PATH = "auth/register";

    // Регистрация пользователя и получение данных о результате.

    public RegistrationResult registerUser(String email, String password, String name) {
        Response response = createUser(email, password, name);

        if (response.statusCode() == 200) {
            boolean success = response.jsonPath().getBoolean("success");
            if (success) {
                String accessToken = response.jsonPath().getString("accessToken");
                String refreshToken = response.jsonPath().getString("refreshToken");
                String userEmail = response.jsonPath().getString("user.email");
                String userName = response.jsonPath().getString("user.name");
                return new RegistrationResult(true, accessToken, refreshToken, userEmail, userName);
            }
        }


        return new RegistrationResult(false, null, null, null, null);
    }

    // Класс-обертка для результата регистрации
    public static class RegistrationResult {
        public boolean success;
        public String accessToken;
        public String refreshToken;
        public String email;
        public String name;

        public RegistrationResult(boolean success, String accessToken, String refreshToken, String email, String name) {
            this.success = success;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.email = email;
            this.name = name;
        }
    }

    // Создание нового пользователя.

    public Response createUser(String email, String password, String name) {
        RegistrationRequest requestBody = new RegistrationRequest(email, password, name);
        return doPostRequest(REGISTER_PATH, requestBody);
    }

    //Создание пользователя без email.

    public Response createUserWithoutEmail(String password, String name) {
        RegistrationRequest requestBody = new RegistrationRequest(null, password, name);
        return doPostRequest(REGISTER_PATH, requestBody);
    }

    //Создание пользователя без пароля.

    public Response createUserWithoutPassword(String email, String name) {
        RegistrationRequest requestBody = new RegistrationRequest(email, null, name);
        return doPostRequest(REGISTER_PATH, requestBody);
    }

    //Создание пользователя без имени.

    public Response createUserWithoutName(String email, String password) {
        RegistrationRequest requestBody = new RegistrationRequest(email, password, null);
        return doPostRequest(REGISTER_PATH, requestBody);
    }

    // Метод для проверки что ответ содержит ошибку о пропущенных обязательных полях.

    public boolean responseIndicatesMissingFields(Response response) {
        if (response.statusCode() == 403) {
            String message = response.jsonPath().getString("message");
            if (message != null && message.contains("required fields")) {
                return true;
            }
        }
        return false;
    }

    // Метод для проверки существующего пользователя с заданными email, паролем и именем.

    public boolean isUserExists(String email, String password, String name) {
        Response response = createUser(email, password, name);

        if (response.statusCode() == 403) {
            String message = response.jsonPath().getString("message");
            if ("User already exists".equalsIgnoreCase(message)) {
                return true;
            }
        }

        return false;
    }

    private static class RegistrationRequest {
        private String email;
        private String password;
        private String name;

        public RegistrationRequest(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getName() { return name; }
    }
}