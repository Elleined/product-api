package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.exception.ResourceNotFoundException;
import com.elleined.marketplaceapi.mapper.BaseMapper;
import com.elleined.marketplaceapi.model.user.Suffix;
import com.elleined.marketplaceapi.repository.SuffixRepository;
import com.elleined.marketplaceapi.service.BaseEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SuffixService implements BaseEntityService<Suffix> {
    private final SuffixRepository suffixRepository;
    private final BaseMapper baseMapper;

    @Override
    public Suffix getById(int id) throws ResourceNotFoundException {
        return suffixRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Suffix with id of " + id + " does not exists!"));
    }

    @Override
    public boolean existsById(int id) {
        return suffixRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return suffixRepository.findAll().stream()
                .map(Suffix::getName)
                .anyMatch(name::equalsIgnoreCase);
    }

    @Override
    public List<String> getAll() {
        return suffixRepository.findAll().stream()
                .map(Suffix::getName)
                .sorted()
                .toList();
    }

    @Override
    public Suffix getByName(String name) throws ResourceNotFoundException {
        return suffixRepository.findAll().stream()
                .filter(suffix -> suffix.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Suffix with name of " + name + " does not exists!"));
    }

    @Override
    public Suffix save(String name) {
        Suffix suffix = baseMapper.toSuffixEntity(name);
        suffixRepository.save(suffix);
        log.debug("Suffix with name of {} saved successfully with id of {}", suffix.getName(), suffix.getId());
        return suffix;
    }
}
