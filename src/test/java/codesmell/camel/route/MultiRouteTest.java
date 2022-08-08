package codesmell.camel.route;

import codesmell.camel.CamelConstants;
import codesmell.camel.TestDataUtil;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.apache.camel.*;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@CamelSpringBootTest
@SpringBootTest
public class MultiRouteTest {

    @Autowired
    private CamelContext context;

    public static final String MOCK_OUTBOX_ENDPOINT = "mock:outbox";
    public static final String MOCK_PARSER_ERROR_ENDPOINT = "mock:parserError";

    @EndpointInject(MOCK_OUTBOX_ENDPOINT)
    private MockEndpoint mockOutboxEndpoint;

    @EndpointInject(MOCK_PARSER_ERROR_ENDPOINT)
    private MockEndpoint mockParserErrorEndpoint;

    // skip the file in part
    // and drop on the split message route
    @Produce(CamelConstants.SPLIT_MESSAGE_ROUTE_URI)
    private ProducerTemplate camelProducer;

    @AfterEach
    public void cleanup() {
        mockOutboxEndpoint.reset();
        mockParserErrorEndpoint.reset();
    }

    @Test
    public void test_successful_processing() throws Exception {
        // setup mock endpoints
        this.interceptOutBoxSuccess();
        this.interceptParserErrorSuccess();

        mockOutboxEndpoint.expectedMessageCount(1);
        mockParserErrorEndpoint.expectedMessageCount(0);
        mockOutboxEndpoint.expectedMessagesMatches(exchange -> {
            String asnJson = exchange.getMessage().getBody(String.class);
            return asnJson.contains("\"shipmentIdentification\":\"9876\"");
        });

        Exchange exchangeIn = new DefaultExchange(context);
        exchangeIn.getMessage().setBody(TestDataUtil.sampleAdvanceShipNotice("9876"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*9876*20101127*2226*0001"));

        assertFalse(exchangeOut.isFailed());
        Exception unhandledException = exchangeOut.getException();
        assertNull(unhandledException);

        Exception handledException = (Exception) exchangeOut.getProperty(Exchange.EXCEPTION_CAUGHT);
        assertNull(handledException);

        mockOutboxEndpoint.assertIsSatisfied();
        mockParserErrorEndpoint.assertIsSatisfied();
    }

    @Test
    public void test_parsingError() throws Exception {
        // setup mock endpoints
        this.interceptOutBoxSuccess();
        this.interceptParserErrorSuccess();

        mockOutboxEndpoint.expectedMessageCount(0);
        mockParserErrorEndpoint.expectedMessageCount(1);
        mockParserErrorEndpoint.expectedMessagesMatches(exchange -> {
            String asnJson = exchange.getMessage().getBody(String.class);
            return asnJson.contains("BSN*00*1234*20101127*2226*0001");
        });

        Exchange exchangeIn = new DefaultExchange(context);
        exchangeIn.getMessage().setBody(TestDataUtil.sampleAdvanceShipNoticeInvalidFormat("1234"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*1234*20101127*2226*0001"));

        assertFalse(exchangeOut.isFailed());
        Exception unhandledException = exchangeOut.getException();
        assertNull(unhandledException);

        Exception handledException = (Exception) exchangeOut.getProperty(Exchange.EXCEPTION_CAUGHT);
        assertNotNull(handledException);
        assertTrue(handledException instanceof X12ParserException);

        mockOutboxEndpoint.assertIsSatisfied();
        mockParserErrorEndpoint.assertIsSatisfied();
    }

    @Test
    public void test_retryError() throws Exception {
        // setup mock endpoints
        this.interceptOutBoxSuccess();
        this.interceptParserErrorSuccess();

        mockOutboxEndpoint.expectedMessageCount(0);
        mockParserErrorEndpoint.expectedMessageCount(0);

        Exchange exchangeIn = new DefaultExchange(context);
        exchangeIn.getMessage().setBody(TestDataUtil.sampleAdvanceShipNotice("ERROR567"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*ERROR567*20101127*2226*0001"));

        mockOutboxEndpoint.assertIsSatisfied();
        mockParserErrorEndpoint.assertIsSatisfied();
    }

    private void interceptOutBoxSuccess() throws Exception {
        AdviceWith.adviceWith(context, CamelConstants.WRITE_FILE_ROUTE_ID, a -> {
            a.interceptSendToEndpoint(CamelConstants.OUTBOX_ENDPOINT)
                .skipSendToOriginalEndpoint()
                .to(MOCK_OUTBOX_ENDPOINT);
        });
    }

    private void interceptParserErrorSuccess() throws Exception {
        AdviceWith.adviceWith(context, CamelConstants.WRITE_PARSER_ERROR_ROUTE_ID, a -> {
            a.interceptSendToEndpoint(CamelConstants.PARSER_ERROR_ENDPOINT)
                .skipSendToOriginalEndpoint()
                .to(MOCK_PARSER_ERROR_ENDPOINT);
        });
    }

}