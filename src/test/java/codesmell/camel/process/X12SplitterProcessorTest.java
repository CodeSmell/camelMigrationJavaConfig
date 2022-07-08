package codesmell.camel.process;

import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.split.X12TransactionSplitter;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.Assert.*;

/**
 * more tests could be written ...
 */
public class X12SplitterProcessorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private X12SplitterProcessor x12SplitterProcessor;

    @Before
    public void setup() {
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

        exception.expect(X12ParserException.class);
        exception.expectMessage("expected ISA segment but got foo");

        String body = "fooBar";
        Exchange exchange = this.createExchangeWithBody(body);
        x12SplitterProcessor.process(exchange);

        List<String> txSets = exchange.getMessage().getBody(List.class);
        assertNotNull(txSets);
        assertTrue(CollectionUtils.isEmpty(txSets));
    }

    private Exchange createExchangeWithBody(String input) {
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getMessage().setBody(input);
        return exchange;
    }

}
