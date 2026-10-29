import api from './api';
import { ApiResponse, DashboardStats, OnboardingRequest, SkillAnalysis, User } from '../types';

export const analysisService = {
  async uploadResume(file: File) {
    const formData = new FormData();
    formData.append('file', file);

    const res = await api.post<ApiResponse<{ resumeId: number; fileName: string; parsedText: string }>>(
      '/analysis/upload-resume',
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      }
    );
    return res.data.data;
  },

  async runAnalysis(data: { targetRole: string; jobDescription: string; resumeText?: string; resumeId?: number }) {
    const res = await api.post<ApiResponse<SkillAnalysis>>('/analysis/analyze', data);
    return res.data.data;
  },

  async getHistory() {
    const res = await api.get<ApiResponse<SkillAnalysis[]>>('/analysis/history');
    return res.data.data;
  },

  async getAnalysisById(id: number) {
    const res = await api.get<ApiResponse<SkillAnalysis>>(`/analysis/${id}`);
    return res.data.data;
  },

  async getDashboardStats() {
    const res = await api.get<ApiResponse<DashboardStats>>('/users/dashboard-stats');
    return res.data.data;
  },

  async saveOnboarding(data: OnboardingRequest) {
    const res = await api.post<ApiResponse<User>>('/users/onboarding', data);
    if (res.data.data) {
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data));
    }
    return res.data.data;
  },

  async getFullProfile() {
    const res = await api.get<ApiResponse<User>>('/users/profile');
    if (res.data.data) {
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data));
    }
    return res.data.data;
  },

  async updateProfile(data: Partial<User>) {
    const res = await api.put<ApiResponse<User>>('/users/profile', data);
    if (res.data.data) {
      localStorage.setItem('careergap_user', JSON.stringify(res.data.data));
    }
    return res.data.data;
  },
};
