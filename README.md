# pagoPA IUV Generator

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=pagopa_pagopa-iuvgenerator&metric=alert_status)](https://sonarcloud.io/dashboard?id=pagopa_pagopa-iuvgenerator)
[![Integration Tests](https://github.com/pagopa/pagopa-iuvgenerator/actions/workflows/integration_test.yml/badge.svg?branch=main)](https://github.com/pagopa/pagopa-iuvgenerator/actions/workflows/integration_test.yml)

A service that permits to generate unique IUV codes for each creditor institution.


---

## Api Documentation 📖

See the [OpenApi 3 here.](https://editor.swagger.io/?url=https://raw.githubusercontent.com/pagopa/pagopa-iuvgenerator/main/openapi/openapi.json)

---

## Technology Stack

- Java 17
- Spring Boot
- Spring Web

---

## Start Project Locally 🚀

### Prerequisites

- docker

### Run docker container

from `./docker` directory

`sh ./run_docker.sh local`

---

## Develop Locally 💻

### Prerequisites

- git
- maven
- jdk-17

### Run the project

Start the springboot application with this command:

`mvn spring-boot:run -Dspring-boot.run.profiles=local`

### Spring Profiles

- **local**: to develop locally.
- _default (no profile set)_: The application gets the properties from the environment (for Azure).

### Testing 🧪

#### Unit testing

To run the **Junit** tests:

`mvn clean verify`

#### Integration testing

From `./integration-test/src`

1. `yarn install`
2. `yarn test`

#### Performance testing

install [k6](https://k6.io/) and then from `./performance-test/src`

1. `k6 run --env VARS=local.environment.json --env TEST_TYPE=./test-types/load.json main_scenario.js`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file
