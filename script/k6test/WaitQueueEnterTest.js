import http from 'k6/http';
import crypto from 'k6/crypto';

let baseUrl = "http://localhost:8080";

export function setup() {
    let key = signUp("Jiwon");
    console.log(`secret=${key.clientSecret}`)

    let eventId = createEvent(key);
    console.log(`eventId=${eventId}`)

    let tokens = []
    for (let i = 0; i < 5; i++) {
        let token = createToken(key)
        tokens.push(token)
    }
    return {
        'key': key,
        'eventId': eventId,
        'tokens': tokens
    }
}

export default function (setupData) {

}

function createToken(key) {
    let createTokenUrl = baseUrl + "/server/v1/auth/token"
    let signature = createHmacSignature(createTokenUrl, key.clientSecret);

    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${key.clientId}:${signature}`
        },
    };

    let response = http.post(createTokenUrl, null, headers);
    return response.json().token.accessToken;
}

function createEvent(key) {
    let createEventUrl = baseUrl + "/server/v1/events"
    let signature = createHmacSignature(createEventUrl, key.clientSecret);

    let startTime = new Date()
    let endTime = new Date(startTime.valueOf())
    endTime.setDate(endTime.getDate() + 2);

    const body = JSON.stringify({
        'startTime': startTime.toISOString(),
        'endTime': endTime.toISOString(),
        'jobQueueSize': 500,
        'jobQueueLimitTime': 300
    });
    const headers = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `${key.clientId}:${signature}`
        },
    };

    let response = http.post(createEventUrl, body, headers);
    return response.json().eventId;
}

function signUp(name) {
    const signMemberUrl = baseUrl + "/server/v1/members";
    const body = JSON.stringify({
        name: name,
    });
    const headers = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    let response = http.post(signMemberUrl, body, headers);
    return response.json().key
}

function createHmacSignature(url, secret) {
    return crypto.hmac('sha256', secret, url, 'base64');
}