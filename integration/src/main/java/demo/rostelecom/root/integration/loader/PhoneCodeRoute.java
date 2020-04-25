package demo.rostelecom.root.integration.loader;


import demo.rostelecom.root.model.PhoneCode;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static demo.rostelecom.root.integration.loader.LoaderRouteUtils.calculateDeletedPhoneCodes;
import static demo.rostelecom.root.integration.loader.LoaderRouteUtils.convertToPhoneCodesList;

@Component
public class PhoneCodeRoute extends RouteBuilder {

    public static final String ROUTE_LOADER_TIMER = "loader-timer";
    public static final String ROUTE_LOADER = "loader";
    public static final String ROUTE_LOADER_CODES = "loader-codes";
    public static final String ROUTE_LOADER_STORED_CODES = "loader-stored-codes";
    public static final String DIRECT_LOADER = "direct:loader";
    public static final String PHONE_CODE_ROUTE_LOGGER = "demo.rostelecom.root.integration.loader.PhoneCodeRoute";

    @Override
    public void configure() {
        AggregationStrategy constructPhoneCodes = (oldExchange, newExchange) -> {

            final Map<String, String> phones = oldExchange.getMessage().getBody(Map.class);
            final Map<String, String> codes = newExchange.getMessage().getBody(Map.class);

            var phoneCodeList = convertToPhoneCodesList(phones, codes);
            oldExchange.getMessage().setBody(phoneCodeList);

            return oldExchange;
        };

        AggregationStrategy storedPhonesForDeleteion = (oldExchange, newExchange) -> {

            final List<PhoneCode> loadedPhones = oldExchange.getMessage().getBody(List.class);
            final List<PhoneCode> storedPhones = newExchange.getMessage().getBody(List.class);

            var deletedPhoneCodes = calculateDeletedPhoneCodes(loadedPhones, storedPhones);

            oldExchange.getMessage().setBody(deletedPhoneCodes);

            return oldExchange;
        };

        from("timer:loader-timer?period={{app.integration.reload.delay}}")
                .routeId(ROUTE_LOADER_TIMER)
                .to(DIRECT_LOADER);

        from(DIRECT_LOADER)
                .routeId(ROUTE_LOADER)
                .onCompletion()
                    .log(LoggingLevel.INFO, PHONE_CODE_ROUTE_LOGGER,"completed")
                .end()
                .log(LoggingLevel.INFO, PHONE_CODE_ROUTE_LOGGER,"started")
                .setBody(simple("${null}")).description("clear body")
                .to("{{app.source.baseurl}}names.json?httpMethod=GET")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .log(LoggingLevel.TRACE, PHONE_CODE_ROUTE_LOGGER,"loaded names ${body}")
                .enrich("direct:loader-codes", constructPhoneCodes)
                .to("bean:repositoryFacade?method=save")
                .log(LoggingLevel.TRACE, PHONE_CODE_ROUTE_LOGGER,"saved codes ${body}")
                .enrich("direct:loader-stored-codes", storedPhonesForDeleteion)
                .to("bean:repositoryFacade?method=delete")
                .log(LoggingLevel.TRACE, PHONE_CODE_ROUTE_LOGGER,"deleted codes ${body}");

        from("direct:loader-codes")
                .routeId(ROUTE_LOADER_CODES)
                .setBody(simple("${null}")).description("clear body")
                .to("{{app.source.baseurl}}phone.json?httpMethod=GET")
                .unmarshal().json(JsonLibrary.Jackson, Map.class)
                .log(LoggingLevel.TRACE, PHONE_CODE_ROUTE_LOGGER,"loaded codes ${body}");

        from("direct:loader-stored-codes")
                .routeId(ROUTE_LOADER_STORED_CODES)
                .to("bean:repositoryFacade?method=getAll");
    }
}


