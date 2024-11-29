[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Work In Progress](https://img.shields.io/badge/Status-Work%20In%20Progress-yellow)](https://guide.unitvectorylabs.com/bestpractices/status/#work-in-progress)

# valkeypubauditsink

Ingests Pub/Sub audit JSON events and synchronizes the records into Valkey (Redis).

## References

- [firepubauditsource](https://github.com/UnitVectorY-Labs/firepubauditsource) - Publishes Firestore data changes to Pub/Sub as JSON audit records for downstream processing.
- [firepubauditsource-tofu](https://github.com/UnitVectorY-Labs/firepubauditsource-tofu) - A module for OpenTofu that deploys firepubauditsource to GCP Cloud Run, along with configuring essential services including Eventarc for Firestore and Pub/Sub.

## Overview

This application is a Cloud Run service that listens to Pub/Sub messages from the Pub/Sub topic created by [firepubauditsource](https://github.com/UnitVectorY-Labs/firepubauditsource). It processes the messages and synchronizes the records into Valkey (Redis).

## Configuration

This application is run as a docker container and allow the following environment variables to be set:

- `VALKEY_HOST`: The hostname of the Valkey (Redis) instance. (default: `localhost`)
- `VALKEY_PORT`: The port of the Valkey (Redis) instance. (default: `6379`)
- `VALKEY_PREFIX`: The prefix to use for keys in Valkey (Redis) to store the documents. (default: `doc`)

## Key Design

The documents stored in Firestore live in a collection and are identified by a document id.  The documents are stored as JSON objects in Valkey (Redis) and are identified by keys.

The keys use the following format:

```
doc:SHA256(<collection>/<document_id>)
```

The reasoning behind this is that the keys should be deterministic and unique for each document.  The SHA256 hash is used to ensure that the key is a fixed length and that it is unique for each document. The prefix can be changed using the `VALKEY_PREFIX` environment variable.

## Limitations

- This application does not support authentication or encryption for the Valkey (Redis) instance yet.
