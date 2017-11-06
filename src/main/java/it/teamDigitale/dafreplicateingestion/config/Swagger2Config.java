/**
 * 
 */
package it.teamDigitale.dafreplicateingestion.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author alessandro
 *
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {
	
	@Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())
          .build()
          .apiInfo(apiInfo());
    }
	
	private ApiInfo apiInfo() {
	     return new ApiInfo(
	       "DAF Replicate ingestion service", 
	       "REST API to trigger ingestion of a list of service from Replicate into DAF", 
	       "API TOS", 
	       "Terms of service", 
	       new Contact("Example", "www.example.com", "address@example.com"), 
	       "GPL-3.0", "https://www.gnu.org/licenses/gpl-3.0.txt", Collections.emptyList());
	}
}
