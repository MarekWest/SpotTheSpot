package com.example.demo.windsurf;

public class WrongKeyException extends Exception {
    public WrongKeyException() {
        super("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.");
    }
}
