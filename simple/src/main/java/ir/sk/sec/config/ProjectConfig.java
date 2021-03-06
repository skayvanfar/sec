package ir.sk.sec.config;

import ir.sk.sec.config.csrf.CustomCsrfTokenRepository;
import ir.sk.sec.config.services.AuthenticationProviderService;
import ir.sk.sec.filters.AuthenticationLoggingFilter;
import ir.sk.sec.filters.CsrfTokenLogger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

// for authorization
@Configuration
@EnableAsync
public class ProjectConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationProviderService authenticationProvider;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SCryptPasswordEncoder sCryptPasswordEncoder() {
        return new SCryptPasswordEncoder();
    }

    @Bean
    public CsrfTokenRepository customTokenRepository() {
        return new CustomCsrfTokenRepository();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin().defaultSuccessUrl("/main", true);

        http.httpBasic();

    /*
        Spring SEPL
        String expression = "hasAuthority('read') and !hasAuthority('delete')";
        http.authorizeRequests()
                .anyRequest().access(expression)
                .anyRequest().hasRole("ADMIN")
                .anyRequest().access("T(java.time.LocalTime).now().isAfter(T(java.time.LocalTime).of(12, 0))
                .mvcMatchers( "/a/b/**").authenticated()
                .mvcMatchers("/hello").hasRole("ADMIN")
                .mvcMatchers("/ciao").hasRole("MANAGER")
                .mvcMatchers( "/product/{code:^[0-9]*$}").permitAll() // regex
                .regexMatchers("./(us|uk|ca)+/(en|fr).*")
                .anyRequest().permitAll();*/

    //    http.authorizeRequests().anyRequest().authenticated();

        http.csrf(c -> {
            c.csrfTokenRepository(customTokenRepository());
            c.ignoringAntMatchers("/ciao");

//            HandlerMappingIntrospector i = new HandlerMappingIntrospector();
//            MvcRequestMatcher r = new MvcRequestMatcher(i, "/ciao");
//            c.ignoringRequestMatchers(r);

//            String pattern = ".*[0-9].*";
//            String httpMethod = HttpMethod.POST.name();
//            RegexRequestMatcher r = new RegexRequestMatcher(pattern, httpMethod);
//            c.ignoringRequestMatchers(r);
        });
        http.csrf().disable();

        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("*"));
                config.setAllowedMethods(List.of("*"));
                return config;
            };
            c.configurationSource(source);
        });

        http
                //.addFilterBefore(
              //  new RequestValidationFilter(),
               // BasicAuthenticationFilter.class)
                .addFilterAfter(
                        new AuthenticationLoggingFilter(),
                        BasicAuthenticationFilter.class)
                .addFilterAfter(
                        new CsrfTokenLogger(),
                        CsrfFilter.class)
                .authorizeRequests()
                .anyRequest()
                .permitAll();
    }

    @Bean
    public InitializingBean initializingBean() {
        return () -> SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }
}
