package com.fgan.azure.api;

import com.fgan.azure.Constants;
import com.fgan.azure.util.GeneralPrintUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.ComputeUsage;

public class QuotaApi {

    public static void printQuotasSync(Azure azure) {
        PagedList<ComputeUsage> computeUsages = getComputeUsages(azure);
        GeneralPrintUtil.printComputeUsages(computeUsages);
    }

    private static PagedList<ComputeUsage> getComputeUsages(Azure azure) {
        return azure.computeUsages().listByRegion(Constants.REGION_DEFAULT);
    }

}
