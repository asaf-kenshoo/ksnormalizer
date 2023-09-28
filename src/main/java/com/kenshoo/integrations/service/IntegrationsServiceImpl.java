package com.kenshoo.integrations.service;

import com.kenshoo.integrations.dao.IntegrationsDao;
import com.kenshoo.integrations.entity.Integration;

import java.io.IOException;
import java.util.List;

public class IntegrationsServiceImpl implements IntegrationsService {

	KsNormalizerClient ksNormalizerClient;
	IntegrationsDao integrationsDao;
	
	@Override
	public void insertIntegration(String ksId, String data) {
		
		var normalizedKsId = normazieKsId(ksId);
		
		if(integrationsDao.fetchByKsId(normalizedKsId).isEmpty() && integrationsDao.fetchByKsId(ksId).isEmpty()) 
		{
			if(integrationsDao.insert(normalizedKsId, data) == 0) {

				throw new IllegalArgumentException("Cannot insert data");
			}
		}
		else 
		{
			throw new IllegalArgumentException("Element with such ksid already exists");
		}
	}

	@Override
	public List<Integration> fetchIntegrationsByKsId(String ksId) {
		List<Integration> integrations = integrationsDao.fetchByKsId(ksId);
		
		if(integrations.isEmpty()) {
			String normalizedKsId = normazieKsId(ksId);
			return integrationsDao.fetchByKsId(normalizedKsId);
		}else {
			return integrations;
		}
	}

	@Override
	public int migrate() {
		var count = integrationsDao.fetchAll().stream()
			.map(integration -> integration.getKsId())
			.distinct()
			.filter(ksId -> {
				String normalizedKsId = normazieKsId(ksId);
				if(ksId != normalizedKsId) {
					integrationsDao.updateKsId(ksId, normalizedKsId);
					return true;
				}
				return false;
			})
			.count();
		return Long.valueOf(count).intValue();
	}
	
	private String normazieKsId(String ksId) {
		try {

			return ksNormalizerClient.normalize(ksId);
		}
		catch(IOException exception){
			throw new IllegalArgumentException("Cannot normalize ksId", exception);
		}
	}
	
	
}