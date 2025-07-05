package ru.yandex.practicum;

public class URL {
    private static final String HOST = "https://stellarburgers.nomoreparties.site/api/";
public static String getHost(){
    if (System.getProperty("host") != null){
        return System.getProperty("host");
    } else {
        return HOST;
    }
  }
}
