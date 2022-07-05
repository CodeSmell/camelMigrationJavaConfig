package codesmell.config;

import codesmell.camel.route.in.GozerFileRouteBuilder;
import codesmell.camel.route.GozerTxSetHandlingRouteBuilder;
import codesmell.camel.route.out.GozerEndRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelRoutesConfig {

    @Bean(name = "gozerFileRoute")
    public RouteBuilder gozerFileRoute() {
        return new GozerFileRouteBuilder();
    }

    @Bean(name = "gozerTxSetRoute")
    public RouteBuilder gozerTxSetRoute() {
        return new GozerTxSetHandlingRouteBuilder();
    }

    @Bean(name = "gozerEndRoute")
    public RouteBuilder gozerEndRoute() {
        return new GozerEndRouteBuilder();
    }
}
