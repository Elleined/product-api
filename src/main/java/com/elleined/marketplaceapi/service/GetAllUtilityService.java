package com.elleined.marketplaceapi.service;

import java.util.List;

public interface GetAllUtilityService {

    List<String> getAllEmail();


    List<String> getAllMobileNumber();

    List<String> getAllGender();

    List<String> getAllSuffix();

    int getAllUsersCount();

    int getAllUsersTransactionsCount();

    List<String> getAllCrops();

    List<String> getAllUnit();

    // only the sold and listed products are included to the count
    int getAllProductCount();
}
