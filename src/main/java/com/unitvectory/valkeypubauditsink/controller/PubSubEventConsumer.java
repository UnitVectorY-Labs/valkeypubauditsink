/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.unitvectory.valkeypubauditsink.controller;

import java.util.Base64;
import java.util.Arrays;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
 
import com.google.protobuf.InvalidProtocolBufferException;
import com.unitvectory.valkeypubauditsink.model.PubSubPublish;
import com.unitvectory.valkeypubauditsink.service.ValkeyService;

import lombok.extern.slf4j.Slf4j;

/**
 * The Pub/Sub event controller
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@RestController
@Slf4j
public class PubSubEventConsumer {

    @Autowired
    private ValkeyService valkeyService;

    @PostMapping(value = "/pubsub", consumes = "application/json")
    public void handleFirestoreEvent(@RequestBody PubSubPublish data) throws InvalidProtocolBufferException {
        // Decode the Base64 encoded message data
        String jsonString = new String(Base64.getDecoder().decode(data.getMessage().getData()));

        // Parse the decoded string into a JSONObject
        JSONObject jsonObject = new JSONObject(jsonString);

        // Keep only the required attributes
        jsonObject.keySet().retainAll(Arrays.asList("timestamp", "database", "documentPath", "value", "oldValue"));

        // Process the  JSON into Valkey
        try {
            this.valkeyService.process(jsonObject);
        } catch (Exception e) {
            log.error("Failed to insert record into Valkey", e);
        }
    }
}