package com.kenshoo.integrations.service;

import com.kenshoo.integrations.dao.IntegrationsDao;
import com.kenshoo.integrations.entity.Integration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IntegrationsServiceImplTest {
    @InjectMocks
    IntegrationsServiceImpl integrationsService;

    @Mock
    IntegrationsDao integrationsDao;

    @Mock
    KsNormalizerClient ksNormalizerClient;

    @Test
    public void insertIntegration() {
        integrationsService.insertIntegration("123", "some data");
    }

    @Test
    public void fetchIntegrationsByKsId_validId_succeed() throws IOException {
        final String KS_ID = "111";
        final String NORMALIZED_KS_ID = "4011";
        List<Integration> expected = List.of(new Integration(1, NORMALIZED_KS_ID, "some data"));

        when(ksNormalizerClient.normalize(eq(KS_ID))).thenReturn(NORMALIZED_KS_ID);
        when(integrationsDao.fetchByKsId(eq(NORMALIZED_KS_ID))).thenReturn(expected);

        assertEquals(expected, integrationsService.fetchIntegrationsByKsId(KS_ID));
    }

    @Test
    public void fetchIntegrationsByKsId_failedNormalize_leaveSameId() throws IOException {
        final String KS_ID = "111";
        List<Integration> expected = List.of(new Integration(1, KS_ID, "some data"));

        when(ksNormalizerClient.normalize(eq(KS_ID))).thenThrow(new IOException());
        when(integrationsDao.fetchByKsId(eq(KS_ID))).thenReturn(expected);

        assertEquals(expected, integrationsService.fetchIntegrationsByKsId(KS_ID));
    }

    @Test
    public void migrate() throws IOException {
        final String KS_ID1 = "111";
        final String NORMALIZED_KS_ID1 = "4011";
        final String KS_ID2 = "222";
        final String NORMALIZED_KS_ID2 = "4222";
        List<Integration> allIntegrations = List.of(
                new Integration(1, KS_ID1, "some data1"),
                new Integration(2, KS_ID1, "some data2"),
                new Integration(3, KS_ID1, "some data3"),
                new Integration(4, KS_ID2, "some data4"));

        when(integrationsDao.fetchAll()).thenReturn(allIntegrations);
        when(integrationsDao.updateKsId(eq(KS_ID1), eq(NORMALIZED_KS_ID1))).thenReturn(countIntegrationsForKsId(KS_ID1, allIntegrations));
        when(integrationsDao.updateKsId(eq(KS_ID2), eq(NORMALIZED_KS_ID2))).thenReturn(countIntegrationsForKsId(KS_ID2, allIntegrations));
        when(ksNormalizerClient.normalize(eq(KS_ID1))).thenReturn(NORMALIZED_KS_ID1);
        when(ksNormalizerClient.normalize(eq(KS_ID2))).thenReturn(NORMALIZED_KS_ID2);

        int updatedEntriesCount = integrationsService.migrate();

        verify(integrationsDao, times(1)).fetchAll();
        verify(integrationsDao, times(2)).updateKsId(anyString(), anyString());
        assertEquals(allIntegrations.size(), updatedEntriesCount);
    }

    private int countIntegrationsForKsId(String ksId, List<Integration> integrations) {
        return (int) integrations.stream().filter(integration -> integration.getKsId().equals(ksId)).count();
    }
}