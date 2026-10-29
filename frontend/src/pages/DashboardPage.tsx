import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { analysisService } from '../services/analysisService';
import { learningService } from '../services/learningService';
import { DashboardStats, SkillAnalysis, LearningDashboardData } from '../types';
import { LoadingSpinner } from '../components/LoadingSpinner';
import { Alert } from '../components/Alert';
import { NextBestActionBanner } from '../components/NextBestActionBanner';
import {
  Compass,
  ArrowRight,
  BrainCircuit,
  TrendingUp,
  Target,
  Sparkles,
  BookOpen,
  FileText,
  Calendar,
  Layers,
  Flame,
  BadgeCheck,
  Zap,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [stats, setStats] = useState<DashboardStats | null>(null);
  const [recentAnalyses, setRecentAnalyses] = useState<SkillAnalysis[]>([]);
  const [learningData, setLearningData] = useState<LearningDashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [statsData, historyData, learnData] = await Promise.all([
          analysisService.getDashboardStats(),
          analysisService.getHistory(),
          learningService.getDashboard().catch(() => null),
        ]);
        setStats(statsData);
        setRecentAnalyses(historyData);
        setLearningData(learnData);
      } catch (err: any) {
        setError(err.message || 'Failed to load dashboard metrics');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (loading) {
    return <LoadingSpinner text="Loading career analytics..." />;
  }

  return (
    <div className="space-y-8">
      {/* Welcome Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-slate-800 to-emerald-950 p-6 sm:p-8 text-white shadow-lg">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2 max-w-xl">
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 text-xs font-semibold border border-emerald-500/30">
              <Sparkles className="w-3.5 h-3.5" /> Career Development Center
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
              Welcome back, {user?.fullName || 'User'}!
            </h1>
            <p className="text-slate-300 text-sm leading-relaxed">
              Target Role:{' '}
              <span className="font-semibold text-emerald-300">
                {user?.targetRole || 'Software Engineer'}
              </span>
              . Compare your resume against latest job requisitions to keep your skills razor sharp.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Link
              to="/analyzer"
              className="px-5 py-2.5 rounded-xl bg-emerald-500 hover:bg-emerald-600 text-white font-bold text-sm shadow-md shadow-emerald-500/25 flex items-center gap-2 transition-all hover:scale-105"
            >
              <BrainCircuit className="w-4 h-4" /> Run New Analysis
            </Link>
          </div>
        </div>
      </div>

      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      {/* Recommended Next-Best-Action Banner */}
      {learningData?.nextBestAction && (
        <NextBestActionBanner action={learningData.nextBestAction} />
      )}

      {/* Metric Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-bold uppercase tracking-wider">Average Readiness</span>
            <div className="p-2 rounded-lg bg-emerald-50 text-emerald-600">
              <TrendingUp className="w-4 h-4" />
            </div>
          </div>
          <div className="text-3xl font-extrabold text-slate-900">
            {stats?.averageReadiness || 0}%
          </div>
          <p className="text-xs text-slate-500">
            Status:{' '}
            <span className="font-semibold text-emerald-700">
              {stats?.currentReadinessLevel || 'Initial'}
            </span>
          </p>
        </div>

        <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-bold uppercase tracking-wider">Total Analyses</span>
            <div className="p-2 rounded-lg bg-teal-50 text-teal-600">
              <Layers className="w-4 h-4" />
            </div>
          </div>
          <div className="text-3xl font-extrabold text-slate-900">
            {stats?.totalAnalyses || 0}
          </div>
          <p className="text-xs text-slate-500">Job descriptions benchmarked</p>
        </div>

        <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-bold uppercase tracking-wider">Skills Validated</span>
            <div className="p-2 rounded-lg bg-indigo-50 text-indigo-600">
              <Target className="w-4 h-4" />
            </div>
          </div>
          <div className="text-3xl font-extrabold text-slate-900">
            {stats?.skillsIdentified || 0}
          </div>
          <p className="text-xs text-slate-500">Matching strengths found</p>
        </div>

        <div className="p-6 rounded-2xl bg-white border border-slate-200 shadow-xs space-y-2">
          <div className="flex items-center justify-between text-slate-500">
            <span className="text-xs font-bold uppercase tracking-wider">Skills Gap</span>
            <div className="p-2 rounded-lg bg-amber-50 text-amber-600">
              <Compass className="w-4 h-4" />
            </div>
          </div>
          <div className="text-3xl font-extrabold text-amber-600">
            {stats?.skillsGapCount || 0}
          </div>
          <p className="text-xs text-slate-500">Areas flagged for learning</p>
        </div>
      </div>

      {/* Quick Launch Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <Link
          to="/analyzer"
          className="group p-6 rounded-2xl bg-white border border-slate-200 hover:border-emerald-500 shadow-xs hover:shadow-md transition-all space-y-3"
        >
          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center group-hover:scale-110 transition-transform">
            <BrainCircuit className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 group-hover:text-emerald-700 transition-colors">
            Skill Gap Analyzer
          </h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Upload your resume and compare against any job posting to calculate match percentage and missing keywords.
          </p>
          <div className="text-xs font-bold text-emerald-600 flex items-center gap-1">
            Analyze now <ArrowRight className="w-3.5 h-3.5" />
          </div>
        </Link>

        <Link
          to="/roadmap"
          className="group p-6 rounded-2xl bg-white border border-slate-200 hover:border-teal-500 shadow-xs hover:shadow-md transition-all space-y-3"
        >
          <div className="w-10 h-10 rounded-xl bg-teal-50 text-teal-600 flex items-center justify-center group-hover:scale-110 transition-transform">
            <BookOpen className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 group-hover:text-teal-700 transition-colors">
            Learning Roadmaps
          </h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Follow customized week-by-week phases with targeted projects, documentation, and milestones.
          </p>
          <div className="text-xs font-bold text-teal-600 flex items-center gap-1">
            Explore roadmaps <ArrowRight className="w-3.5 h-3.5" />
          </div>
        </Link>

        <Link
          to="/resume-builder"
          className="group p-6 rounded-2xl bg-white border border-slate-200 hover:border-indigo-500 shadow-xs hover:shadow-md transition-all space-y-3"
        >
          <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center group-hover:scale-110 transition-transform">
            <FileText className="w-5 h-5" />
          </div>
          <h3 className="font-bold text-slate-900 group-hover:text-indigo-700 transition-colors">
            Resume Builder
          </h3>
          <p className="text-xs text-slate-500 leading-relaxed">
            Tailor your resume bullets to include identified matching keywords and avoid ATS automated rejections.
          </p>
          <div className="text-xs font-bold text-indigo-600 flex items-center gap-1">
            Build resume <ArrowRight className="w-3.5 h-3.5" />
          </div>
        </Link>
      </div>

      {/* Recent Analyses History Section */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-xs overflow-hidden">
        <div className="p-6 border-b border-slate-100 flex items-center justify-between">
          <div>
            <h2 className="font-extrabold text-slate-900 text-lg">Recent Skill Gap Analyses</h2>
            <p className="text-xs text-slate-500">Track how your readiness changes over time</p>
          </div>
          {recentAnalyses.length > 0 && (
            <Link to="/analyzer" className="text-xs font-bold text-emerald-600 hover:text-emerald-700">
              Run New +
            </Link>
          )}
        </div>

        {recentAnalyses.length === 0 ? (
          <div className="p-12 text-center space-y-4">
            <div className="w-16 h-16 mx-auto rounded-3xl bg-slate-100 text-slate-400 flex items-center justify-center">
              <BrainCircuit className="w-8 h-8" />
            </div>
            <div className="space-y-1">
              <h3 className="text-base font-bold text-slate-800">No analyses performed yet</h3>
              <p className="text-xs text-slate-500 max-w-sm mx-auto">
                Upload your resume and paste a job specification to generate your first job readiness score.
              </p>
            </div>
            <Link
              to="/analyzer"
              className="inline-flex items-center gap-2 px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/25 transition-all"
            >
              Analyze Your First Job <ArrowRight className="w-3.5 h-3.5" />
            </Link>
          </div>
        ) : (
          <div className="divide-y divide-slate-100 overflow-x-auto">
            {recentAnalyses.map((item) => (
              <div key={item.id} className="p-6 flex flex-col sm:flex-row sm:items-center justify-between gap-4 hover:bg-slate-50/70 transition-colors">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <span className="font-bold text-slate-900 text-sm">{item.targetRole}</span>
                    <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full ${
                      item.overallMatchScore >= 75
                        ? 'bg-emerald-100 text-emerald-800'
                        : item.overallMatchScore >= 50
                        ? 'bg-blue-100 text-blue-800'
                        : 'bg-amber-100 text-amber-800'
                    }`}>
                      {item.readinessLevel}
                    </span>
                  </div>
                  <div className="flex items-center gap-3 text-xs text-slate-400">
                    <span className="flex items-center gap-1">
                      <Calendar className="w-3.5 h-3.5" />
                      {new Date(item.createdAt).toLocaleDateString()}
                    </span>
                    <span>•</span>
                    <span>{item.presentSkills?.length || 0} skills matched</span>
                    <span>•</span>
                    <span className="text-rose-600 font-medium">{item.missingSkills?.length || 0} gaps detected</span>
                  </div>
                </div>

                <div className="flex items-center gap-6">
                  <div className="text-right">
                    <div className="text-2xl font-extrabold text-slate-900">{item.overallMatchScore}%</div>
                    <div className="text-[10px] text-slate-400 font-medium uppercase tracking-wider">Overall Match</div>
                  </div>
                  <Link
                    to={`/analyzer?id=${item.id}`}
                    className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-emerald-50 text-slate-700 hover:text-emerald-700 text-xs font-bold transition-colors"
                  >
                    View Report
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
