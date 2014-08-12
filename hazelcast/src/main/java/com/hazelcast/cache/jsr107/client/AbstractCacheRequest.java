/*
 * Copyright (c) 2008-2013, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.cache.jsr107.client;

import com.hazelcast.cache.jsr107.CachePortableHook;
import com.hazelcast.cache.jsr107.CacheService;
import com.hazelcast.client.KeyBasedClientRequest;
import com.hazelcast.client.RetryableRequest;

import java.security.Permission;

abstract class AbstractCacheRequest extends KeyBasedClientRequest implements RetryableRequest {

    protected String name;


    public AbstractCacheRequest() {
    }

    public AbstractCacheRequest(String name) {
        this.name = name;
    }

    public final int getFactoryId() {
        return CachePortableHook.F_ID;
    }

    public final String getServiceName() {
        return CacheService.SERVICE_NAME;
    }

    @Override
    public Permission getRequiredPermission() {
        return null;
    }

}