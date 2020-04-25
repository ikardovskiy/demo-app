package demo.rostelecom.root.integration.loader;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.model.PhoneCode;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component("repositoryFacade")
public class RepositoryFacadeImpl implements RepositoryFacade {

    public static final int ELASTIC_LOADING_PARALLEL_REQUESTS = 10;
    private final PhoneCodeRepository phoneCodeRepository;

    public RepositoryFacadeImpl(PhoneCodeRepository phoneCodeRepository) {
        this.phoneCodeRepository = phoneCodeRepository;
    }

    @Override
    public void save(List<PhoneCode> codes) {
        Flux.fromIterable(codes).flatMap(phoneCodeRepository::save, ELASTIC_LOADING_PARALLEL_REQUESTS).blockLast();
    }

    @Override
    public void delete(List<PhoneCode> codes) {
        Flux.fromIterable(codes).flatMap(phoneCodeRepository::delete,ELASTIC_LOADING_PARALLEL_REQUESTS).blockLast();
    }

    @Override
    public List<PhoneCode> getAll() {
        return phoneCodeRepository.findAll().collectList().block();
    }
}
