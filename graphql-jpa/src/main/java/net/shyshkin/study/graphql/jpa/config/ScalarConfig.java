package net.shyshkin.study.graphql.jpa.config;

import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class ScalarConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder.scalar(ExtendedScalars.GraphQLLong);
    }
}
