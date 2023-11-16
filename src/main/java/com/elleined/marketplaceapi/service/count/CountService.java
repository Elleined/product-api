package com.elleined.marketplaceapi.service.count;

public interface CountService {

    int getAllUsersCount();

    int getAllUsersTransactionsCount();

    // only the sold and listed products are included to the count
    int getAllProductCount();
}
