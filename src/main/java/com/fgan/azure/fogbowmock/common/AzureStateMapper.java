package com.fgan.azure.fogbowmock.common;

import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.models.ResourceType;
import com.fgan.azure.fogbowmock.compute.AzureComputePlugin;
import org.slf4j.LoggerFactory;

public class AzureStateMapper {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AzureComputePlugin.class);
    private static final String COMPUTE_PLUGIN = AzureComputePlugin.class.getSimpleName();
    public static final String CREATING_STATE = "Creating";
    public static final String SUCCEEDED_STATE = "Succeeded";

    public static InstanceState map(ResourceType type, String state) {
        state = state.toLowerCase();
        switch (type) {
            case COMPUTE:
                // cloud state values: [Creating, Succeded]
                switch (state) {
                    case CREATING_STATE:
                        return InstanceState.CREATING;
                    case SUCCEEDED_STATE:
                        return InstanceState.READY;
                    default:
                        LOGGER.error(String.format(Messages.Error.UNDEFINED_INSTANCE_STATE_MAPPING, state, COMPUTE_PLUGIN));
                        return InstanceState.INCONSISTENT;
                }
            default:
                LOGGER.error(Messages.Error.INSTANCE_TYPE_NOT_DEFINED);
                return InstanceState.INCONSISTENT;
        }
    }
}
