package demo.rostelecom.root.service;

import demo.rostelecom.root.controller.PhoneCode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class CodeServiceImpl implements CodeService {
    @Override
    public Mono<List<PhoneCode>> searchCodes(String country) {
        return Mono.just(Arrays.asList(
                PhoneCode.builder()
                        .code("BE")
                        .name("Belgium")
                        .country("BLG")
                        .build()
        ));
    }
}
