package codesmell.camel.route.in;

import codesmell.camel.CamelConstants;
import codesmell.util.RetryableException;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * reads an ASN 856 file from the directory inbox
 */
public class GozerFileRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GozerFileRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        onException(X12ParserException.class)
            .handled(true)
            .log(LoggingLevel.WARN, "Error parsing document: ${id}.txt")
            // there was an issue w/ the AdviceWith changes in Camel 3
            // that wouldn't work unless moved this file endpoint
            // to another route
            //.to(CamelConstants.PARSER_ERROR_ENDPOINT)   // what was working in Camel 2
            .to(CamelConstants.WRITE_PARSER_ERROR_ROUTE_URI)
            .end();

        onException(RetryableException.class)
            .handled(false)
            .log(LoggingLevel.ERROR, "Error processing document: ${exception.message}")
            .end();

        this.fileInRoute();

        this.splitEdiMessageRoute();

        // route to allow MultiRouteTest to
        // work using AdviceWith changes in Camel 3
        from(CamelConstants.WRITE_PARSER_ERROR_ROUTE_URI)
            .routeId(CamelConstants.WRITE_PARSER_ERROR_ROUTE_ID)
            .log(LoggingLevel.INFO, "writing error file")
            .to(CamelConstants.PARSER_ERROR_ENDPOINT);
    }

    public void fileInRoute() throws Exception {

        from("file://inbox?preMove=in-progress&delete=true")
            .log(LoggingLevel.INFO, "Consuming X12 file from directory...")
            .to(CamelConstants.SPLIT_MESSAGE_ROUTE_URI)
            // when all of the routes end we end up back here
            .log(LoggingLevel.INFO, "Finished processing the X12 file from directory");
    }

    public void splitEdiMessageRoute() throws Exception {

        from(CamelConstants.SPLIT_MESSAGE_ROUTE_URI)
            .routeId(CamelConstants.SPLIT_MESSAGE_ROUTE_ID)
            // call the bean as a process
            // that was not autowired
            .process("x12SplitterProcessor")
            // splitter separates each transaction set out
            // from the original message and puts them into a list
            .process(exchange -> {
                List<String> x12TransactionSets = exchange.getMessage().getBody(List.class);
                exchange.setProperty("total.tx.sets", x12TransactionSets.size());
            })
            .log(LoggingLevel.INFO, "Finished splitting X12 File into ${exchangeProperty.total.tx.sets} documents")
            // multi route processing
            // send each member of the list to another route to be processed
            .split(body())
                .to(CamelConstants.PROCESS_SPLIT_MESSAGE_ROUTE_URI)
            .end()
            // when all of the routes called from
            // the split are done we end up
            // right here
            // camel will have access to the original message
            // after split ends by default
            .log(LoggingLevel.INFO, "End of " + CamelConstants.SPLIT_MESSAGE_ROUTE_URI);

    }

}
