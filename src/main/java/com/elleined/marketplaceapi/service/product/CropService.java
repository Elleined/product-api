package com.elleined.marketplaceapi.service.product;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.Crop;
import com.elleined.marketplaceapi.repository.CropRepository;
import com.elleined.marketplaceapi.service.baseservices.GetAllService;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropService implements PostService<Crop, BaseDTO>, GetService<Crop>, GetAllService<String> {

    private final CropRepository cropRepository;
    private final BaseMapper baseMapper;

    @Override
    public Crop getById(int id) throws ResourceNotFoundException {
        return cropRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Crop with id of " + id + " does not exists!"));
    }

    @Override
    public boolean isExists(int id) {
        return cropRepository.existsById(id);
    }

    @Override
    public List<String> getAll() {
        return cropRepository.findAll().stream()
                .map(Crop::getName)
                .sorted()
                .toList();
    }

    @Override
    public Crop saveByDTO(BaseDTO baseDTO) {
        Crop crop = baseMapper.toCropEntity(baseDTO);
        cropRepository.save(crop);
        log.debug("Crop with name of {} saved successfully with id of {}", crop.getName(), crop.getId());
        return crop;
    }
}
