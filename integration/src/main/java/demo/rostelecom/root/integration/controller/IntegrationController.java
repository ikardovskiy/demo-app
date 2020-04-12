package demo.rostelecom.root.integration.controller;

import demo.rostelecom.root.integration.service.LoaderService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping("/integration")
@Api(description = "Integration API",
        tags = {"integration"})
public class IntegrationController {

    private final LoaderService loaderService;

    public IntegrationController(LoaderService loaderService) {
        this.loaderService = loaderService;
    }

    @PostMapping("/load")
    public Mono<Void> loadCodes(){
        return loaderService.loadCodes();
    }
}
