
import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '1m', target: 5 },
        { duration: '2m', target: 10 },
        { duration: '3m', target: 20 },
        { duration: '2m', target: 30 },
        { duration: '2m', target: 0 },
    ],

    thresholds: {
        http_req_failed: ['rate<0.05'],
        checks: ['rate>0.95'],
    },
};

export default function () {
    const response = http.get(`${__ENV.BASE_URL}/user/40`, {
        headers: { Host: 'arch.homework' },
    });

    check(response, {
        'status is 200': (r) => r.status === 200,
    });

    sleep(1);
}
