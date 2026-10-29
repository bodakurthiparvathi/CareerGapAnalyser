import React from 'react';
import { Compass, Github, Heart } from 'lucide-react';
import { Link } from 'react-router-dom';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-white border-t border-slate-200 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          <div className="md:col-span-2 space-y-3">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-emerald-600 flex items-center justify-center text-white">
                <Compass className="w-4 h-4" />
              </div>
              <span className="font-extrabold text-lg text-slate-900">CareerGap AI</span>
            </div>
            <p className="text-sm text-slate-500 max-w-sm leading-relaxed">
              Know what you're missing. Learn what matters. Become job-ready.
              Empowering students and job-seekers with precise AI skill gap analysis and actionable learning roadmaps.
            </p>
          </div>

          <div>
            <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider mb-3">Platform</h4>
            <ul className="space-y-2 text-sm text-slate-600">
              <li><Link to="/analyzer" className="hover:text-emerald-600">Skill Gap Analyzer</Link></li>
              <li><Link to="/roadmap" className="hover:text-emerald-600">Learning Roadmaps</Link></li>
              <li><Link to="/resume-builder" className="hover:text-emerald-600">Resume Builder</Link></li>
              <li><Link to="/dashboard" className="hover:text-emerald-600">Dashboard</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider mb-3">Technology</h4>
            <p className="text-xs text-slate-500 leading-relaxed mb-3">
              Powered by Spring Boot, PostgreSQL, Spring Security, React 18, and AI semantic intelligence.
            </p>
            <div className="flex items-center gap-3 text-slate-400">
              <span className="text-xs font-medium text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-full border border-emerald-100">
                v1.0.0 Production Ready
              </span>
            </div>
          </div>
        </div>

        <div className="border-t border-slate-100 pt-6 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs text-slate-400">
          <p>© {new Date().getFullYear()} CareerGap AI. All rights reserved.</p>
          <p className="flex items-center gap-1">
            Built with <Heart className="w-3.5 h-3.5 text-rose-500 fill-rose-500" /> for ambitious career seekers
          </p>
        </div>
      </div>
    </footer>
  );
};
