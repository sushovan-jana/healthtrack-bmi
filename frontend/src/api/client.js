import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  withCredentials: true, // Crucial for receiving and sending HttpOnly cookies (JWT)
  timeout: 30000, // 30 second global timeout — prevents infinite loading on slow Render free tier
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Request interceptor: attach JWT from localStorage as Authorization header (cross-domain fallback)
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt-token');
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor to handle session expiration or global errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    // If the backend returns 401 (Unauthorized) or 403 (Forbidden) on doctor routes, 
    // it implies the session cookie is missing or invalid.
    if (error.response) {
      const { status, config } = error.response;
      const isPublicEndpoint = config.url.includes('/api/auth/login') || 
                               config.url.includes('/api/auth/register') ||
                               config.url.includes('/api/bmi/calculate');

      if ((status === 401 || status === 403) && !isPublicEndpoint) {
        // Clear doctor-specific auth states locally if unauthorized
        // We can dispatch a custom event that AuthContext will listen to
        window.dispatchEvent(new Event('auth-unauthorized'));
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;

