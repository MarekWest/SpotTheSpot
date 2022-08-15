package com.example.demo.windsurf;

public class SurfConditions {
    double minWind;
    double maxWind;
    double temperature;
    WindUnit windUnit;


    public SurfConditions(double minWind, double maxWind, double temperature, WindUnit windUnit) {
        switch (windUnit) {
            case M_PER_S:
                this.minWind = minWind;
                this.maxWind = maxWind;
                break;
            case KM_PER_H:
                this.minWind = Spot.kmhInMs(minWind);
                this.maxWind = Spot.kmhInMs(maxWind);
                break;
            case BFT:
                this.minWind = Spot.bftInMs(minWind);
                this.maxWind = Spot.bftInMs(maxWind);
                break;
        }
        this.temperature = temperature;
        this.windUnit = windUnit;
    }

    public double getMinWind() {
        return minWind;
    }

    public double getMaxWind() {
        return maxWind;
    }

    public double getTemperature() {
        return temperature;
    }

    public WindUnit getWindUnit() {
        return windUnit;
    }
}
