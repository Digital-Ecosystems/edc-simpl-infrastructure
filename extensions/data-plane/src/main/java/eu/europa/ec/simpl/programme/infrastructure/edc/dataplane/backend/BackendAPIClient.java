/*
 *  Copyright (c) 2024 IONOS
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *      IONOS
 *
 */

package eu.europa.ec.simpl.programme.infrastructure.edc.dataplane.backend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.spi.EdcException;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.lang.String.format;

public class BackendAPIClient {

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    private final Request.Builder requestBuilder = new Request.Builder();

    public BackendAPIClient(OkHttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    public ScriptTriggerResponse sendTriggerRequest(String endpoint, String authToken, String deploymentScriptId, String requesterUniqueId, String requesterEmail) {

        var payload = new ScriptTriggerRequest(deploymentScriptId, requesterUniqueId, requesterEmail);

        try {
            return sendRequest(endpoint, authToken, requesterUniqueId, payload, ScriptTriggerResponse.class);
        } catch (Exception e) {
            throw new EdcException(format("Error sending script trigger request to endpoint %s", endpoint), e);
        }
    }

    private <T> T sendRequest(String endpoint, String authToken, String requesterUniqueId, Object payload, Class<T> clazz) throws Exception {

        var request = buildRequest(endpoint, authToken, payload);

        try (var response = httpClient.newCall(request).execute()) {
            if (response.body() == null) {
                throw new EdcException(format("Response without body. requesterUniqueId: %s, statusCode: %s", requesterUniqueId, response.code()));
            }

            if (response.isSuccessful()) {
                return objectMapper.readValue(response.body().string(), clazz);
            } else {
                throw new EdcException(format("Unexpected error: requesterUniqueId: %s, statusCode: %s, message: %s", requesterUniqueId, response.code(), response.message()));
            }
        }
    }

    private Request buildRequest(String endpoint, String authToken, Object payload) {

        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new EdcException("Error parsing payload to JSON", e);
        }

        var requestBody = RequestBody.create(json, MediaType.get(APPLICATION_JSON));
        return requestBuilder
                .url(endpoint)
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(AUTHORIZATION, "Bearer " + authToken)
                .post(requestBody)
                .build();
    }

    public record ScriptTriggerRequest(String deploymentScriptId, String requesterUniqueId, String requesterEmail) {
    }

    public record ScriptTriggerResponse(Boolean success, String message) {
    }
}