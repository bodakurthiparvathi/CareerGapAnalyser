import React from 'react';
import { NextBestAction } from '../types';
import { Sparkles, ArrowRight, Zap, Target, BookOpen, Award, CheckCircle2 } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

interface NextBestActionBannerProps {
  action?: NextBestAction | null;
  onActionClick?: (action: NextBestAction) => void;
}

export const NextBestActionBanner: React.FC<NextBestActionBannerProps> = ({ action, onActionClick }) => {
  const navigate = useNavigate();

  if (!action) return null;

  const handleClick = () => {
    if (onActionClick) {
      onActionClick(action);
    } else if (action.targetUrl) {
      navigate(action.targetUrl);
    }
  };

  const getPriorityColor = () => {
    switch (action.priority) {
      case 'HIGH':
        return 'bg-rose-50 text-rose-700 border-rose-200';
      case 'MEDIUM':
        return 'bg-amber-50 text-amber-700 border-amber-200';
      default:
        return 'bg-emerald-50 text-emerald-700 border-emerald-200';
    }
  };

  const getIcon = () => {
    switch (action.actionType) {
      case 'ANALYZE_GAP':
        return <Target className="w-5 h-5 text-emerald-600" />;
      case 'START_MODULE':
      case 'COMPLETE_PRACTICE':
        return <BookOpen className="w-5 h-5 text-emerald-600" />;
      case 'VERIFY_SKILL':
        return <Award className="w-5 h-5 text-amber-600" />;
      case 'TAKE_MOCK_TEST':
      case 'REVISE_TOPIC':
        return <Zap className="w-5 h-5 text-indigo-600" />;
      default:
        return <Sparkles className="w-5 h-5 text-emerald-600" />;
    }
  };

  return (
    <div className="p-6 rounded-3xl bg-gradient-to-r from-emerald-900 via-teal-900 to-slate-900 text-white shadow-lg relative overflow-hidden">
      <div className="absolute right-0 top-0 translate-x-8 -translate-y-8 w-48 h-48 bg-emerald-500/10 rounded-full blur-2xl pointer-events-none" />

      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6 relative z-10">
        <div className="space-y-2 max-w-2xl">
          <div className="flex items-center gap-2">
            <span className="inline-flex items-center gap-1.5 px-3 py-0.5 rounded-full bg-white/20 text-emerald-200 text-xs font-bold backdrop-blur-xs">
              <Sparkles className="w-3.5 h-3.5 text-emerald-300" /> Next-Best-Action
            </span>
            <span className={`text-[11px] font-bold px-2.5 py-0.5 rounded-full border ${getPriorityColor()} bg-white/90`}>
              {action.badge || action.priority}
            </span>
          </div>
          <h3 className="text-xl sm:text-2xl font-black text-white tracking-tight flex items-center gap-2">
            {getIcon()}
            {action.title}
          </h3>
          <p className="text-xs sm:text-sm text-slate-300 leading-relaxed font-normal">
            {action.description}
          </p>
        </div>

        <button
          onClick={handleClick}
          className="px-6 py-3.5 rounded-2xl bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-black text-xs sm:text-sm shadow-lg shadow-emerald-500/25 flex items-center gap-2 transition-all hover:scale-105 cursor-pointer flex-shrink-0"
        >
          <span>Take Action Now</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>
    </div>
  );
};

