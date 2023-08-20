package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.repository.CropRepository;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropService implements PostService<Crop, BaseDTO>, GetService<Crop> {

    private final CropRepository cropRepository;

    @Override
    public Crop getById(int id) throws ResourceNotFoundException {
        return null;
    }

    @Override
    public boolean isExists(int id) {
        return false;
    }

    @Override
    public boolean isExists(Crop crop) {
        return false;
    }

    @Override
    public Crop saveByDTO(BaseDTO baseDTO) {
        return null;
    }

    @Override
    public Crop save(Crop crop) {
        return null;
    }
}
