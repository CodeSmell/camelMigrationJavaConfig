package codesmell.camel.process;

import codesmell.camel.TestDataUtil;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.split.X12TransactionSplitter;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * more tests could be written ...
 */
public class X12SplitterProcessorTest {

    private X12SplitterProcessor x12SplitterProcessor;

    @BeforeEach
    public void setupEachTest() {
        x12SplitterProcessor = new X12SplitterProcessor();
        X12TransactionSplitter gozerSplitter = new X12TransactionSplitter();
        ReflectionTestUtils.setField(x12SplitterProcessor, "x12TransactionSplitter", gozerSplitter);
    }

    @Test
    public void test_processWithNullBody() {
        String body = null;
        Exchange exchange = this.createExchangeWithBody(body);
        x12SplitterProcessor.process(exchange);

        List<String> txSets = exchange.getMessage().getBody(List.class);
        assertNotNull(txSets);
        assertTrue(CollectionUtils.isEmpty(txSets));
    }

    @Test
    public void test_processWithNonEdiMessage() {

        String body = "fooBar";
        Exchange exchange = this.createExchangeWithBody(body);

        X12ParserException thrownException = assertThrows(
                X12ParserException.class,
                () -> x12SplitterProcessor.process(exchange));

        assertEquals("expected ISA segment but got foo", thrownException.getMessage());
    }

    @Test
    public void test_processWithEdiMessage() {

        String body = TestDataUtil.sampleAdvanceShipNotice("FOO123");
        Exchange exchange = this.createExchangeWithBody(body);

        x12SplitterProcessor.process(exchange);

        List<String> txSets = exchange.getMessage().getBody(List.class);
        assertNotNull(txSets);
        assertEquals(1, txSets.size());
    }

    private Exchange createExchangeWithBody(String input) {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getMessage().setBody(input);
        return exchange;
    }

}
