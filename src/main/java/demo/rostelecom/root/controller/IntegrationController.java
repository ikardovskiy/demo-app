package demo.rostelecom.root.controller;

import demo.rostelecom.root.integration.service.LoaderService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
