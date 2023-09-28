package com.kenshoo.integrations.service;
import org.junit.*;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import com.kenshoo.integrations.dao.IntegrationsDao;
import com.kenshoo.integrations.entity.Integration;

import org.junit.runner.*;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationsServiceImplTest {

    @Mock
    KsNormalizerClient ksNormalizerClient;
    
    @Mock
    IntegrationsDao integrationsDao;
    
    @InjectMocks
    IntegrationsServiceImpl integrationsService;
    
    @Test
    public void insertIntegration_Success() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(integrationsDao.insert(Mockito.eq(normalizedKsId), Mockito.eq(data))).thenReturn(1);
    	
    	integrationsService.insertIntegration(ksId, data);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void insertIntegration_CannotInsertInDB() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(integrationsDao.insert(Mockito.eq(normalizedKsId), Mockito.eq(data))).thenReturn(0);
    	
    	integrationsService.insertIntegration(ksId, data);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void insertIntegration_KsIdAlreadyExists() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(ksId)))
    		.thenReturn(List.of(new Integration(id,ksId,data)));
    	
    	integrationsService.insertIntegration(ksId, data);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void insertIntegration_NormalizedKsIdAlreadyExists() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(normalizedKsId)))
    		.thenReturn(List.of(new Integration(id,normalizedKsId,data)));
    	
    	integrationsService.insertIntegration(ksId, data);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void insertIntegration_NormalizationFailed() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenThrow(new IOException());
    	
    	integrationsService.insertIntegration(ksId, data);
    }    

    @Test
    public void fetchIntegrationsByKsId_SuccessWithKsId() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	List<Integration> expectedIntegration = List.of(new Integration(id,ksId,data));
    	
    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(ksId))).thenReturn(expectedIntegration);
    	
    	List<Integration> actualIntegration = integrationsService.fetchIntegrationsByKsId(ksId);
    	assertEquals(expectedIntegration, actualIntegration);
    }

    @Test
    public void fetchIntegrationsByKsId_SuccessWithNormilizedKsId() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	List<Integration> expectedIntegration = List.of(new Integration(id,ksId,data));

    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(ksId))).thenReturn(List.of());
    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(normalizedKsId))).thenReturn(expectedIntegration);
    	
    	List<Integration> actualIntegration = integrationsService.fetchIntegrationsByKsId(ksId);
    	assertEquals(expectedIntegration, actualIntegration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchIntegrationsByKsId_NormalizationFailed() throws IOException {
    	String ksId = UUID.randomUUID().toString();

    	Mockito.when(integrationsDao.fetchByKsId(Mockito.eq(ksId))).thenReturn(List.of());
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenThrow(new IOException());
    	
    	integrationsService.fetchIntegrationsByKsId(ksId);
    }

    @Test
    public void fetchAll_SuccessWithOneUnnormalizedKsId() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String alreadyNormalizedKsId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	Integration normalizedIntegration = new Integration(id,alreadyNormalizedKsId,data);
    	Integration integration = new Integration(id,ksId,data);
    	List<Integration> expectedIntegration = List.of(normalizedIntegration, integration);

    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(alreadyNormalizedKsId))).thenReturn(alreadyNormalizedKsId);
    	Mockito.when(integrationsDao.fetchAll()).thenReturn(expectedIntegration);
    	
    	int actualNumberOfChangedIntegrations = integrationsService.migrate();
    	assertEquals(1, actualNumberOfChangedIntegrations);
    }

    @Test
    public void fetchAll_SuccessWithOneDuplicatedUnnormalizedKsId() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String alreadyNormalizedKsId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	Integration normalizedIntegration = new Integration(id,alreadyNormalizedKsId,data);
    	Integration integration = new Integration(id,ksId,data);
    	List<Integration> expectedIntegration = List.of(normalizedIntegration, integration, normalizedIntegration, integration);

    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenReturn(normalizedKsId);
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(alreadyNormalizedKsId))).thenReturn(alreadyNormalizedKsId);
    	Mockito.when(integrationsDao.fetchAll()).thenReturn(expectedIntegration);
    	
    	int actualNumberOfChangedIntegrations = integrationsService.migrate();
    	assertEquals(1, actualNumberOfChangedIntegrations);
    }

    @Test
    public void fetchAll_SuccessWithTwoUnnormalizedKsId() throws IOException {
    	String alreadyNormalizedKsId1 = UUID.randomUUID().toString();
    	String alreadyNormalizedKsId2 = UUID.randomUUID().toString();
    	String ksId1 = UUID.randomUUID().toString();
    	String normalizedKsId1 = UUID.randomUUID().toString();
    	String ksId2 = UUID.randomUUID().toString();
    	String normalizedKsId2 = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	Integration normalizedIntegration1 = new Integration(id,alreadyNormalizedKsId1,data);
    	Integration normalizedIntegration2 = new Integration(id,alreadyNormalizedKsId2,data);
    	Integration integration1 = new Integration(id,ksId1,data);
    	Integration integration2 = new Integration(id,ksId2,data);
    	List<Integration> expectedIntegration = List.of(normalizedIntegration1, normalizedIntegration2, integration1, integration2);

    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId1))).thenReturn(normalizedKsId1);
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId2))).thenReturn(normalizedKsId2);
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(alreadyNormalizedKsId1))).thenReturn(alreadyNormalizedKsId1);
    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(alreadyNormalizedKsId2))).thenReturn(alreadyNormalizedKsId2);
    	Mockito.when(integrationsDao.fetchAll()).thenReturn(expectedIntegration);
    	
    	int actualNumberOfChangedIntegrations = integrationsService.migrate();
    	assertEquals(2, actualNumberOfChangedIntegrations);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fetchAll_NormalizationFailed() throws IOException {
    	String ksId = UUID.randomUUID().toString();
    	String alreadyNormalizedKsId = UUID.randomUUID().toString();
    	String normalizedKsId = UUID.randomUUID().toString();
    	String data = UUID.randomUUID().toString();
    	int id = 0;
    	Integration normalizedIntegration = new Integration(id,alreadyNormalizedKsId,data);
    	Integration integration = new Integration(id,ksId,data);
    	List<Integration> expectedIntegration = List.of(normalizedIntegration, integration);


    	Mockito.when(ksNormalizerClient.normalize(Mockito.eq(ksId))).thenThrow(new IOException());
    	Mockito.when(integrationsDao.fetchAll()).thenReturn(expectedIntegration);
    	
    	integrationsService.migrate();
    }
}