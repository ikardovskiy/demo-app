package demo.rostelecom.root.integration.loader;

import reactor.core.publisher.Mono;

public interface LoaderService {
    Mono<Void> loadCodes();
}
