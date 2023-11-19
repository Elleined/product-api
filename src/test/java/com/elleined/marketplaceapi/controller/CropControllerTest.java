package com.elleined.marketplaceapi.controller;

import com.elleined.marketplaceapi.service.CropService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.*;

class CropControllerTest {
    @Mock
    CropService cropService;
    @InjectMocks
    CropController cropController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllCrops() {
        when(cropService.getAll()).thenReturn(List.of("getAllResponse"));

        List<String> result = cropController.getAllCrops();
        Assertions.assertEquals(List.of("replaceMeWithExpectedResult"), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: https://weirddev.com/forum#!/testme