package sg.edu.ntu.bus_api.service;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;


@Service
public class AppBusArrivalService {
  // Datamall endpoints
  // Parameters: BusStopCode, ServiceNo
  private final String BUS_ARRIVAL_URL = 
    "https://datamall2.mytransport.sg/ltaodataservice/v3/BusArrival";
  
  // Local object variable for ApiKey
  private final ApiKey apiKey;

  // Constructor injection
  public AppBusArrivalService(ApiKey apiKey) {
      this.apiKey = apiKey;
  }

  
  // GET bus arrival time
  public String getBusArrivalTime(String BusStopCode, String ServiceNo){
        // create the rest client
    RestClient restClient = RestClient.create();
    // map the request query parameters
    // Map<String, String> paramMap = new LinkedHashMap<>();
    // paramMap.put("BusStopCode", BusStopCode);
    // paramMap.put("ServiceNo", ServiceNo);
    // build the query string
    // BusStopCode is required, but ServiceNo is optional
    // Build the query string
    StringBuilder query = new StringBuilder();
    query.append("BusStopCode=");
    query.append(BusStopCode);
    if(!ServiceNo.equals("")){
      query.append("&ServiceNo=");
      query.append(ServiceNo);
    }
    // append the query string to the request url
    StringBuilder requestUrl = new StringBuilder();
    requestUrl.append(BUS_ARRIVAL_URL);
    requestUrl.append("?");
    requestUrl.append(query.toString());
    // the order of rest client get matters.
    // must be .get, .uri., .header.
    ResponseEntity<String> result = 
    restClient.get()
        .uri(requestUrl.toString())
        .header("accountKey", apiKey.getApiKey())
        .retrieve()
        .toEntity(String.class);
    // print the result for debugging
    System.out.println("Response status: " + result.getStatusCode());
    // System.out.println("Response headers: " + result.getHeaders());
    System.out.println("Contents: " + result.getBody());
    return result.getBody();
  }
}
