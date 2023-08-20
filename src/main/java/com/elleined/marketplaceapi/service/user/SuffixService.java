package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.dto.BaseDTO;
import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.user.Suffix;
import com.elleined.marketplaceapi.repository.SuffixRepository;
import com.elleined.marketplaceapi.service.baseservices.GetService;
import com.elleined.marketplaceapi.service.baseservices.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SuffixService implements PostService<Suffix, BaseDTO>, GetService<Suffix> {
    private final SuffixRepository suffixRepository;
    private final BaseMapper baseMapper;

    @Override
    public Suffix getById(int id) throws ResourceNotFoundException {
        return suffixRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Suffix with id of " + id + " does not exists!"));
    }

    @Override
    public boolean isExists(int id) {
        return suffixRepository.existsById(id);
    }

    @Override
    public Suffix saveByDTO(BaseDTO baseDTO) {
        Suffix suffix = baseMapper.toSuffixEntity(baseDTO);
        suffixRepository.save(suffix);
        log.debug("Suffix with name of {} saved successfully with id of {}", suffix.getName(), suffix.getId());
        return suffix;
    }

    @Override
    public Suffix save(Suffix suffix) {
        suffixRepository.save(suffix);
        log.debug("Suffix with name of {} saved successfully with id of {}", suffix.getName(), suffix.getId());
        return suffix;
    }
}
