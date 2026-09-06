package com.example.paymentplatform.processor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/authorizations")
public class AuthorizationController {

    private final AuthorizationRegistry registry;

    public AuthorizationController(AuthorizationRegistry registry) {
        this.registry = registry;
    }

    @PostMapping
    public ResponseEntity<AuthorizationResponse> authorize(@RequestBody AuthorizationRequest request) {
        return ResponseEntity.status(CREATED).body(registry.authorize(request));
    }

    @GetMapping("/{processorKey}")
    public ResponseEntity<AuthorizationResponse> find(@PathVariable String processorKey) {
        AuthorizationResponse response = registry.find(processorKey);
        return response == null ? ResponseEntity.status(NOT_FOUND).build() : ResponseEntity.ok(response);
    }
}
