package net.cs50.finance;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by cbay on 5/11/15.
 */

@Configuration
public class WebApplicationConfig extends WebMvcConfigurerAdapter {

    // Create managed bean to allow autowiring
    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor( authenticationInterceptor() );
    }

}
//ASTA e tot ce are in cod pentru autentificare. clasa User, clasa UserDao si asta cu Authentification Interceptor

//eu am pachetul ala AUTH in proiectul meu, si vreau sa il pun pe autentificarea si registerul asta