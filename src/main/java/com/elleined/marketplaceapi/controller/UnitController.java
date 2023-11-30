package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.dto.UnitDTO;
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
    public List<UnitDTO> getAllRetailUnits() {
        return retailUnitService.getAll().stream()
                .map(retailUnitService::toDTO)
                .toList();
    }

    @GetMapping("/whole-sale-units")
    public List<UnitDTO> getAllWholeSaleUnits() {
        return wholeSaleUnitService.getAll().stream()
                .map(wholeSaleUnitService::toDTO)
                .toList();
    }


}
