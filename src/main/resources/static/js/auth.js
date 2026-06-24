// Auth utility for Civilization OS
window.civos = window.civos || {};

civos.getToken = async function() {
    let token = localStorage.getItem('civos_token');
    if (!token) {
        try {
            let resp = await fetch('/api/v1/auth/connect', { method: 'POST' });
            let data = await resp.json();
            token = data.token;
            localStorage.setItem('civos_token', token);
            localStorage.setItem('civos_clientId', data.clientId);
        } catch(e) {
            console.error('Auth failed:', e);
            return null;
        }
    }
    return token;
};

civos.apiFetch = async function(url, options = {}) {
    const token = await civos.getToken();
    if (token) {
        options.headers = { ...options.headers, 'Authorization': 'Bearer ' + token };
    }
    return fetch(url, options);
};

civos.getTokenSync = function() {
    return localStorage.getItem('civos_token');
};
