package demo.rostelecom.root.controller;

import demo.rostelecom.root.model.PhoneCode;
import demo.rostelecom.root.service.CodeService;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@SuppressWarnings("deprecation")
@RestController
@RequestMapping("/rest")
@Api(description = "телефонные Коды",
        tags = {"code"})
public class SearchController {

    private final CodeService codeService;

    public SearchController(CodeService codeService) {
        this.codeService = codeService;
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(IllegalArgumentException exception){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exception.getMessage());
    }

    @GetMapping("/code")
    public Mono<List<PhoneCode>> searchCodes(@RequestParam String country){
        return codeService.searchCodes(country);
    }
}
