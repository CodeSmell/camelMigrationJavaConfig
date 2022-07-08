package codesmell.camel.process;

import codesmell.util.LogFormatUtil;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.split.X12TransactionSplitter;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * will process an EDI message and put on the exchange
 * a List of all the TransactionSets in the message
 */
public class X12SplitterProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(X12SplitterProcessor.class);

    @Autowired
    private X12TransactionSplitter x12TransactionSplitter;

    /**
     * split an EDI file into the separate documents
     * using the Gozer splitter
     *
     * @param exchange the message exchange
     * @throws X12ParserException
     */
    @Override
    public void process(Exchange exchange) {
        LogFormatUtil.logger(LOGGER)
            .withMessage("splitting X12 file...")
            .andLog(Level.INFO);

        String x12File = exchange.getMessage().getBody(String.class);
        List<String> x12TransactionSets = x12TransactionSplitter.split(x12File);

        exchange.getMessage().setBody(x12TransactionSets);
    }
}
