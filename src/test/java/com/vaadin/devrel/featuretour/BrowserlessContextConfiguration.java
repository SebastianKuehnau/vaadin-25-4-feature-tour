package com.vaadin.devrel.featuretour;

import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * One browserless application for all tests that share the Spring context.
 * Each test opens a user and a window of its own, and Spring closes the
 * application together with its context.
 */
@Configuration
public class BrowserlessContextConfiguration {

    @Bean
    BrowserlessApplicationContext browserlessApplicationContext(ApplicationContext applicationContext) {
        return SpringBrowserlessApplicationContext.create(applicationContext, Application.class);
    }
}
