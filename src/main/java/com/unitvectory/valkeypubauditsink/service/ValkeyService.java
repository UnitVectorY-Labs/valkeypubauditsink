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
package com.unitvectory.valkeypubauditsink.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * The Valkey Service
 * 
 * @author Jared Hatfield (UnitVectorY Labs)
 */
@Service
public class ValkeyService {

    @Autowired
    private ReactiveStringRedisTemplate redisTemplate;

    @Value("${valkey.prefix:doc}")
    private String prefix;

    public void process(JSONObject json) {
        String database = json.optString("database", null);
        String documentPath = json.optString("documentPath", null);

        if (database == null || documentPath == null) {
            return;
        }

        // SHA256 hash the database and document path
        String key = this.prefix + ":" + DigestUtils.sha256Hex(database + "/" + documentPath);

        if (!json.has("value") || json.isNull("value")) {
            // Delete
            redisTemplate.opsForValue().delete(key).block();
        } else {
            // Insert or Update
            String value = json.getJSONObject("value").toString();
            redisTemplate.opsForValue().set(key, value).block();

            // TODO: Deteremine how to have GSIs populate
        }
    }
}
