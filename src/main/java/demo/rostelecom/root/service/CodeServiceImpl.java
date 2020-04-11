package demo.rostelecom.root.service;

import demo.rostelecom.root.dao.PhoneCodeRepository;
import demo.rostelecom.root.model.PhoneCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CodeServiceImpl implements CodeService {

    private final PhoneCodeRepository phoneCodeRepository;

    public CodeServiceImpl(PhoneCodeRepository phoneCodeRepository) {
        this.phoneCodeRepository = phoneCodeRepository;
    }

    @Override
    public Mono<List<PhoneCode>> searchCodes(String country) {
        return phoneCodeRepository.findByCountryContains(country)
                .collectList();
    }
}
