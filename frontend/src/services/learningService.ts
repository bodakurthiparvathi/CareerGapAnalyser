import api from './api';
import {
  ApiResponse,
  LearningDashboardData,
  LearningModule,
  MockTest,
  MockTestResult,
  NextBestAction,
  PracticeEvaluationResult,
  PracticeSubmissionRequest,
  SkillVerificationRequest,
  SkillVerificationResponse,
  MentorMessage,
} from '../types';

export const learningService = {
  async getDashboard(): Promise<LearningDashboardData> {
    const res = await api.get<ApiResponse<LearningDashboardData>>('/learning/dashboard');
    return res.data.data;
  },

  async getRoadmapModules(roadmapId: number): Promise<LearningModule[]> {
    const res = await api.get<ApiResponse<LearningModule[]>>(`/learning/roadmaps/${roadmapId}/modules`);
    return res.data.data;
  },

  async updateDailyStudyTime(roadmapId: number, dailyStudyMinutes: number): Promise<LearningDashboardData> {
    const res = await api.put<ApiResponse<LearningDashboardData>>(
      `/learning/roadmaps/${roadmapId}/study-preferences`,
      { dailyStudyMinutes }
    );
    return res.data.data;
  },

  async getModuleById(moduleId: number): Promise<LearningModule> {
    const res = await api.get<ApiResponse<LearningModule>>(`/learning/modules/${moduleId}`);
    return res.data.data;
  },

  async evaluatePractice(moduleId: number, data: PracticeSubmissionRequest): Promise<PracticeEvaluationResult> {
    const res = await api.post<ApiResponse<PracticeEvaluationResult>>(
      `/learning/modules/${moduleId}/practice/evaluate`,
      data
    );
    return res.data.data;
  },

  async updateProgress(moduleId: number, data: { status: string; notes?: string }): Promise<LearningModule> {
    const res = await api.post<ApiResponse<LearningModule>>(`/learning/modules/${moduleId}/progress`, data);
    return res.data.data;
  },

  async verifySkill(data: SkillVerificationRequest): Promise<SkillVerificationResponse> {
    const res = await api.post<ApiResponse<SkillVerificationResponse>>('/learning/verify-skill', data);
    return res.data.data;
  },

  async getVerifications(): Promise<SkillVerificationResponse[]> {
    const res = await api.get<ApiResponse<SkillVerificationResponse[]>>('/learning/verifications');
    return res.data.data;
  },

  async generateMockTest(targetRole?: string): Promise<MockTest> {
    const params = targetRole ? `?targetRole=${encodeURIComponent(targetRole)}` : '';
    const res = await api.post<ApiResponse<MockTest>>(`/learning/mock-tests/generate${params}`);
    return res.data.data;
  },

  async submitMockTest(testId: number, answers: Record<number, number>): Promise<MockTestResult> {
    const res = await api.post<ApiResponse<MockTestResult>>(`/learning/mock-tests/${testId}/submit`, { answers });
    return res.data.data;
  },

  async getNextBestAction(): Promise<NextBestAction> {
    const res = await api.get<ApiResponse<NextBestAction>>('/learning/next-best-action');
    return res.data.data;
  },

  async sendMentorMessage(message: string): Promise<{ response: string; suggestedNextActions: string[] }> {
    const res = await api.post<ApiResponse<{ response: string; suggestedNextActions: string[] }>>(
      '/mentor/chat',
      { message }
    );
    return res.data.data;
  },

  async getMentorHistory(): Promise<MentorMessage[]> {
    const res = await api.get<ApiResponse<MentorMessage[]>>('/mentor/history');
    return res.data.data;
  },
};
