package codesmell.camel.route.out;

import codesmell.camel.CamelConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmartlabs.x12.standard.StandardX12Document;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class GozerEndRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GozerEndRouteBuilder.class);

    @Autowired
    ObjectMapper jsonMapper;

    @Override
    public void configure() throws Exception {

        from(CamelConstants.WRITE_FILE_ROUTE_URI)
            .routeId(CamelConstants.WRITE_FILE_ROUTE_ID)
            .log(LoggingLevel.INFO, "writing out split X12 document...")
            .process(exchange -> {
                // should have Gozer POJOs
                StandardX12Document x12Doc = exchange.getMessage().getBody(StandardX12Document.class);
                String jsonGozer = jsonMapper.writeValueAsString(x12Doc);
                exchange.getMessage().setBody(jsonGozer);
            })
            // write each file using the camel message id
            .to(CamelConstants.OUTBOX_ENDPOINT);
    }
}
