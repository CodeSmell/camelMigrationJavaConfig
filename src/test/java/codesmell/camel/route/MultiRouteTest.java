package codesmell.camel.route;

import codesmell.camel.CamelConstants;
import codesmell.config.BeanConfig;
import codesmell.config.CamelRoutesConfig;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Before;
import org.junit.Test;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        BeanConfig.class,
        CamelRoutesConfig.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class MultiRouteTest extends CamelTestSupport {

    public static final String MOCK_OUTBOX_ENDPOINT = "mock:outbox";
    public static final String MOCK_PARSER_ERROR_ENDPOINT = "mock:parserError";

    // skip the file in part
    // and drop on the split message route
    @Produce(uri = CamelConstants.SPLIT_MESSAGE_ROUTE_URI)
    private ProducerTemplate camelProducer;

    @Autowired
    @Qualifier("x12SplitterProcessor")
    Processor x12SplitterProcessor;

    @Autowired
    @Qualifier("gozerFileRoute")
    RouteBuilder gozerInRoute;

    @Autowired
    @Qualifier("gozerTxSetRoute")
    RouteBuilder gozerTxSetRoute;

    @Autowired
    @Qualifier("gozerEndRoute")
    RouteBuilder gozerEndRoute;

    private MockEndpoint mockOutboxEndpoint;
    private MockEndpoint mockParserErrorEndpoint;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        mockOutboxEndpoint = getMockEndpoint(MOCK_OUTBOX_ENDPOINT);
        mockParserErrorEndpoint = getMockEndpoint(MOCK_PARSER_ERROR_ENDPOINT);

        context.getRegistry().bind("x12SplitterProcessor", x12SplitterProcessor);

        context.addRoutes(gozerInRoute);
        context.addRoutes(gozerTxSetRoute);
        context.addRoutes(gozerEndRoute);

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
        exchangeIn.getMessage().setBody(this.sampleAdvanceShipNotice("9876"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*9876*20101127*2226*0001"));

        assertFalse(exchangeOut.isFailed());
        Exception unhandledException = exchangeOut.getException();
        assertNull(unhandledException);

        Exception handledException = (Exception) exchangeOut.getProperty(Exchange.EXCEPTION_CAUGHT);
        assertNull(handledException);

        this.assertMockEndpointsSatisfied();
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
        exchangeIn.getMessage().setBody(this.sampleAdvanceShipNoticeInvalidFormat("1234"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*1234*20101127*2226*0001"));

        assertFalse(exchangeOut.isFailed());
        Exception unhandledException = exchangeOut.getException();
        assertNull(unhandledException);

        Exception handledException = (Exception) exchangeOut.getProperty(Exchange.EXCEPTION_CAUGHT);
        assertNotNull(handledException);
        assertTrue(handledException instanceof X12ParserException);

        this.assertMockEndpointsSatisfied();
    }

    @Test
    public void test_retryError() throws Exception {
        // setup mock endpoints
        this.interceptOutBoxSuccess();
        this.interceptParserErrorSuccess();

        mockOutboxEndpoint.expectedMessageCount(0);
        mockParserErrorEndpoint.expectedMessageCount(0);

        Exchange exchangeIn = new DefaultExchange(context);
        exchangeIn.getMessage().setBody(this.sampleAdvanceShipNotice("ERROR567"));
        Exchange exchangeOut = camelProducer.send(exchangeIn);

        String origMessage = exchangeOut.getMessage().getBody(String.class);
        assertTrue(origMessage.contains("BSN*00*ERROR567*20101127*2226*0001"));

        this.assertMockEndpointsSatisfied();
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

    private String sampleAdvanceShipNoticeInvalidFormat(String bsn02) {
        String data = this.sampleAdvanceShipNotice(bsn02);
        return data.substring(0, data.trim().lastIndexOf("\n"));
    }
    private String sampleAdvanceShipNotice(String bsn02) {
        return new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABC*ZZ*123456789012345*101127*1719*U*00400*000003438*0*P*>")
            .append("\n")
            .append("GS*PO*99*999999999*20101127*1719*1421*X*004010")
            .append("\n")
            .append("ST*856*0001")
            .append("\n")
            .append("BSN*00*").append(bsn02).append("*20101127*2226*0001")
            .append("\n")
            .append("HL*1**S")
            .append("\n")
            .append("TD1**160****G*6256*LB")
            .append("\n")
            .append("TD5**2*WALMRT")
            .append("\n")
            .append("REF*UCB*60504900000438841")
            .append("\n")
            .append("DTM*011*20210329")
            .append("\n")
            .append("FOB*PP")
            .append("\n")
            .append("N1*SF*Sunkist*UL*0")
            .append("N1*ST*WAL-MART GROCERY DC #6057*UL*0")
            .append("\n")
            .append("HL*2*1*O")
            .append("\n")
            .append("PRF*0558834757***20210323")
            .append("\n")
            .append("REF*IA*99")
            .append("\n")
            .append("REF*IV*77")
            .append("\n")
            .append("HL*3*2*P")
            .append("\n")
            .append("MAN*GM*1001001")
            .append("\n")
            .append("HL*4*3*ZZ")
            .append("\n")
            .append("LIN**LT*BBC")
            .append("\n")
            .append("SN1**32*EA")
            .append("\n")
            .append("CTT*3")
            .append("\n")
            .append("SE*23*0001")
            .append("\n")
            .append("GE*1*1421")
            .append("\n")
            .append("IEA*1*000003438")
            .append("\n")
            .toString();
    }
}