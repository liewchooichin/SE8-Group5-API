package sg.edu.ntu.bus_api.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


// This is a simple controller to test if I can print out the API key from the spring boot environment variable

@RestController
public class ApiTestController {
    
    @Value("${app.api.key}")
    private String apiKey;

    @GetMapping("/app/test-protect")
    public String test() {
        return apiKey;
    }
    
}