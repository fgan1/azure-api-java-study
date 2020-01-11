package com.fgan.azure.fogbowmock.volume;

import cloud.fogbow.common.exceptions.UnexpectedException;
import com.fgan.azure.fogbowmock.exceptions.AzureException;
import com.microsoft.azure.management.Azure;
import rx.Completable;

public interface AzureVolumeSDK {

    static void deleteDisk(Azure azure, String diskId) throws UnexpectedException {
        try {
            azure.disks().deleteById(diskId);
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }

    static Completable buildDeleteDiskCompletable(Azure azure, String diskId) throws UnexpectedException {
        try {
            return azure.disks().deleteByIdAsync(diskId);
        } catch (RuntimeException e) {
            throw new UnexpectedException(e.getMessage(), e);
        }
    }

}
