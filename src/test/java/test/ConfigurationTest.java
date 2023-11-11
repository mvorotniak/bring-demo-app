package test;

import static org.assertj.core.api.Assertions.assertThat;

import com.bring.BringApplication;
import com.bring.context.BringApplicationContext;
import data.client.RestClient;
import data.configuration.TestConfiguration;
import data.service.BringService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ConfigurationTest {
    
    @DisplayName("All beans from configuration class registered in Bring Context")
    @Test
    void testConfigurationBeansRegistration() {
        BringApplication bringApplication = new BringApplication(TestConfiguration.class);
        BringApplicationContext bringApplicationContext = bringApplication.run();
        
        RestClient restClient = bringApplicationContext.getBean(RestClient.class);
        BringService bringService = bringApplicationContext.getBean(BringService.class);
        
        assertThat(restClient).isNotNull();
        assertThat(bringService).isNotNull();
        assertThat(bringService.getBringRestClient()).isEqualTo(restClient);
    }
    
}
