# K6 Load Tests

Use this script to run the load tests with [k6](https://k6.io/docs/).

## Setup your Local Environment

1. Configure your environment through `*.environment.json` file.
2. Start the App Function.

_Note: You can use `local.environment.json` to launch the tests in your local environment_

## Launch the Tests 🧪

Launch the following command from the `load-test/src` directory.

`k6 run --env VARS=<env>.environment.json --env TEST_TYPE=./test-types/<type>.json main.js`

- As `<env>`, you can use: `local` or `dev` or whatever you configure...
- As `<type>`, you can use: `load.json` or `stress.json` or `spike.json`

### Example

`k6 run --env VARS=uat.environment.json --env TEST_TYPE=./test-types/load.json main.js
`
