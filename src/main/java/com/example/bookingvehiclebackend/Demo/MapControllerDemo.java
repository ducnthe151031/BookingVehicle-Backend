package com.example.bookingvehiclebackend.Demo;

import org.springframework.web.bind.annotation.GetMapping;

public class MapControllerDemo {
    @GetMapping("/map")
    public String showMap() {
        return "map"; // return map.html
    }
}
