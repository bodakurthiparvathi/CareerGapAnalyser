import api from './api';
import {
  ApiResponse,
  ResumeData,
  PendingSkillUpdate,
  ResumeTailorResponse,
  ResumeVersion,
} from '../types';

export const resumeService = {
  async extractProfile(): Promise<ResumeData> {
    const res = await api.get<ApiResponse<ResumeData>>('/resume/extract-profile');
    return res.data.data;
  },

  async getPendingSkills(currentResume?: ResumeData): Promise<PendingSkillUpdate[]> {
    const res = await api.post<ApiResponse<PendingSkillUpdate[]>>(
      '/resume/pending-skills',
      currentResume || {}
    );
    return res.data.data;
  },

  async tailorResume(payload: {
    targetJobTitle: string;
    targetJobDescription: string;
    resumeData: ResumeData;
  }): Promise<ResumeTailorResponse> {
    const res = await api.post<ApiResponse<ResumeTailorResponse>>('/resume/tailor', payload);
    return res.data.data;
  },

  async saveVersion(payload: {
    title: string;
    targetJobTitle?: string;
    targetJobDescription?: string;
    changeSummary?: string;
    resumeData: ResumeData;
  }): Promise<ResumeVersion> {
    const res = await api.post<ApiResponse<ResumeVersion>>('/resume/versions', payload);
    return res.data.data;
  },

  async getVersions(): Promise<ResumeVersion[]> {
    const res = await api.get<ApiResponse<ResumeVersion[]>>('/resume/versions');
    return res.data.data;
  },

  async getVersion(id: number): Promise<ResumeVersion> {
    const res = await api.get<ApiResponse<ResumeVersion>>(`/resume/versions/${id}`);
    return res.data.data;
  },

  async downloadVersionPdf(id: number, filename = 'resume.pdf'): Promise<void> {
    const res = await api.get(`/resume/versions/${id}/pdf`, {
      responseType: 'blob',
    });
    const blob = new Blob([res.data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },

  async exportPdf(resumeData: ResumeData, filename = 'resume.pdf'): Promise<void> {
    const res = await api.post('/resume/export-pdf', resumeData, {
      responseType: 'blob',
    });
    const blob = new Blob([res.data], { type: 'application/pdf' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  },
};

export default resumeService;

