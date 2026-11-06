package com.arquiweb.catalog.dev.controller;

import com.arquiweb.catalog.dev.service.DevService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dev")
public class DevController {

    private final DevService devService;

    @PostMapping("/seed")
    public ResponseEntity<Void> seed() {
        devService.seed();
        return ResponseEntity.ok().build();
    }

}
