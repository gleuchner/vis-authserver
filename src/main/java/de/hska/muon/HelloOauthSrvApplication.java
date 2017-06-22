package de.hska.muon;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableDiscoveryClient
public class HelloOauthSrvApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloOauthSrvApplication.class, args);
	}

	@Value("${OAuth2ClientId:my-client-with-secret}")
	private String oAuth2ClientId;

	@Value("${OAuth2ClientSecret:secret}")
	private String oAuth2ClientSecret;

	@Value("${oauth.token:http://authserver:8763/oauth/token}")
	private String accessTokenUri;

	@Bean
	public RestTemplate oAuthRestTemplate() {
		ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
		resourceDetails.setId("1");
		resourceDetails.setClientId(oAuth2ClientId);
		resourceDetails.setClientSecret(oAuth2ClientSecret);
		resourceDetails.setAccessTokenUri(accessTokenUri);
		List<String> scopes = new ArrayList<>();
		scopes.add("read");
		scopes.add("write");
		scopes.add("trust");
		resourceDetails.setScope(scopes);

		OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext());

		return restTemplate;
	}
}
