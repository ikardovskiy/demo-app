package demo.rostelecom.root.integration;

import demo.rostelecom.root.integration.service.LoaderService;
import demo.rostelecom.root.model.PhoneCode;
import io.swagger.annotations.Api;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/integration")
@Api(description = "Integration API",
        tags = {"integration"})
public class IntegrationController {

    private final LoaderService loaderService;

    public IntegrationController(LoaderService loaderService) {
        this.loaderService = loaderService;
    }
}
