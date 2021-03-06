/*
 * Copyright (c) 2008-2016, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.config;

import com.hazelcast.spi.discovery.NodeFilter;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;

/**
 * Readonly version of {@link DiscoveryConfig}
 */
public class DiscoveryConfigReadOnly
        extends DiscoveryConfig {

    DiscoveryConfigReadOnly(DiscoveryConfig discoveryConfig) {
        super(discoveryConfig.getDiscoveryServiceProvider(), discoveryConfig.getNodeFilter(),
                discoveryConfig.getNodeFilterClass(), discoveryConfig.getDiscoveryStrategyConfigs());
    }

    @Override
    public void setDiscoveryServiceProvider(DiscoveryServiceProvider discoveryServiceProvider) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void setNodeFilter(NodeFilter nodeFilter) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void setNodeFilterClass(String nodeFilterClass) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

    @Override
    public void addDiscoveryStrategyConfig(DiscoveryStrategyConfig discoveryStrategyConfig) {
        throw new UnsupportedOperationException("Configuration is readonly");
    }

}
