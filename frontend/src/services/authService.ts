import api from './api';
import { ApiResponse, AuthResponse, User } from '../types';

export const authService = {
  async register(data: { fullName: string; email: string; password: string; targetRole?: string }) {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/register', data);
    if (res.data.data) {
      localStorage.setItem('careergap_token', res.data.data.token);
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data.user));
    }
    return res.data;
  },

  async login(data: { email: string; password: string }) {
    const res = await api.post<ApiResponse<AuthResponse>>('/auth/login', data);
    if (res.data.data) {
      localStorage.setItem('careergap_token', res.data.data.token);
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data.user));
    }
    return res.data;
  },

  async forgotPassword(email: string) {
    const res = await api.post<ApiResponse<{ resetToken: string; email: string }>>('/auth/forgot-password', { email });
    return res.data;
  },

  async resetPassword(data: { token: string; newPassword: string }) {
    const res = await api.post<ApiResponse<void>>('/auth/reset-password', data);
    return res.data;
  },

  async getCurrentUser() {
    const res = await api.get<ApiResponse<User>>('/auth/me');
    if (res.data.data) {
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data));
    }
    return res.data.data;
  },

  logout() {
    localStorage.removeItem('careergap_token');
    localStorage.removeItem('careergap_user');
  },

  getStoredUser(): User | null {
    const userStr = localStorage.getItem('careergap_user');
    if (!userStr) return null;
    try {
      return JSON.parse(userStr);
    } catch {
      return null;
    }
  },

  isAuthenticated(): boolean {
    return !!localStorage.getItem('careergap_token');
  },
};
