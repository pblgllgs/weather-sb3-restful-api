package com.pblgllgs.weatherspringmvc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/*
 *
 * @author pblgl
 * Created on 12-03-2024
 *
 */
@Configuration
public class AppConfig {

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
