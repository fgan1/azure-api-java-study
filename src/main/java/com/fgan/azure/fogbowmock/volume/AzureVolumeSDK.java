package com.fgan.azure.fogbowmock.volume;

import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.microsoft.azure.management.Azure;
import rx.Completable;

public interface AzureVolumeSDK {

    static void deleteDisk(Azure azure, String diskId) throws AzureException.Unexpected {
        try {
            azure.disks().deleteById(diskId);
        } catch (RuntimeException e) {
            throw new AzureException.Unexpected(e);
        }
    }

    static Completable buildDeleteDiskCompletable(Azure azure, String diskId) throws AzureException.Unexpected {
        try {
            return azure.disks().deleteByIdAsync(diskId);
        } catch (RuntimeException e) {
            throw new AzureException.Unexpected(e);
        }
    }

}
