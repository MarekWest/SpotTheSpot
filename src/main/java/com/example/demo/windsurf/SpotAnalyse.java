package com.example.demo.windsurf;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.windsurf.Spot.*;

public class SpotAnalyse {

    public static void main(String[] args) throws SpotNotFoundException {
        if (args.length < 1) {
            System.out.println("Gebe deine(n) gewünschten windsurf.Spot(s) ein: '<ort>,<land> ... ...'");
            return;
        }
        SurfConditions surfConditions = new SurfConditions(4.5, 8, 16); //example
        List<Spot> chosenSpots = chooseSpots(args, surfConditions);
        if (chosenSpots.isEmpty()) { //keine Einträge gefunden
            System.out.printf("Leider sind keine Einträge für deine Bedingungen vorhanden.%n" +
                    "Tipp: Ergänze weitere Spots oder lockere deine Bedingungen.");
            System.exit(0);
        } else {
            List<String> recommendedSpots = findRecommendedSpot(chosenSpots);
            sendAlarm(chosenSpots, recommendedSpots);
        }
    }

    public static List<Spot> chooseSpots(String[] args, SurfConditions surfConditions) throws SpotNotFoundException {
        List<Spot> spotsInfo;
        try {
            spotsInfo = WeatherApi.getSpots(args, 39);// mehr als 39listLength nicht erlaubt
        } catch (SpotNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return spotsInfo.stream()
                .filter(spot -> spot.checkConditions(surfConditions)).toList();
    }

    public static List<String> findRecommendedSpot(List<Spot> chosenSpot) {
        List<String> recommendedSpotPrint = new ArrayList<>();
        Spot premiumSpot = chosenSpot.get(0);
        int inARow = 0;
        int spotKeyBefore = -1;
        for (Spot spot :
                chosenSpot) {
            if (spotKeyBefore == spot.getKey() - 1) { //gute Spots hintereinander
                inARow += 1;
            } else if (inARow > 1) {       //gute Spots hintereinander zuEnde
                addRecommendedSpot(recommendedSpotPrint, premiumSpot, inARow);
                premiumSpot = spot;
                inARow = 1;
            } else {                      //möglicher guter Spot
                premiumSpot = spot;
                inARow = 1;
            }
            spotKeyBefore = spot.getKey();
        }
        return recommendedSpotPrint;
    }

    private static void addRecommendedSpot(List<String> recommendedSpotPrint, Spot premiumSpot, int inARow) {
        recommendedSpotPrint.add(String.format("In %s sind ab dem %s mind. %dStunden lang hintereinander perfekte Bedingungen.",
                premiumSpot.name, beautifulDateForPrinting(premiumSpot.getTime()), (inARow - 1) * 3));
    }

    private static void sendAlarm(List<Spot> chosenSpot, List<String> recommendedSpot) {
        System.out.println("    Spot   |  Datum  | Windstärke |Windrichtung|Temperatur");
        System.out.println("----------------------------------------------------------");
        chosenSpot.forEach(SpotAnalyse::alarmWithKmh);
        System.out.printf("%nEmpfehlung:%n");
        if (recommendedSpot.isEmpty()) {
            System.out.println("Keine klaren Empfehlungen vorhanden.");
        } else {
            for (String recommended : recommendedSpot) {
                System.out.println(recommended);
            }
        }
    }

    private static void alarmWithBft(Spot spot) {

        System.out.printf("%s %s %.2fm/s     %s     %.0f°C%n",
                spot.name, beautifulDateForPrinting(spot.getTime()), spot.getWindSpeed(),
                windDegreeForPrinting(spot.getWindDirection()), spot.getTemperatur());
    }

    private static void alarmWithKmh(Spot spot) {

        System.out.printf("%s %s %.2fm/s %.2fkm/h %.2fBft    %s     %.0f°C%n",
                spot.name, beautifulDateForPrinting(spot.getTime()), spot.getWindSpeed(), msInKmh(spot.getWindSpeed()), msInBft(spot.getWindSpeed()),
                windDegreeForPrinting(spot.getWindDirection()), spot.getTemperatur());
    }


}