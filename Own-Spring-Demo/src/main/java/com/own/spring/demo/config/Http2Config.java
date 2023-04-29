package com.own.spring.demo.config;

import io.undertow.UndertowOptions;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Http2Config {

    /**
     * Configure undertow enable Http2 support
     */
    @Bean
    public UndertowServletWebServerFactory undertowServletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addBuilderCustomizers(builder ->
                builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        return factory;
    }

}
