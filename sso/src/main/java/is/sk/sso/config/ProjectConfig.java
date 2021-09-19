package is.sk.sso.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

@Configuration
public class ProjectConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public ClientRegistrationRepository clientRepository() {
        var c = clientRegistration();
        return new InMemoryClientRegistrationRepository(c);
    }

    private ClientRegistration clientRegistration() {
        return CommonOAuth2Provider.GITHUB.getBuilder("github")
                .clientId("8a4bf4a04b40e202c3bc")
                .clientSecret("34faa971431a824ffc144ac8f73cd7b6a5baef0c")
                .build();
    }

//    private ClientRegistration clientRegistration() {
//        ClientRegistration cr = ClientRegistration.withRegistrationId("github")
//                .clientId("8a4bf4a04b40e202c3bc")
//                .clientSecret("34faa971431a824ffc144ac8f73cd7b6a5baef0c")
//                .scope(new String[]{"read:user"})
//                .authorizationUri("https://github.com/login/oauth/authorize")
//                .tokenUri("https://github.com/login/oauth/access_token")
//                .userInfoUri("https://api.github.com/user")
//                .userNameAttributeName("id")
//                .clientName("GitHub")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .redirectUriTemplate("{baseUrl}/{action}/oauth2/code/{registrationId}")
//                .build();
//        return cr;
//    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login();

        http.authorizeRequests()
                .anyRequest().authenticated();
    }
}
