package com.example.demo.windsurf;

public class SpotNotFoundException extends Exception {

        public SpotNotFoundException(String location) {
            super("Der folgende Ort ist nicht verfügbar: " + location);
        }


}
