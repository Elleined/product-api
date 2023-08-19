package com.elleined.marketplaceapi.populator;

import com.elleined.marketplaceapi.model.user.Suffix;
import com.elleined.marketplaceapi.repository.SuffixRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Set;

@Transactional
@Component
@Qualifier("suffixPopulator")
public class SuffixPopulator extends Populator {

    private final SuffixRepository suffixRepository;

    public SuffixPopulator(ObjectMapper objectMapper, SuffixRepository suffixRepository) {
        super(objectMapper);
        this.suffixRepository = suffixRepository;
    }

    @Override
    public void populate(String jsonFile) throws IOException {
        var resource = new ClassPathResource(jsonFile);
        var type = objectMapper.getTypeFactory().constructCollectionType(Set.class, Suffix.class);

        Set<Suffix> suffixes = objectMapper.readValue(resource.getFile(), type);
        suffixRepository.saveAll(suffixes);
    }
}
