package net.icnslab.sparkhu.dataretentionmanagementservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfiguration {
	
	@Bean
	public Docket api() {
		 
		return new Docket(DocumentationType.OAS_30)
				.apiInfo(apiInfo())
				.useDefaultResponseMessages(false)
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any())
				.build();
	}
	
	private ApiInfo apiInfo() {
		String description = "Data retention management service in Sparkhu";
		return new ApiInfoBuilder()
				.title("Swagger data-retention-management-sparkhu")
				.description(description)
				.version("1.0")
				.build();
	}
}
