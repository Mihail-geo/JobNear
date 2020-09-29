package com.example.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@ConditionalOnProperty(value = "swagger.enabled", havingValue = "true")
@EnableSwagger2
@Configuration
public class SwaggerConfig {
	private static final String PASSWORD_FLOW = "Password Flow";

	@Value("${hostUrl}")
	private String hostUrl;

	@Bean
	public Docket api10() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("v1.0")
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/.*v1.0.*"))
				.build()
				.securityContexts(List.of(securityContext()))
				.securitySchemes(List.of(oAuthSecurity()))
				.apiInfo(buildApiInfo("1.0"));
	}

	@Bean
	public Docket api20() {
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("v2.0")
				.select()
				.apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.regex("/.*v2.0.*"))
				.build()
				.securityContexts(List.of(securityContext()))
				.securitySchemes(List.of(oAuthSecurity()))
				.apiInfo(buildApiInfo("2.0"));
	}

	private ApiInfo buildApiInfo(String version) {
		return new ApiInfoBuilder()
				.title("API Личного кабинета пассажира (панель администратора)")
				.version(version)
				.build();
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(securityReferences())
				.forPaths(PathSelectors.any())
				.build();
	}

	private List<SecurityReference> securityReferences() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return List.of(new SecurityReference(PASSWORD_FLOW, authorizationScopes));
	}

	private OAuth oAuthSecurity() {
		List<AuthorizationScope> authorizationScopeList = List.of(new AuthorizationScope("trust", "trust all"));
		List<GrantType> grantTypes = List.of(new ResourceOwnerPasswordCredentialsGrant(hostUrl + "/oauth/token"));
		return new OAuth(PASSWORD_FLOW, authorizationScopeList, grantTypes);
	}
}
