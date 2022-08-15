package com.example.demo.windsurf;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Spot {

    private final int key;//SpotKey
    String name;
    private final LocalDateTime time;
    private final double windSpeed; //in m/s
    private final int windDirection; //in Grad
    private final double temperature; //in Celsius
    private final boolean thunderstorm;
    private final boolean Day;


    public Spot(int key, String name, LocalDateTime time, double windSpeed, int windDirection, double temperature,
                boolean thunderstorm, boolean Day) {
        this.key = key;
        this.name = name;
        this.time = time;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.temperature = temperature;
        this.thunderstorm = thunderstorm;
        this.Day = Day;
    }


    public boolean checkConditions(SurfConditions cond) {
        return (this.getWindSpeed() >= cond.getMinWind()
                && this.getWindSpeed() <= cond.getMaxWind()
                && this.getTemperatur() >= cond.getTemperature()
                && !this.isThunderstorm()
                && this.isDay());
    }


    public int getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public double getTemperatur() {
        return temperature;
    }

    public boolean isThunderstorm() {
        return thunderstorm;
    }

    public boolean isDay() {
        return Day;
    }

    public String toString() {
        return String.format("%s %s %.2fm/s %.2fkm/h %.2fBft    %s     %.0f°C%n",
                this.name, beautifulDateForPrinting(this.getTime()), this.getWindSpeed(), msInKmh(this.getWindSpeed()), msInBft(this.getWindSpeed()),
                windDegreeForPrinting(this.getWindDirection()), this.getTemperatur());
    }

    protected static String beautifulDateForPrinting(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM' at 'kk' o`clock'");
        return time.format(formatter);
    }

    protected static String windDegreeForPrinting(int degree) {
        if (degree < 23 || degree >= 338) return "north    ";
        if (degree < 68) return "north-east"; //45
        if (degree < 113) return "east      "; //90
        if (degree < 158) return "south-east  ";//135
        if (degree < 203) return "south     ";//180
        if (degree < 248) return "south-west"; //225
        if (degree < 293) return "west     "; //270
        return "north-west"; //315
    }

    protected static double msInKmh(double mPerS) {
        return mPerS * 3.6;
    }

    protected static double msInBft(double mPerS) {
        return (Math.pow((mPerS / 0.836), (double) 2 / 3));
    }

    protected static double kmhInMs(double mPerS) {
        return mPerS / 3.6;
    }

    protected static double bftInMs(double bft) {
        return Math.pow(bft, (double) 3/2) * 0.836;
    }
}
