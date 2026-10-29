import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  BrainCircuit,
  BookOpen,
  FileText,
  User,
  Settings,
  Sparkles,
} from 'lucide-react';

const navItems = [
  { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
  { name: 'Skill Analyzer', path: '/analyzer', icon: BrainCircuit },
  { name: 'Learning Roadmap', path: '/roadmap', icon: BookOpen },
  { name: 'Resume Builder', path: '/resume-builder', icon: FileText },
  { name: 'My Profile', path: '/profile', icon: User },
  { name: 'Settings', path: '/settings', icon: Settings },
];

export const Sidebar: React.FC = () => {
  return (
    <aside className="w-64 bg-white border-r border-slate-200 hidden lg:flex flex-col flex-shrink-0 min-h-[calc(100vh-4rem)]">
      <div className="p-4 space-y-1">
        <div className="px-3 py-2 text-xs font-semibold uppercase tracking-wider text-slate-400">
          Core Platform
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-emerald-50 text-emerald-700 font-semibold shadow-xs'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`
              }
            >
              <Icon className="w-4 h-4 flex-shrink-0" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </div>

      <div className="mt-auto p-4 m-3 rounded-2xl bg-gradient-to-br from-emerald-600 to-teal-700 text-white shadow-md shadow-emerald-900/10">
        <div className="flex items-center gap-2 mb-2 text-emerald-100 text-xs font-semibold">
          <Sparkles className="w-4 h-4 text-emerald-200" />
          <span>AI Assistant Ready</span>
        </div>
        <p className="text-xs text-emerald-50 leading-relaxed mb-3">
          Upload any job description to instantly map your skill match & roadmap.
        </p>
        <NavLink
          to="/analyzer"
          className="inline-block w-full text-center py-2 px-3 bg-white text-emerald-800 text-xs font-bold rounded-lg shadow-xs hover:bg-emerald-50 transition-colors"
        >
          New Analysis
        </NavLink>
      </div>
    </aside>
  );
};
