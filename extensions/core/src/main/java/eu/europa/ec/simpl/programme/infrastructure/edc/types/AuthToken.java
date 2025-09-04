/*
 *  Copyright (c) 2022 IONOS
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

package eu.europa.ec.simpl.programme.infrastructure.edc.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.SecretToken;

@JsonTypeName("dataspaceconnector:simpl.edc.authtoken  ")
public class AuthToken implements SecretToken {

    private final String tokenValue;
    private final long expiration;

    public AuthToken(@JsonProperty("tokenValue") String tokenValue, @JsonProperty("expiration") long expiration) {
    	this.tokenValue = tokenValue;
        this.expiration = expiration;
    }
   
	public String getTokenValue() {
		return tokenValue;
	}
	
	@Override
	public long getExpiration() {
		return expiration;
	}
}
