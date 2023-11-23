package com.elleined.marketplaceapi.service;

import com.elleined.marketplaceapi.mapper.CropMapper;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.repository.CropRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CropServiceTest {

    @Mock
    private CropRepository cropRepository;
    @Mock
    private CropMapper cropMapper;
    @InjectMocks
    private CropService cropService;

    @Test
    void notExist() {
        // Mock data

        // Stubbing methods
        when(cropRepository.findAll()).thenReturn(Collections.singletonList(getMockCrop()));

        // Calling the method
        // Assertions
        assertTrue(cropService.notExist("Not existing crop name"));

        // Behavior verification
        verify(cropRepository).findAll();
    }

    @Test
    void getByName() {
        // Mock data
        Crop expected = Crop.builder()
                .name("Crop")
                .build();

        // Stubbing methods
        when(cropRepository.findAll()).thenReturn(Collections.singletonList(expected));

        // Calling the method
        Crop actual = cropService.getByName("cRop");

        // Assertions
        assertEquals(expected, actual);

        // Behavior verification
        verify(cropRepository).findAll();
    }

    private Crop getMockCrop() {
        return Crop.builder()
                .name("Crop")
                .build();
    }
}