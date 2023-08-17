package com.elleined.marketplaceapi;

import com.elleined.marketplaceapi.populator.CropPopulator;
import com.elleined.marketplaceapi.populator.Populator;
import com.elleined.marketplaceapi.populator.UnitPopulator;
import com.elleined.marketplaceapi.repository.CropRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Transactional
@Component
@Slf4j
public class AfterStartUp {

    private final Populator cropPopulator;
    private final Populator unitPopulator;
    private final CropRepository cropRepository;

    public AfterStartUp(Populator cropPopulator, @Qualifier("unitPopulator") Populator unitPopulator, CropRepository cropRepository) {
        this.cropPopulator = cropPopulator;
        this.unitPopulator = unitPopulator;
        this.cropRepository = cropRepository;
    }

    private final static String CROPS_JSON = "/json/crops.json";
    private final static String UNITS_JSON = "/json/units.json";

    @PostConstruct
    public void init() throws IOException {
        if (cropRepository.existsById(1)) {
            log.debug("Returning... because initial values of crops and units are already saved!");
            return;
        }
        log.debug("Saving crops and units initial values! Please wait....");
        cropPopulator.populate(CROPS_JSON);
        unitPopulator.populate(UNITS_JSON);
        log.debug("Saving crops and units are successful. Thank you!...");
    }
}
