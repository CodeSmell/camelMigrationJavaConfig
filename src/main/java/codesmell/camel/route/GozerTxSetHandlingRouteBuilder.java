package codesmell.camel.route;

import codesmell.camel.CamelConstants;
import codesmell.camel.process.X12SaveProcessor;
import codesmell.util.LogFormatUtil;
import codesmell.util.X12GozerDataExtractionUtil;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

public class GozerTxSetHandlingRouteBuilder extends RouteBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(GozerTxSetHandlingRouteBuilder.class);

    @Autowired
    StandardX12Parser x12Parser;

    @Autowired
    @Qualifier("x12SaveProcessor")
    X12SaveProcessor saveProcessor;

    @Override
    public void configure() throws Exception {

        from(CamelConstants.PROCESS_SPLIT_MESSAGE_ROUTE_URI)
            .routeId(CamelConstants.PROCESS_SPLIT_MESSAGE_ROUTE_ID)
            .log(LoggingLevel.INFO, "Processing split X12 document...")
            .process(exchange -> {
                // parsing the document
                String x12Doc = exchange.getIn().getBody(String.class);
                StandardX12Document x12ParsedDoc = x12Parser.parse(x12Doc);

                String bsn02 = X12GozerDataExtractionUtil.extractDocumentNumber(x12ParsedDoc);
                LogFormatUtil.logger(LOGGER)
                    .withMessage("parsed ASN document successfully: " + bsn02)
                    .andLog(Level.INFO);

                exchange.getIn().setBody(x12ParsedDoc);
            })
            // call the bean as a process
            // that was autowired
            .process(saveProcessor)
            .to(CamelConstants.WRITE_FILE_ROUTE_URI)
            .log(LoggingLevel.INFO, "End of " + CamelConstants.PROCESS_SPLIT_MESSAGE_ROUTE_URI);
    }
}
