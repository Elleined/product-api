package com.elleined.marketplaceapi;

import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.model.AppWallet;
import com.elleined.marketplaceapi.populator.CropPopulator;
import com.elleined.marketplaceapi.populator.UserPopulator;
import com.elleined.marketplaceapi.populator.unit.RetailUnitPopulator;
import com.elleined.marketplaceapi.populator.unit.WholeSaleUnitPopulator;
import com.elleined.marketplaceapi.repository.AppWalletRepository;
import com.elleined.marketplaceapi.repository.CropRepository;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Transactional
@Component
@Slf4j
@RequiredArgsConstructor
public class AfterStartUp {

    private final CropPopulator cropPopulator;
    private final RetailUnitPopulator retailUnitPopulator;
    private final WholeSaleUnitPopulator wholeSaleUnitPopulator;
    private final UserPopulator userPopulator;
    private final CropRepository cropRepository;
    private final AppWalletRepository appWalletRepository;
    private final ModeratorService moderatorService;

    private final static String CROPS_JSON = "/json/crops.json";
    private final static String USERS_JSON = "/json/users.json";
    private final static String RETAIL_UNITS_JSON = "/json/unit/retail_units.json";
    private final static String WHOLE_SALE_UNITS_JSON = "/json/unit/whole_sale_units.json";

    @PostConstruct
    public void init() throws IOException {
        if (cropRepository.existsById(1)) {
            log.debug("Returning... because initial values of crops and units are already saved!");
            return;
        }
        log.debug("Saving app wallet, moderator, crops and units initial values! Please wait....");

        cropPopulator.populate(CROPS_JSON);
        retailUnitPopulator.populate(RETAIL_UNITS_JSON);
        wholeSaleUnitPopulator.populate(WHOLE_SALE_UNITS_JSON);
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
