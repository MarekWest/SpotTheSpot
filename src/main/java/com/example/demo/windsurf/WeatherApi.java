package com.example.demo.windsurf;

import net.bytebuddy.implementation.bytecode.Throw;
import org.json.JSONObject;


import java.io.*;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
public class WeatherApi {

    int listIndex; //Zeitstempel der Api

    public WeatherApi() {
        this.listIndex = 0;
    }

    public void increaseListIndex() {
        this.listIndex = this.listIndex + 1;
    }

    public static List<Spot> getSpots(String[] mySpots, int listLength) throws SpotNotFoundException, IOException {  //for SpotAnalyse
        ArrayList<Spot> spotList = new ArrayList<>();
        for (String spotLocation : mySpots) {
                    getSpot(spotList, spotLocation, listLength);
        }
        return spotList;
    }

    private static void getSpot(List<Spot> spotTimes, String spotName,int listLength) throws SpotNotFoundException, IOException {  //for SpotAnalyse
        WeatherApi weatherApi = new WeatherApi();
        while(listLength >= 0) {
            Spot spot = weatherApi.sendHttpRequest(spotName);
            if(spot == null) {
                throw new SpotNotFoundException(spotName);
            }
            spotTimes.add(spot);
            weatherApi.increaseListIndex();
            listLength -= 1;
        }
    }

    private Spot sendHttpRequest(String location) throws IOException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(getUrlFor(location))).build();
        Spot spot = client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(loc-> {
                        try {
                            return this.parseToSpot(loc);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .join();
        return spot;
    }
    private static String getApiKey() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/main/resources/myConfigs.properties");
        Properties properties = new Properties();
        properties.load(fileInputStream);
        return properties.getProperty("apiKey");
    }

    private static String getUrlFor(String location) throws IOException {
        return "https://api.openweathermap.org/data/2.5/forecast?q=" + location + "&units=metric&appid=" + getApiKey();
    }
    private Spot parseToSpot(String responseBody) throws WrongKeyException {
        JSONObject report = new JSONObject(responseBody);
        if(report.getInt("cod") == (401)) {
            //System.out.println("Invalid API key. Please see http://openweathermap.org/faq#error401 for more info.");
            throw new WrongKeyException();
        }
        else if(!report.getString("cod").equals("200")) {
            return null;
        }

        JSONObject city = report.getJSONObject("city");
        String name = city.getString("name");
        JSONObject listElement = report.getJSONArray("list").getJSONObject(this.listIndex);
        LocalDateTime time = LocalDateTime.parse(listElement.getString("dt_txt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JSONObject wind = listElement.getJSONObject("wind");
        double windSpeed = wind.getDouble("speed");
        int windDirection = wind.getInt("deg");
        double temperature = listElement.getJSONObject("main").getDouble("temp");
        String weather = listElement.getJSONArray("weather").getJSONObject(0).getString("main");
        boolean thunderstorm = weather.equals("Thunderstorm"); //test of lightning etc.
        long sunriseLong = city.getInt("sunrise");
        long sunsetLong = city.getInt("sunset");
        boolean isDay = checkDay(sunriseLong, sunsetLong,time);
        return new Spot(this.listIndex, name, time, windSpeed, windDirection, temperature, thunderstorm, isDay);
    }

    private static boolean checkDay(long sunriseLong, long sunsetLong, LocalDateTime time) {
        LocalDateTime sunrise = LocaleDateTimeOf(sunriseLong);
        LocalDateTime sunset = LocaleDateTimeOf(sunsetLong);
        LocalTime sunriseTime = sunrise.toLocalTime();
        LocalTime sunsetTime = sunset.toLocalTime();
        LocalTime localTime = time.toLocalTime();
        return sunriseTime.isBefore(localTime) && sunsetTime.isAfter(localTime);
    }

    private static LocalDateTime LocaleDateTimeOf(long unixTimeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimeStamp),
                TimeZone.getDefault().toZoneId());
    }

}
