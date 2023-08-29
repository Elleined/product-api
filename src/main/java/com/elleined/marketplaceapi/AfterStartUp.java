package com.elleined.marketplaceapi;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.model.AppWallet;
import com.elleined.marketplaceapi.populator.Populator;
import com.elleined.marketplaceapi.repository.AppWalletRepository;
import com.elleined.marketplaceapi.repository.CropRepository;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Transactional
@Component
@Slf4j
public class AfterStartUp {

    private final Populator cropPopulator;
    private final Populator unitPopulator;
    private final Populator userPopulator;
    private final CropRepository cropRepository;
    private final AppWalletRepository appWalletRepository;
    private final ModeratorService moderatorService;

    public AfterStartUp(Populator cropPopulator,
                        @Qualifier("unitPopulator") Populator unitPopulator,
                        @Qualifier("userPopulator") Populator userPopulator,
                        CropRepository cropRepository,
                        AppWalletRepository appWalletRepository,
                        ModeratorService moderatorService) {
        this.cropPopulator = cropPopulator;
        this.unitPopulator = unitPopulator;
        this.userPopulator = userPopulator;
        this.cropRepository = cropRepository;
        this.appWalletRepository = appWalletRepository;
        this.moderatorService = moderatorService;
    }

    private final static String CROPS_JSON = "/json/crops.json";
    private final static String USERS_JSON = "/json/users.json";
    private final static String UNITS_JSON = "/json/units.json";

    @PostConstruct
    public void init() throws IOException {
        if (cropRepository.existsById(1)) {
            log.debug("Returning... because initial values of crops and units are already saved!");
            return;
        }
        log.debug("Saving app wallet, moderator, crops and units initial values! Please wait....");

        cropPopulator.populate(CROPS_JSON);
        unitPopulator.populate(UNITS_JSON);
        userPopulator.populate(USERS_JSON);

        AppWallet appWallet = AppWallet.builder()
                .appWalletBalance(new BigDecimal(0))
                .build();
        appWalletRepository.save(appWallet);

        moderatorService.save(ModeratorDTO.builder()
                .id(1)
                .name("Sample moderator name")
                .moderatorCredentialDTO(CredentialDTO.builder()
                        .email("sampleModeratorEmail@gmail.com")
                        .password("sampleModeratorPassword")
                        .build())
                .build());
        log.debug("Saving app wallet, moderator, crops and units are successful. Thank you!...");
    }
}
