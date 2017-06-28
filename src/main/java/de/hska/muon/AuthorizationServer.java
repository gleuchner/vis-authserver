package de.hska.muon;

import de.hska.muon.model.CustomTokenServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import javax.sql.DataSource;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends
        AuthorizationServerConfigurerAdapter {


    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
        endpoints.userDetailsService(userDetailsService); // Inject custom
        endpoints.tokenServices(tokenServices());
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // @formatter:off
        clients.inMemory().withClient("my-trusted-client")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")
                .resourceIds("oauth2-resource").accessTokenValiditySeconds(600).and()
                .withClient("my-client-with-registered-redirect").authorizedGrantTypes("authorization_code")
                .authorities("ROLE_CLIENT").scopes("read", "trust").resourceIds("oauth2-resource")
                .redirectUris("http://anywhere?key=value").and().withClient("my-client-with-secret")
                .authorizedGrantTypes("client_credentials", "password").authorities("ROLE_CLIENT")
                .scopes("read", "write", "trust").resourceIds("oauth2-resource").secret("secret");
        // @formatter:on
    }

    public DefaultTokenServices tokenServices() {
        DefaultTokenServices tokenServices = new CustomTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(new InMemoryTokenStore());
        tokenServices.setAccessTokenValiditySeconds(86400000);
        tokenServices.setRefreshTokenValiditySeconds(86400000);
        return tokenServices;
    }
}

