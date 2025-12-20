const API_BASE_URL = 'http://localhost:8080/api';

class ApiService {
  // Auth endpoints
  async signup(username, email, password) {
    const response = await fetch(`${API_BASE_URL}/auth/signup`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, email, password })
    });
    return response.json();
  }

  async login(username, password) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    return response.json();
  }

  // Analysis endpoints
  async analyzeCode(code) {
    const response = await fetch(`${API_BASE_URL}/analyze`, {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: code
    });
    return response.json();
  }

  async refactorCode(code, token) {
    const headers = { 'Content-Type': 'text/plain' };
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    
    const response = await fetch(`${API_BASE_URL}/refactor`, {
      method: 'POST',
      headers,
      body: code
    });
    return response.json();
  }

  async getHistory(token) {
    const response = await fetch(`${API_BASE_URL}/history`, {
      method: 'GET',
      headers: { 'Authorization': `Bearer ${token}` }
    });
    return response.json();
  }
}

export default new ApiService();