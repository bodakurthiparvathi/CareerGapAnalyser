import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Alert } from '../components/Alert';
import {
  Settings,
  Bell,
  Shield,
  Server,
  Database,
  Cpu,
  Trash2,
  Check,
  RefreshCw,
} from 'lucide-react';

export const SettingsPage: React.FC = () => {
  const { user, logout } = useAuth();

  const [emailAlerts, setEmailAlerts] = useState(true);
  const [weeklyDigest, setWeeklyDigest] = useState(true);
  const [savedStatus, setSavedStatus] = useState<string | null>(null);

  const handleSavePreferences = () => {
    setSavedStatus('Preferences saved successfully.');
    setTimeout(() => setSavedStatus(null), 3000);
  };

  return (
    <div className="max-w-4xl space-y-8">
      {/* Header */}
      <div>
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-100 text-slate-800 text-xs font-bold mb-2">
          <Settings className="w-3.5 h-3.5 text-slate-600" /> Platform Configuration
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          Settings
        </h1>
        <p className="text-sm text-slate-500">
          Configure notification preferences, security, and view platform system information.
        </p>
      </div>

      {savedStatus && <Alert type="success" message={savedStatus} />}

      {/* Notifications */}
      <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-5">
        <h3 className="font-extrabold text-slate-900 text-base flex items-center gap-2">
          <Bell className="w-4 h-4 text-emerald-600" /> Notification Preferences
        </h3>

        <div className="space-y-4">
          <div className="flex items-center justify-between p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div>
              <div className="font-bold text-slate-900 text-sm">Roadmap Milestones Reminders</div>
              <div className="text-xs text-slate-500">Receive alerts when milestones are due</div>
            </div>
            <button
              onClick={() => setEmailAlerts(!emailAlerts)}
              className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${
                emailAlerts ? 'bg-emerald-600' : 'bg-slate-300'
              }`}
            >
              <div
                className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${
                  emailAlerts ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>

          <div className="flex items-center justify-between p-4 rounded-2xl bg-slate-50 border border-slate-100">
            <div>
              <div className="font-bold text-slate-900 text-sm">Weekly Job Readiness Digest</div>
              <div className="text-xs text-slate-500">Weekly report of your closing skill gaps</div>
            </div>
            <button
              onClick={() => setWeeklyDigest(!weeklyDigest)}
              className={`w-11 h-6 flex items-center rounded-full p-1 transition-colors ${
                weeklyDigest ? 'bg-emerald-600' : 'bg-slate-300'
              }`}
            >
              <div
                className={`bg-white w-4 h-4 rounded-full shadow-md transform transition-transform ${
                  weeklyDigest ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
        </div>

        <div className="flex justify-end">
          <button
            onClick={handleSavePreferences}
            className="px-5 py-2.5 rounded-xl bg-slate-900 hover:bg-slate-800 text-white font-bold text-xs flex items-center gap-1.5 transition-colors"
          >
            <Check className="w-3.5 h-3.5" /> Save Preferences
          </button>
        </div>
      </div>

      {/* Backend & Environment Diagnostics */}
      <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-5">
        <h3 className="font-extrabold text-slate-900 text-base flex items-center gap-2">
          <Server className="w-4 h-4 text-emerald-600" /> Platform Architecture & Services
        </h3>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="flex items-center gap-2 text-slate-700 font-bold text-xs">
              <Server className="w-4 h-4 text-emerald-600" /> Backend
            </div>
            <div className="text-sm font-extrabold text-slate-900">Spring Boot 3.3.4</div>
            <div className="text-[11px] text-slate-500">Java 17/21 • Spring Security JWT</div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="flex items-center gap-2 text-slate-700 font-bold text-xs">
              <Database className="w-4 h-4 text-blue-600" /> Database
            </div>
            <div className="text-sm font-extrabold text-slate-900">PostgreSQL / H2</div>
            <div className="text-[11px] text-slate-500">Spring Data JPA & Hibernate</div>
          </div>

          <div className="p-4 rounded-2xl bg-slate-50 border border-slate-100 space-y-1.5">
            <div className="flex items-center gap-2 text-slate-700 font-bold text-xs">
              <Cpu className="w-4 h-4 text-purple-600" /> AI Engine
            </div>
            <div className="text-sm font-extrabold text-slate-900">NLP Semantic Parser</div>
            <div className="text-[11px] text-slate-500">Real algorithmic skill extraction</div>
          </div>
        </div>
      </div>

      {/* Account Session & Danger Zone */}
      <div className="bg-white p-6 sm:p-8 rounded-3xl border border-rose-200 shadow-xs space-y-4">
        <h3 className="font-extrabold text-rose-900 text-base flex items-center gap-2">
          <Shield className="w-4 h-4 text-rose-600" /> Session & Account Security
        </h3>
        <p className="text-xs text-slate-500">
          Sign out of all sessions or clear client-side token cache.
        </p>

        <div className="pt-2 flex flex-wrap items-center gap-4">
          <button
            onClick={logout}
            className="px-5 py-2.5 rounded-xl bg-rose-50 hover:bg-rose-100 text-rose-700 font-bold text-xs flex items-center gap-2 transition-colors border border-rose-200"
          >
            <Trash2 className="w-3.5 h-3.5" /> Sign Out of Account
          </button>
        </div>
      </div>
    </div>
  );
};
