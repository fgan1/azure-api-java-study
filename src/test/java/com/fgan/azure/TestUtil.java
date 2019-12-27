package com.fgan.azure;

import com.fgan.azure.util.AzureIDBuilder;
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
@PrepareForTest({PropertiesUtil.class, AzureIDBuilder.class})
public class TestUtil {

    static final String ID_DEFAULT = "id";

    @Before
    public void setUp() throws IOException {
        PowerMockito.mockStatic(PropertiesUtil.class);
        Properties properties = new Properties();
        PowerMockito.when(PropertiesUtil.getInstance()).thenReturn(properties);

        PowerMockito.mockStatic(AzureIDBuilder.class);
        PowerMockito.when(AzureIDBuilder.buildVirtualMachineId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilder.buildSecurityGroupId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilder.buildDiskId(Mockito.anyString())).thenReturn(ID_DEFAULT);
        PowerMockito.when(AzureIDBuilder.buildNetworkId(Mockito.anyString())).thenReturn(ID_DEFAULT);
    }

}
