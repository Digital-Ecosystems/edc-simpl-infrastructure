# SIMPL Extension for Eclipse Dataspace Connector - Infrastructure

This repository contains the SIMPL Extension that works with the Eclipse Dataspace Connector allowing operations of provisioning/decomissioning of cloud resources.

## Based on the following

- [https://github.com/eclipse-dataspaceconnector/DataSpaceConnector](https://github.com/eclipse-dataspaceconnector/DataSpaceConnector) - v0.7.2;


## Requirements

You will need the following:
- Java Development Kit (JDK) 17 or higher;
- Docker;
- GIT;
- Linux shell or PowerShell;

## Folders Description

### `extensions`
Contains the source code of the SIMPL Infra Extension.

### `launchers`
Contains the required instructions to run an EDC Connector or create an EDC docker image with the Extension.

## Modules, Dependencies and Usage

### Modules
The extension has the following modules:
| Module name                                                     | Description                                |
|-----------------------------------------------------------------|--------------------------------------------|
| `eu.europa.ec.simpl.programme.infrastructure.edc:control-plane` | Allows the creation of assets for the SIMPL project |
| `eu.europa.ec.simpl.programme.infrastructure.edc:data-plane`    | Allows the triggering process of a deploymemt script inside the SIMPL context|

### Dependencies
The extension has the following dependencies:

| Module name                                  | Description                                                      |
|----------------------------------------------|------------------------------------------------------------------|
| `org.eclipse.edc:control-plane-core`            | Main features of the control plane |
| `org.eclipse.edc:data-plane-core`            | Main features of the data plane |

### Usage
Payload of the creation of an asset, using the /management/v3/assets/ endpoint of the Connector:
```
Sample:
{
    "@context": {
        "@vocab": "https://w3id.org/edc/v0.0.1/ns/")
    },
    "@id": "asset-671",
    "properties": {
        "name": "Test Infrastructure Asset"
    },
    "dataAddress": {
        "type": "Infrastructure",
        "provisioningAPI": "http://localhost:8080/scripts/trigger",
        "deploymentScriptId": "0806537b-a3a6-4527-a6e4-c404647558be"
    }
}
```
| Field name                                                     | Description                                |
|-----------------------------------------------------------------|--------------------------------------------|
| @id | Id of the asset |
| properties.name    | Name of the asset |
| dataAddress.type    | This extension uses the `Infrastructure` designation  |
| dataAddress.provioningAPI    | URL of the Cloud provider's API that will trigger the deployment script  |
| dataAddress.deploymentScriptId    | Id the deployment script  |

Triggering of the `deployment script`, using the management/v3/transferprocesses endpoint of the Connector:
```
Sample
{
    "@context":{
        "edc":"https://w3id.org/edc/v0.0.1/ns/"
    },
    "connectorId":"provider",
    "counterPartyAddress":"http://localhost:8282/protocol",
    "contractId":"3186afb5-7b10-4665-b07b-233f5665eb98",
    "protocol":"dataspace-protocol-http",
    "transferType": "Infrastructure-PUSH",
    "dataDestination":{
        "type":"Infrastructure",
        "consumerEmail": "someuser@xptodomain.com"
    }
}
```
| Field name                                                     | Description                                |
|-----------------------------------------------------------------|--------------------------------------------|
| transferType | This extension uses the `Infrastructure-PUSH` designation  |
| dataDestination.type    | This extension uses the `Infrastructure` designation  |
| dataDestination.consumerEmail    | Email address that will receive the status of the deployment of the `deploymentScriptId`  |


Note: the scope of this repo is NOT to exaplain the complete flows (and payloads) of the EDC Connector. If you want to know more please take a look at the [IONOS S3 Extension](https://github.com/Digital-Ecosystems/edc-ionos-s3).

## Building and Running

```bash
git clone git@github.com:ionos-cloud/edc-simpl-infrastructure.git
cd extensions
./gradlew clean build
```


