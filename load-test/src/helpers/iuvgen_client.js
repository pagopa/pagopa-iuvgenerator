import http from 'k6/http';

const ciCode = "12345678";

const params = {
    headers: {
        'Content-Type': 'application/json',
        'Ocp-Apim-Subscription-Key': 'abf9c660dd984ce4b6db84a77f8f11ee'
    },
};

export function getCiCode(id) {
	return ciCode + ('000'+id).slice(-3);
}

export function organizationsIuvGen(rootUrl, id, segregationCode, auxDigit) {
	const url = `${rootUrl}/${getCiCode(id)}/iuv`
	const payload = {
        "segregationCode": segregationCode,
        "auxDigit": auxDigit
	};

	return http.post(url, JSON.stringify(payload), params);
}
