package data.configuration;

import com.bring.annotation.Bean;
import com.bring.annotation.Configuration;
import data.client.RestClient;
import data.service.BringService;

@Configuration
public class TestConfiguration {

    @Bean
    public RestClient bringRestClient() {
        final RestClient restClient = new RestClient();
        restClient.setUrl("https://");
        restClient.setKey("KEY");
        
        return restClient;
    }

    @Bean
    public BringService bringService(final RestClient bringRestClient) {
        return new BringService(bringRestClient);
    }
    
}
