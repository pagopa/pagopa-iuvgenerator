// 1. init code (once per VU)
// prepares the script: loading files, importing modules, and defining functions

import { check, sleep} from 'k6';
import { SharedArray } from 'k6/data';

import {
	organizationsIuvGen
} from "./helpers/iuvgen_client.js";

let myOptions = JSON.parse(open(__ENV.TEST_TYPE));
export let options = myOptions;


// read configuration
// note: SharedArray can currently only be constructed inside init code
// according to https://k6.io/docs/javascript-api/k6-data/sharedarray
const varsArray = new SharedArray('vars', function () {
	return JSON.parse(open(`./${__ENV.VARS}`)).environment;
});
// workaround to use shared array (only array should be used)
const vars = varsArray[0];
const rootUrl = `${vars.host}/${vars.basePath}`;


export function setup() {
	// 2. setup code (once)
	// The setup code runs, setting up the test environment (optional) and generating data
	// used to reuse code for the same VU

	// precondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly
}

function precondition() {
	// no pre conditions
}

function postcondition() {
	// no post conditions
}

export default function (data) {

	// precondition();


	// Get creditor institutions
	let response = organizationsIuvGen(rootUrl, __VU, 47, 3);
	check(response, {
		"status equals 201": response => response.status === 201,
		"body contains iuv": response => response.json().hasOwnProperty("iuv")
	});

	sleep(1)

	// postcondition();
}

export function teardown(data) {
	// 4. teardown code (once per script)
	// The teardown code runs, postprocessing data and closing the test environment.

	// postcondition is moved to default fn because in this stage
	// __VU is always 0 and cannot be used to create env properly

}
