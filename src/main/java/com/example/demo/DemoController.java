package com.example.demo;

import com.example.demo.windsurf.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;


@Controller
//@RequestMapping("/Infos")
public class DemoController {

    @GetMapping
    public String demo(Model model, @RequestParam(required = false) String ort,
                       @RequestParam(required = false, defaultValue = "0") double minWindspeed,
                       @RequestParam(required = false, defaultValue = "100") double maxWindspeed,
                       @RequestParam(required = false, defaultValue = "0") double minTemperatur) {
        model.addAttribute("zeitstempel", LocalDateTime.now());
        model.addAttribute("minWindspeed", minWindspeed);
        model.addAttribute("maxWindspeed", maxWindspeed);
        model.addAttribute("minTemperatur", minTemperatur);
        if(ort != null) {
            model.addAttribute("ort", ort);
            List<Spot> chosenSpot = null;
            try {
                chosenSpot = SpotAnalyse.chooseSpots(getStringArray(ort),
                        new SurfConditions(minWindspeed, maxWindspeed, minTemperatur));
            } catch (SpotNotFoundException e) {
                String exceptionMessage = e.getMessage();
                model.addAttribute("exceptionMessage", exceptionMessage);
                return "demo";
            }
            model.addAttribute("SpotInfo", chosenSpot);
            model.addAttribute("SpotEmpfehlung", SpotAnalyse.findRecommendedSpot(chosenSpot));
        }
        return "demo"; //namen einer HTML datei
    }  //soll sich um http GetRequest kümmern

    private String[] getStringArray(String orte) {
        return orte.split(" ");
    }




}
