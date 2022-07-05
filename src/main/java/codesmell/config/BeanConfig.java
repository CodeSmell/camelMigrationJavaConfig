package codesmell.config;

import codesmell.camel.process.X12SaveProcessor;
import codesmell.camel.process.X12SplitterProcessor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.txset.asn856.DefaultAsn856TransactionSetParser;
import com.walmartlabs.x12.util.split.X12TransactionSplitter;
import org.apache.camel.Processor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean(name = "x12SplitterProcessor")
    public Processor x12SplitterProcessor() {
        return new X12SplitterProcessor();
    }

    @Bean(name = "x12SaveProcessor")
    public Processor x12SaveProcessor() {
        return new X12SaveProcessor();
    }

    @Bean
    public X12TransactionSplitter x12TransactionSplitter() {
        return new X12TransactionSplitter();
    }

    @Bean
    public StandardX12Parser x12parser() {
        StandardX12Parser standardX12Parser = new StandardX12Parser();
        standardX12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
        return standardX12Parser;
    }

    @Bean
    public ObjectMapper jsonMapper() {
        return new ObjectMapper()
            .configure(SerializationFeature.WRAP_ROOT_VALUE, false)
            .configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .configure(SerializationFeature.INDENT_OUTPUT, false);
    }
}
