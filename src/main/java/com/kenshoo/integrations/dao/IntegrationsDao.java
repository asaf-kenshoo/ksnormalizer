package com.kenshoo.integrations.dao;

import com.kenshoo.integrations.entity.Integration;

import java.util.List;

public interface IntegrationsDao {
    /**
     * @return number of affected rows (0 or 1)
     */
    int insert(String ksId, String data);

    /**
     * @return number of affected rows (0 or 1)
     */
    int update(int id, String ksId, String data);

    /**
     * @return number of affected rows
     */
    int updateKsId(String oldKsId, String newKsId);

    List<Integration> fetchById(int id);
    List<Integration> fetchByKsId(String normalizeKsId);
    List<Integration> fetchAll();
}
