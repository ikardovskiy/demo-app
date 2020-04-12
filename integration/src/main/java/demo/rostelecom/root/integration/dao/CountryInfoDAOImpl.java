package demo.rostelecom.root.integration.dao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
public class CountryInfoDAOImpl implements CountryInfoDAO {

    static final String NAMES_PAGE = "names.json";
    static final String PHONES_PAGE = "phone.json";


    private final WebClient countryIOClient;


    public CountryInfoDAOImpl(@Value("${app.source.baseurl}") String baseUrl,
                              WebClient.Builder webClientBuilder) {
        this.countryIOClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public Mono<Map<String, String>> getNames() {
        return countryIOClient.
                get()
                .uri(NAMES_PAGE)
                .exchange()
                .flatMap(c -> c.bodyToMono(new ParameterizedTypeReference<>() {}));
    }

    public Mono<Map<String, String>> getPhones() {
        return countryIOClient.
                get()
                .uri(PHONES_PAGE)
                .exchange()
                .flatMap(c -> c.bodyToMono(new ParameterizedTypeReference<>() {}));
    }
}
