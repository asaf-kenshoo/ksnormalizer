package com.kenshoo.integrations.service;

import com.kenshoo.integrations.entity.Integration;

import java.util.List;

public interface IntegrationsService {
    /**
     * Inserts data into the integrations table
     *
     * @param ksId a ks id, might be not normalized
     * @param data
     */
    void insertIntegration(String ksId, String data);

    /**
     * Returns all integrations having the provided ks id
     *
     * @param ksId a ks id, might be not normalized
     * @return list of all the integrations having the provided ks id
     */
    List<Integration> fetchIntegrationsByKsId(String ksId);

    /**
     * Updates all rows in integrations table with normalized ks id
     *
     * @return number of affected rows
     */
    int migrate();
}
