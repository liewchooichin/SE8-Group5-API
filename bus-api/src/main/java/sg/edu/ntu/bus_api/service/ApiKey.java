package sg.edu.ntu.bus_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKey {
    // Do a @Value injection of the APP_API_KEY property 
    @Value("${APP_API_KEY}")
    private String myApiKey;

    // Set up a getter method
    public String getApiKey(){
      return myApiKey;
    }

}

// ApiKey.java act as a centralized place to hold your API key.
// Your API Key comes from an env variable that must be set up in .vscode/launch.json first
// Next, assign the env variable to the custom property APP_API_KEY in application.properties
// Only then will the @Value injection of API key work