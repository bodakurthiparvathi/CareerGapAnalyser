export interface SkillItem {
  skillName: string;
  proficiencyLevel: 'Beginner' | 'Intermediate' | 'Advanced' | 'Expert';
  category: 'Technical' | 'Soft';
}

export interface ProjectItem {
  id?: number;
  title: string;
  description: string;
  technologies: string;
  githubUrl?: string;
  liveUrl?: string;
}

export interface ExperienceItem {
  id?: number;
  title: string;
  company: string;
  duration: string;
  description: string;
}

export interface CertificationItem {
  id?: number;
  name: string;
  issuer: string;
  issueYear?: number;
  credentialUrl?: string;
}

export interface User {
  id: number;
  email: string;
  fullName: string;
  phoneNumber?: string;
  location?: string;
  degree?: string;
  branch?: string;
  college?: string;
  graduationYear?: number;
  academicStatus?: string;
  targetRole?: string;
  preferredIndustry?: string;
  yearsOfExperience?: number;
  education?: string;
  bio?: string;
  softSkills?: string[];
  onboardingCompleted?: boolean;
  technicalSkills?: SkillItem[];
  projects?: ProjectItem[];
  experiences?: ExperienceItem[];
  certifications?: CertificationItem[];
  createdAt: string;
}

export interface OnboardingRequest {
  fullName?: string;
  phoneNumber?: string;
  location?: string;
  degree?: string;
  branch?: string;
  college?: string;
  graduationYear?: number;
  academicStatus?: string;
  targetRole: string;
  preferredIndustry?: string;
  yearsOfExperience?: number;
  bio?: string;
  technicalSkills: SkillItem[];
  softSkills: string[];
  projects: ProjectItem[];
  experiences: ExperienceItem[];
  certifications: CertificationItem[];
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface RoadmapStage {
  stageNumber: number;
  stageName: string;
  duration: string;
  focusArea: string;
  learningGoals: string[];
  recommendedResources: string[];
  milestoneProjects: string[];
}

export interface Roadmap {
  id?: number;
  title: string;
  targetRole: string;
  estimatedWeeks: number;
  stages: RoadmapStage[];
}

export interface SkillDetail {
  skillName: string;
  currentProficiency: string;
  requiredProficiency: string;
  classification: 'Strong' | 'Developing' | 'Missing' | 'Optional';
  evidence: string;
  confidenceScore: number;
  priorityScore: number;
  priorityLevel: 'High' | 'Medium' | 'Low';
  category: 'Technical' | 'Soft';
  importance: 'Essential' | 'Desirable' | 'Optional';
  estimatedEffort: string;
  explanation: string;
  recommendedAction: string;
}

export interface SkillAnalysis {
  id: number;
  targetRole: string;
  overallMatchScore: number;
  technicalScore: number;
  softSkillsScore: number;
  readinessLevel: string;
  skills?: SkillDetail[];
  strongSkills?: SkillDetail[];
  developingSkills?: SkillDetail[];
  missingSkills?: SkillDetail[];
  optionalSkills?: SkillDetail[];
  highPriorityGaps?: SkillDetail[];
  strongCount?: number;
  developingCount?: number;
  missingCount?: number;
  optionalCount?: number;
  calculationCriteria?: string;
  scoreDisclaimer?: string;
  presentSkills: string[];
  missingSkillsList?: string[];
  strengths: string[];
  weaknesses: string[];
  recommendations: string[];
  roadmap?: Roadmap;
  createdAt: string;
}

// ----------------- PERSONALIZED LEARNING INTERFACES ----------------- //

export interface PracticeQuestion {
  questionIndex: number;
  type: 'MULTIPLE_CHOICE' | 'CONCEPTUAL' | 'SHORT_ANSWER' | 'SCENARIO_BASED';
  question: string;
  options?: string[];
  correctOptionIndex?: number;
  sampleAnswer?: string;
  explanation?: string;
  scenarioContext?: string;
}

export interface LearningModule {
  id: number;
  roadmapId: number;
  skillName: string;
  category: string;
  orderIndex: number;
  estimatedHours: string;
  currentProficiency: string;
  targetProficiency: string;
  whyItMatters: string;
  learningObjectives: string[];
  suitableResources: { title: string; type: string; url: string; verification?: string }[];
  contentExplanations: string;
  practiceExercises: string[];
  practiceQuestions: PracticeQuestion[];
  dependencies: string[];
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  userScore: number;
  userNotes?: string;
  isVerified: boolean;
  verificationBadge?: string;
}

export interface PracticeSubmissionRequest {
  questionIndex: number;
  questionType: string;
  userAnswer: string;
}

export interface PracticeEvaluationResult {
  questionIndex: number;
  questionType: string;
  correct: boolean;
  score: number;
  feedback: string;
  correctAnswerExplanation: string;
  updatedModuleStatus: string;
  streakDays: number;
}

export interface SkillVerificationRequest {
  skillName: string;
  targetRole?: string;
  verificationMethod: 'ASSESSMENT_SCORE' | 'PROJECT_EVIDENCE';
  assessmentScore?: number;
  projectTitle?: string;
  projectGithubUrl?: string;
  projectLiveUrl?: string;
  projectEvidenceDetails?: string;
}

export interface SkillVerificationResponse {
  id: number;
  skillName: string;
  targetRole: string;
  status: 'VERIFIED' | 'PENDING_REVIEW' | 'UNVERIFIED' | 'REJECTED';
  verificationMethod: string;
  assessmentScore?: number;
  projectTitle?: string;
  projectGithubUrl?: string;
  projectLiveUrl?: string;
  verificationBadge?: string;
  evaluatorFeedback?: string;
  verifiedAt?: string;
}

export interface MockTestQuestion {
  questionId: number;
  skill: string;
  question: string;
  options: string[];
  category: string;
}

export interface MockTest {
  id: number;
  targetRole: string;
  title: string;
  description: string;
  totalQuestions: number;
  timeLimitMinutes: number;
  questions: MockTestQuestion[];
}

export interface MockTestReviewItem {
  questionId: number;
  skill: string;
  question: string;
  options: string[];
  selectedOptionIndex?: number;
  correctOptionIndex: number;
  correct: boolean;
  explanation: string;
}

export interface MockTestResult {
  attemptId: number;
  testId: number;
  testTitle: string;
  targetRole: string;
  score: number;
  totalQuestions: number;
  correctCount: number;
  passed: boolean;
  strengths: string[];
  weaknesses: string[];
  revisionTopics: string[];
  questionReviews: MockTestReviewItem[];
  attemptedAt: string;
}

export interface NextBestAction {
  title: string;
  description: string;
  actionType: 'ANALYZE_GAP' | 'START_MODULE' | 'COMPLETE_PRACTICE' | 'VERIFY_SKILL' | 'TAKE_MOCK_TEST' | 'REVISE_TOPIC' | 'CHAT_MENTOR';
  targetUrl: string;
  priority: 'HIGH' | 'MEDIUM' | 'RECOMMENDED';
  badge: string;
  targetId?: number;
}

export interface LearningDashboardData {
  roadmapId?: number;
  targetRole?: string;
  roadmapTitle?: string;
  estimatedWeeks?: number;
  dailyStudyMinutes: number;
  totalModules: number;
  completedModules: number;
  inProgressModules: number;
  progressPercentage: number;
  currentStreakDays: number;
  longestStreakDays: number;
  totalPracticeCount: number;
  totalMockTestsCount: number;
  totalHoursLearned: number;
  verifiedSkillsCount: number;
  verifiedSkills: SkillVerificationResponse[];
  nextBestAction?: NextBestAction;
  modules: LearningModule[];
  latestTestResult?: MockTestResult;
}

export interface MentorMessage {
  id?: number;
  sender: 'USER' | 'MENTOR';
  message: string;
  suggestedActions?: string[];
  timestamp: string;
}

export interface DashboardStats {
  totalAnalyses: number;
  averageReadiness: number;
  skillsIdentified: number;
  skillsGapCount: number;
  topTargetRole: string;
  currentReadinessLevel: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface ResumeSkillItem {
  skillName: string;
  category: string;
  proficiencyLevel: string;
  skillType: 'LEARNED' | 'ASSESSED' | 'VERIFIED' | 'CORE';
  verified?: boolean;
  verificationBadge?: string;
}

export interface ResumeExperience {
  id?: number;
  role: string;
  company: string;
  location?: string;
  duration: string;
  bullets: string[];
}

export interface ResumeProject {
  id?: number;
  title: string;
  technologies: string;
  githubUrl?: string;
  liveUrl?: string;
  bullets: string[];
}

export interface ResumeEducation {
  institution: string;
  degree: string;
  branch: string;
  graduationYear: string;
  gradeGpa?: string;
}

export interface ResumeCertification {
  name: string;
  issuer: string;
  issueYear?: string;
  credentialUrl?: string;
}

export interface ResumeData {
  fullName: string;
  email: string;
  phoneNumber?: string;
  location?: string;
  linkedinUrl?: string;
  githubUrl?: string;
  portfolioUrl?: string;
  summary: string;
  skills: ResumeSkillItem[];
  experiences: ResumeExperience[];
  projects: ResumeProject[];
  education: ResumeEducation[];
  certifications: ResumeCertification[];
}

export interface PendingSkillUpdate {
  skillName: string;
  category: string;
  skillType: 'LEARNED' | 'ASSESSED' | 'VERIFIED';
  proficiencyLevel: string;
  source: string;
  suggestedBulletOrEntry: string;
  verificationBadge?: string;
  alreadyInResume: boolean;
  recommendationNote: string;
}

export interface BulletImprovement {
  section: 'PROJECTS' | 'EXPERIENCE';
  itemTitle: string;
  originalBullet: string;
  improvedBullet: string;
  rationale: string;
  accepted?: boolean;
}

export interface ResumeTailorResponse {
  tailoredResume: ResumeData;
  targetKeywords: string[];
  matchedKeywords: string[];
  missingKeywords: string[];
  keywordRecommendations: string[];
  bulletImprovements: BulletImprovement[];
  matchScore: number;
  alignmentSummary?: string;
}

export interface ResumeVersion {
  id: number;
  versionNumber: number;
  title: string;
  targetJobTitle?: string;
  targetJobDescription?: string;
  changeSummary?: string;
  tailoredKeywords?: string[];
  resumeData: ResumeData;
  createdAt: string;
}
