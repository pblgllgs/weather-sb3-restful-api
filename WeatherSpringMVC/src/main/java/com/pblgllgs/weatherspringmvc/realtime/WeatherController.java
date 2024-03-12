package com.pblgllgs.weatherspringmvc.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */
@Controller
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @RequestMapping("")
    public String viewHomePage(Model model) {
        try {
            RealtimeWeather realtimeWeather = weatherService.getRealtimeWeather();
            model.addAttribute("weather", realtimeWeather);
            return "index";
        } catch (WeatherServiceException e) {
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }
}
