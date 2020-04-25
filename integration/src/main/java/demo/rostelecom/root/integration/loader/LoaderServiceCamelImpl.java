package demo.rostelecom.root.integration.loader;

import org.apache.camel.ProducerTemplate;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static demo.rostelecom.root.integration.loader.PhoneCodeRoute.DIRECT_LOADER;

@Service
@Primary
public class LoaderServiceCamelImpl implements LoaderService {

    private final ProducerTemplate producerTemplate;

    public LoaderServiceCamelImpl(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    @Override
    public Mono<Void> loadCodes() {
        return Mono.fromFuture(producerTemplate.asyncSendBody(DIRECT_LOADER,null)).then();
    }
}
