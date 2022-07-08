package codesmell.camel;

public final class CamelConstants {
    /**
     * routes
     */
    public static final String SPLIT_MESSAGE_ROUTE_URI = "direct:gozerSplitMessage";
    public static final String SPLIT_MESSAGE_ROUTE_ID = "gozerSplitMessage";

    public static final String PROCESS_SPLIT_MESSAGE_ROUTE_URI = "direct:gozerDocProcess";
    public static final String PROCESS_SPLIT_MESSAGE_ROUTE_ID = "gozerDocProcess";

    public static final String WRITE_FILE_ROUTE_URI = "direct:gozerEnd";
    public static final String WRITE_FILE_ROUTE_ID = "gozerEnd";

    public static final String WRITE_PARSER_ERROR_ROUTE_URI = "direct:writeParserError";
    public static final String WRITE_PARSER_ERROR_ROUTE_ID = "writeParserError";


    /**
     * endpoints
     */
    public static final String OUTBOX_ENDPOINT = "file:outbox?fileExist=fail&fileName=${id}.txt";
    public static final String PARSER_ERROR_ENDPOINT = "file:parserErrors?fileName=${id}.txt";
}
