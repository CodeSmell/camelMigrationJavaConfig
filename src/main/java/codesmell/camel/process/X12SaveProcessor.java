package codesmell.camel.process;

import codesmell.util.LogFormatUtil;
import codesmell.util.RetryableException;
import codesmell.util.X12GozerDataExtractionUtil;
import com.walmartlabs.x12.standard.StandardX12Document;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.util.StringUtils;

/**
 * simulate saving the X12 data
 * with possibility for errors
 */
public class X12SaveProcessor implements Processor {
    private static final Logger LOGGER = LoggerFactory.getLogger(X12SaveProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        LogFormatUtil.logger(LOGGER)
            .withMessage("saving X12 file...")
            .andLog(Level.INFO);

        // exception simulator
        StandardX12Document x12ParsedDoc = exchange.getIn().getBody(StandardX12Document.class);
        String bsn02 = X12GozerDataExtractionUtil.extractDocumentNumber(x12ParsedDoc);
        if (StringUtils.hasLength(bsn02) && bsn02.startsWith("ERROR")) {
            throw new RetryableException("an error occurred...");
        }
    }
}
