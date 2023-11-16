package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crops")
public class CropController {
    private final CropService cropService;

    @GetMapping("/crops")
    public List<String> getAllCrops() {
        return cropService.getAll();
    }
}
