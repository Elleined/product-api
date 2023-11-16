package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.service.unit.RetailUnitService;
import com.elleined.marketplaceapi.service.unit.WholeSaleUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/units")
public class UnitController {
    private final RetailUnitService retailUnitService;
    private final WholeSaleUnitService wholeSaleUnitService;

    @GetMapping("/retail-units")
    public List<String> getAllRetailUnits() {
        return retailUnitService.getAll();
    }

    @GetMapping("/whole-sale-units")
    public List<String> getAllWholeSaleUnits() {
        return wholeSaleUnitService.getAll();
    }
}
