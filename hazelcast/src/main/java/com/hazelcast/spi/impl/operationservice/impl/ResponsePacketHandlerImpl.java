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

package com.hazelcast.spi.impl.operationservice.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.Packet;
import com.hazelcast.spi.impl.PacketHandler;
import com.hazelcast.spi.impl.operationservice.impl.responses.BackupResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.CallTimeoutResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.ErrorResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.NormalResponse;
import com.hazelcast.spi.impl.operationservice.impl.responses.Response;

/**
 * Responsible for handling responses.
 */
final class ResponsePacketHandlerImpl implements PacketHandler {

    private final ILogger logger;
    private final InternalSerializationService serializationService;
    private final InvocationRegistry invocationRegistry;

    public ResponsePacketHandlerImpl(ILogger logger,
                                     InternalSerializationService serializationService,
                                     InvocationRegistry invocationRegistry) {
        this.logger = logger;
        this.serializationService = serializationService;
        this.invocationRegistry = invocationRegistry;
    }

    @Override
    public void handle(Packet packet) throws Exception {
        Response response = serializationService.toObject(packet);
        Address sender = packet.getConn().getEndPoint();
        try {
            if (response instanceof NormalResponse) {
                NormalResponse normalResponse = (NormalResponse) response;
                invocationRegistry.notifyNormalResponse(
                        normalResponse.getCallId(),
                        normalResponse.getValue(),
                        normalResponse.getBackupCount(),
                        sender);
            } else if (response instanceof BackupResponse) {
                invocationRegistry.notifyBackupComplete(response.getCallId());
            } else if (response instanceof CallTimeoutResponse) {
                invocationRegistry.notifyCallTimeout(response.getCallId(), sender);
            } else if (response instanceof ErrorResponse) {
                ErrorResponse errorResponse = (ErrorResponse) response;
                invocationRegistry.notifyErrorResponse(
                        errorResponse.getCallId(),
                        errorResponse.getCause(),
                        sender);
            } else {
                logger.severe("Unrecognized response: " + response);
            }
        } catch (Throwable e) {
            logger.severe("While processing response...", e);
        }
    }
}
