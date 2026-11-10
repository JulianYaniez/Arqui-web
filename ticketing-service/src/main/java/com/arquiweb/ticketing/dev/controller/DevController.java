package com.arquiweb.ticketing.dev.controller;

import com.arquiweb.ticketing.dev.service.DevService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@RequiredArgsConstructor
@Hidden
public class DevController {

    private final DevService devService;

    @PostMapping("/seed")
    public ResponseEntity<Void> seed() {
        devService.seed();
        return ResponseEntity.ok().build();
    }
}
