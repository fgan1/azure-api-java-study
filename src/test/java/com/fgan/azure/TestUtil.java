package com.fgan.azure;

import cloud.fogbow.common.exceptions.UnauthenticatedUserException;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.fgan.azure.util.AzureIDBuilderGeneral;
import com.fgan.azure.util.PropertiesUtil;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PropertiesUtil.class, AzureIDBuilderGeneral.class})
public class TestUtil {

    static final String ID_DEFAULT = "id";

    @Before
    public void setUp() throws IOException, UnauthenticatedUserException {
        PowerMockito.mockStatic(PropertiesUtil.class);
        Properties properties = new Properties();
        PowerMockito.when(PropertiesUtil.getInstance()).thenReturn(properties);

        PowerMockito.mockStatic(AzureIDBuilderGeneral.class);
        PowerMockito.when(AzureIDBuilderGeneral.buildVirtualMachineId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilderGeneral.buildSecurityGroupId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilderGeneral.buildDiskId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilderGeneral.buildNetworkId(Mockito.anyString())).thenReturn(ID_DEFAULT);
    }

}
