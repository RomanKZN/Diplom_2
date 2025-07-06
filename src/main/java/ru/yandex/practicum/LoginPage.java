package ru.yandex.practicum;

import io.restassured.response.Response;


public class LoginPage extends BaseHttpClient {

    private static final String LOGIN_PATH = "auth/login";

    //Выполняет логин с заданными email и паролем.

    public Response login(String email, String password) {

        LoginRequest loginRequest = new LoginRequest(email, password);

        return doPostRequest(LOGIN_PATH, loginRequest);
    }

    //Возвращает результат с токенами и данными пользователя при успехе.

    public LoginResult loginAndGetTokens(String email, String password) {
        Response response = login(email, password);
        if (response.statusCode() == 200) {
            boolean success = response.jsonPath().getBoolean("success");
            if (success) {
                String accessToken = response.jsonPath().getString("accessToken");
                String refreshToken = response.jsonPath().getString("refreshToken");
                String userEmail = response.jsonPath().getString("user.email");
                String userName = response.jsonPath().getString("user.name");
                return new LoginResult(true, accessToken, refreshToken, userEmail, userName);
            }
        }
        return new LoginResult(false, null, null, null, null);
    }

    // Обработка ошибок при неправильных данных.

    public String getErrorMessage(Response response) {
        if (response.statusCode() == 401) {
            String message = response.jsonPath().getString("message");
            if (message != null && message.equals("email or password are incorrect")) {

                return message;
            }
        }
        return null;
    }


    private static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }


    public static class LoginResult {
        public boolean success;
        public String accessToken;
        public String refreshToken;
        public String email;
        public String name;

        public LoginResult(boolean success, String accessToken, String refreshToken, String email, String name) {
            this.success = success;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.email = email;
            this.name = name;
        }
    }
}