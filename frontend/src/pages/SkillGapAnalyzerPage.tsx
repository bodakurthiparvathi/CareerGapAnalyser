import React, { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { analysisService } from '../services/analysisService';
import { SkillAnalysis } from '../types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Alert } from '../components/Alert';
import {
  Upload,
  FileText,
  BrainCircuit,
  Target,
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  ArrowRight,
  TrendingUp,
  BookOpen,
  RefreshCw,
  FileCheck,
} from 'lucide-react';

export const SkillGapAnalyzerPage: React.FC = () => {
  const { user } = useAuth();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [targetRole, setTargetRole] = useState(user?.targetRole || 'Full Stack Java Developer');
  const [jobDescription, setJobDescription] = useState('');
  const [resumeText, setResumeText] = useState('');
  const [uploadedFileName, setUploadedFileName] = useState<string | null>(null);
  const [resumeId, setResumeId] = useState<number | null>(null);

  const [activeTab, setActiveTab] = useState<'upload' | 'paste'>('upload');
  const [uploading, setUploading] = useState(false);
  const [analyzing, setAnalyzing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [analysisResult, setAnalysisResult] = useState<SkillAnalysis | null>(null);

  // If ?id= is present in URL, load saved analysis
  useEffect(() => {
    const analysisId = searchParams.get('id');
    if (analysisId) {
      const loadAnalysis = async () => {
        try {
          setAnalyzing(true);
          const data = await analysisService.getAnalysisById(Number(analysisId));
          setAnalysisResult(data);
          setTargetRole(data.targetRole);
        } catch (err: any) {
          setError(err.message || 'Failed to load analysis');
        } finally {
          setAnalyzing(false);
        }
      };
      loadAnalysis();
    }
  }, [searchParams]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    setError(null);
    setUploading(true);
    try {
      const res = await analysisService.uploadResume(file);
      setUploadedFileName(file.name);
      setResumeId(res.resumeId);
      setResumeText(res.parsedText);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to parse resume document');
    } finally {
      setUploading(false);
    }
  };

  const handleAnalyze = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!targetRole.trim()) {
      setError('Please provide a target role title.');
      return;
    }
    if (!jobDescription.trim()) {
      setError('Please paste the target job description.');
      return;
    }
    if (!resumeText.trim() && !resumeId) {
      setError('Please upload a resume or paste your resume text.');
      return;
    }

    setAnalyzing(true);
    try {
      const result = await analysisService.runAnalysis({
        targetRole,
        jobDescription,
        resumeText: resumeText.trim(),
        resumeId: resumeId || undefined,
      });
      setAnalysisResult(result);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to run analysis');
    } finally {
      setAnalyzing(false);
    }
  };

  const sampleJob = () => {
    setJobDescription(`We are looking for a Full Stack Java Developer to build high-scale cloud platforms.
Responsibilities:
- Build REST APIs using Spring Boot, JPA, and PostgreSQL.
- Create responsive frontend features using React, TypeScript, and Tailwind CSS.
- Deploy microservices using Docker, Kubernetes, and AWS.
- Practice CI/CD with Git, Jenkins, and GitHub.
- Write unit tests using JUnit and Mockito.
- Collaborate with agile cross-functional teams with strong communication and problem solving skills.`);
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold mb-2">
            <BrainCircuit className="w-3.5 h-3.5 text-emerald-600" />
            AI Semantic Gap Engine
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
            Skill Gap Analyzer
          </h1>
          <p className="text-sm text-slate-500">
            Compare your resume against any job description to discover exact skill gaps and calculate readiness.
          </p>
        </div>

        {analysisResult && (
          <button
            onClick={() => {
              setAnalysisResult(null);
              setJobDescription('');
            }}
            className="self-start md:self-auto px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold flex items-center gap-2 transition-colors"
          >
            <RefreshCw className="w-3.5 h-3.5" /> Start New Comparison
          </button>
        )}
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {!analysisResult ? (
        /* Form view */
        <form onSubmit={handleAnalyze} className="space-y-6">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            {/* Left Column: Resume Input */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-5">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="font-bold text-slate-900 text-base">Your Resume</h2>
                  <p className="text-xs text-slate-500">Upload PDF/DOCX or paste raw text</p>
                </div>
                <div className="flex rounded-lg bg-slate-100 p-1 text-xs font-semibold">
                  <button
                    type="button"
                    onClick={() => setActiveTab('upload')}
                    className={`px-3 py-1 rounded-md transition-all ${
                      activeTab === 'upload' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'
                    }`}
                  >
                    File Upload
                  </button>
                  <button
                    type="button"
                    onClick={() => setActiveTab('paste')}
                    className={`px-3 py-1 rounded-md transition-all ${
                      activeTab === 'paste' ? 'bg-white text-slate-900 shadow-xs' : 'text-slate-500'
                    }`}
                  >
                    Paste Text
                  </button>
                </div>
              </div>

              {activeTab === 'upload' ? (
                <div className="space-y-4">
                  <label className="border-2 border-dashed border-slate-200 hover:border-emerald-500 rounded-2xl p-8 flex flex-col items-center justify-center text-center cursor-pointer transition-colors bg-slate-50/50 hover:bg-emerald-50/30">
                    <input
                      type="file"
                      accept=".pdf,.docx,.txt"
                      onChange={handleFileUpload}
                      className="hidden"
                    />
                    <div className="w-12 h-12 rounded-xl bg-emerald-100 text-emerald-600 flex items-center justify-center mb-3">
                      <Upload className="w-6 h-6" />
                    </div>
                    <span className="font-bold text-slate-800 text-sm mb-1">
                      {uploadedFileName ? uploadedFileName : 'Click to upload your resume'}
                    </span>
                    <span className="text-xs text-slate-400">
                      Supports PDF (.pdf) and Word documents (.docx)
                    </span>
                  </label>

                  {uploading && <LoadingSpinner size="sm" text="Parsing document contents..." />}

                  {resumeText && (
                    <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 text-xs text-slate-600 space-y-2">
                      <div className="flex items-center justify-between text-slate-800 font-bold">
                        <span className="flex items-center gap-1.5 text-emerald-600">
                          <FileCheck className="w-4 h-4" /> Resume Parsed Successfully
                        </span>
                        <span className="text-slate-400 text-[11px]">
                          {resumeText.split(/\s+/).length} words
                        </span>
                      </div>
                      <p className="line-clamp-3 text-slate-500 italic">
                        "{resumeText.substring(0, 200)}..."
                      </p>
                    </div>
                  )}
                </div>
              ) : (
                <div>
                  <textarea
                    rows={12}
                    value={resumeText}
                    onChange={(e) => setResumeText(e.target.value)}
                    placeholder="Paste the full text of your resume here (Summary, Skills, Work Experience, Projects, Education)..."
                    className="w-full p-4 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden leading-relaxed font-mono"
                  />
                </div>
              )}
            </div>

            {/* Right Column: Target Job Description */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-5">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="font-bold text-slate-900 text-base">Target Opportunity</h2>
                  <p className="text-xs text-slate-500">Paste job requirements & role title</p>
                </div>
                <button
                  type="button"
                  onClick={sampleJob}
                  className="text-xs font-bold text-emerald-600 hover:text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-lg"
                >
                  Load Sample Job
                </button>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                  Target Role Title
                </label>
                <div className="relative">
                  <Target className="w-5 h-5 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    required
                    value={targetRole}
                    onChange={(e) => setTargetRole(e.target.value)}
                    placeholder="e.g. Senior Java Backend Engineer"
                    className="w-full pl-11 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase tracking-wider mb-2">
                  Job Description / Requirements
                </label>
                <textarea
                  rows={9}
                  required
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                  placeholder="Paste the entire job description from LinkedIn, Indeed, or the company careers page..."
                  className="w-full p-4 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden leading-relaxed font-sans"
                />
              </div>
            </div>
          </div>

          {/* Action CTA */}
          <div className="flex justify-end">
            <button
              type="submit"
              disabled={analyzing}
              className="px-8 py-4 rounded-2xl bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-sm shadow-lg shadow-emerald-600/30 flex items-center gap-3 transition-all hover:scale-105 disabled:opacity-50"
            >
              {analyzing ? (
                <>
                  <LoadingSpinner size="sm" />
                  <span>Computing Real Skill Match...</span>
                </>
              ) : (
                <>
                  <Sparkles className="w-5 h-5 text-emerald-200" />
                  <span>Calculate Readiness & Skill Gaps</span>
                  <ArrowRight className="w-5 h-5" />
                </>
              )}
            </button>
          </div>
        </form>
      ) : (
        /* Results View */
        <div className="space-y-8 animate-fadeIn">
          {/* Top Score Banner */}
          <div className="p-8 rounded-3xl bg-white border border-slate-200 shadow-sm grid grid-cols-1 md:grid-cols-3 gap-8 items-center">
            <div className="space-y-2">
              <span className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                Target Role
              </span>
              <h2 className="text-2xl font-black text-slate-900">{analysisResult.targetRole}</h2>
              <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full text-xs font-bold bg-emerald-100 text-emerald-800">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                {analysisResult.readinessLevel}
              </div>
            </div>

            {/* Score Radial / Circle */}
            <div className="flex flex-col items-center justify-center p-4 rounded-2xl bg-slate-50 border border-slate-100 text-center">
              <div className="text-5xl font-black text-emerald-600 mb-1">
                {analysisResult.overallMatchScore}%
              </div>
              <div className="text-xs font-bold uppercase tracking-wider text-slate-500">
                Overall Job Readiness Score
              </div>
            </div>

            {/* Score Breakdown */}
            <div className="space-y-3">
              <div>
                <div className="flex justify-between text-xs font-bold mb-1">
                  <span className="text-slate-600">Technical Skills Match</span>
                  <span className="text-slate-900">{analysisResult.technicalScore}%</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                  <div
                    className="bg-emerald-600 h-2.5 rounded-full transition-all duration-1000"
                    style={{ width: `${analysisResult.technicalScore}%` }}
                  />
                </div>
              </div>

              <div>
                <div className="flex justify-between text-xs font-bold mb-1">
                  <span className="text-slate-600">Soft Skills & Communication</span>
                  <span className="text-slate-900">{analysisResult.softSkillsScore}%</span>
                </div>
                <div className="w-full bg-slate-100 rounded-full h-2.5 overflow-hidden">
                  <div
                    className="bg-teal-500 h-2.5 rounded-full transition-all duration-1000"
                    style={{ width: `${analysisResult.softSkillsScore}%` }}
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Skill Breakdown Badges */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {/* Matching Skills */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-4">
              <div className="flex items-center gap-2 text-emerald-700">
                <CheckCircle2 className="w-5 h-5" />
                <h3 className="font-extrabold text-slate-900 text-lg">
                  Matched Skills ({analysisResult.presentSkills?.length || 0})
                </h3>
              </div>
              <p className="text-xs text-slate-500">
                Skills identified in both your resume and the job specification:
              </p>
              <div className="flex flex-wrap gap-2 pt-2">
                {analysisResult.presentSkills?.length ? (
                  analysisResult.presentSkills.map((skill: any, idx: number) => {
                    const skillName = typeof skill === 'string' ? skill : skill.skillName;
                    return (
                      <span
                        key={idx}
                        className="px-3 py-1.5 rounded-xl bg-emerald-50 text-emerald-800 text-xs font-bold border border-emerald-200 flex items-center gap-1.5"
                      >
                        <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
                        {skillName}
                      </span>
                    );
                  })
                ) : (
                  <span className="text-xs text-slate-400 italic">No direct keyword matches detected.</span>
                )}
              </div>
            </div>

            {/* Missing Skill Gaps */}
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-rose-200 bg-rose-50/20 shadow-xs space-y-4">
              <div className="flex items-center gap-2 text-rose-600">
                <AlertTriangle className="w-5 h-5" />
                <h3 className="font-extrabold text-slate-900 text-lg">
                  Skill Gaps to Close ({analysisResult.missingSkills?.length || 0})
                </h3>
              </div>
              <p className="text-xs text-slate-500">
                Critical requirements from the job description not found in your resume:
              </p>
              <div className="flex flex-wrap gap-2 pt-2">
                {analysisResult.missingSkills?.length ? (
                  analysisResult.missingSkills.map((skill: any, idx: number) => {
                    const skillName = typeof skill === 'string' ? skill : skill.skillName;
                    return (
                      <span
                        key={idx}
                        className="px-3 py-1.5 rounded-xl bg-rose-50 text-rose-800 text-xs font-bold border border-rose-200 flex items-center gap-1.5"
                      >
                        <span className="w-1.5 h-1.5 rounded-full bg-rose-500" />
                        {skillName}
                      </span>
                    );
                  })
                ) : (
                  <span className="text-xs text-emerald-600 font-bold">
                    Zero gaps! You meet all specified requirements.
                  </span>
                )}
              </div>
            </div>
          </div>

          {/* Strengths & Weaknesses */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-3">
              <h4 className="font-bold text-slate-900 text-sm flex items-center gap-2">
                <TrendingUp className="w-4 h-4 text-emerald-600" /> Key Strengths
              </h4>
              <ul className="space-y-2 text-xs text-slate-600">
                {analysisResult.strengths?.map((item, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="text-emerald-500 font-bold">•</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>

            <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-3">
              <h4 className="font-bold text-slate-900 text-sm flex items-center gap-2">
                <Target className="w-4 h-4 text-amber-500" /> Focus Recommendations
              </h4>
              <ul className="space-y-2 text-xs text-slate-600">
                {analysisResult.recommendations?.map((item, idx) => (
                  <li key={idx} className="flex items-start gap-2">
                    <span className="text-amber-500 font-bold">•</span>
                    <span>{item}</span>
                  </li>
                ))}
              </ul>
            </div>
          </div>

          {/* Roadmap Teaser / Action */}
          {analysisResult.roadmap && (
            <div className="p-8 rounded-3xl bg-gradient-to-r from-emerald-700 to-teal-800 text-white shadow-lg flex flex-col md:flex-row items-center justify-between gap-6">
              <div className="space-y-2">
                <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-white text-xs font-bold">
                  <Sparkles className="w-3.5 h-3.5 text-emerald-200" /> Personalized Roadmap Generated
                </div>
                <h3 className="text-2xl font-black">{analysisResult.roadmap.title}</h3>
                <p className="text-xs text-emerald-100 max-w-xl">
                  A structured {analysisResult.roadmap.estimatedWeeks}-week phased pathway specifically curated to bridge your {analysisResult.missingSkills?.length || 0} identified skill gaps.
                </p>
              </div>
              <button
                onClick={() => navigate('/roadmap')}
                className="px-6 py-3.5 rounded-xl bg-white text-emerald-900 font-extrabold text-sm shadow-md hover:bg-emerald-50 transition-all flex items-center gap-2 flex-shrink-0"
              >
                <BookOpen className="w-4 h-4" /> View Full Roadmap <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
