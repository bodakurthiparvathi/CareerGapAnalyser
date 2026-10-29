import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { analysisService } from '../services/analysisService';
import { User, SkillItem, ProjectItem, ExperienceItem, CertificationItem } from '../types';
import { Alert } from '../components/Alert';
import { LoadingSpinner } from '../components/LoadingSpinner';
import {
  User as UserIcon,
  Mail,
  Phone,
  MapPin,
  Briefcase,
  GraduationCap,
  Building2,
  Calendar,
  Save,
  CheckCircle2,
  Loader2,
  Sparkles,
  Award,
  Code,
  Plus,
  Trash2,
} from 'lucide-react';

export const ProfilePage: React.FC = () => {
  const { user: authUser, refreshUser } = useAuth();
  const [profile, setProfile] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Editable Form Fields
  const [fullName, setFullName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [location, setLocation] = useState('');
  const [degree, setDegree] = useState('');
  const [branch, setBranch] = useState('');
  const [college, setCollege] = useState('');
  const [graduationYear, setGraduationYear] = useState<number>(new Date().getFullYear());
  const [academicStatus, setAcademicStatus] = useState('Student');
  const [targetRole, setTargetRole] = useState('');
  const [preferredIndustry, setPreferredIndustry] = useState('');
  const [yearsOfExperience, setYearsOfExperience] = useState<number>(0);
  const [bio, setBio] = useState('');
  const [softSkillsInput, setSoftSkillsInput] = useState('');

  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchFullProfile = async () => {
      try {
        const data = await analysisService.getFullProfile();
        setProfile(data);
        setFullName(data.fullName || '');
        setPhoneNumber(data.phoneNumber || '');
        setLocation(data.location || '');
        setDegree(data.degree || '');
        setBranch(data.branch || '');
        setCollege(data.college || '');
        setGraduationYear(data.graduationYear || new Date().getFullYear());
        setAcademicStatus(data.academicStatus || 'Student');
        setTargetRole(data.targetRole || '');
        setPreferredIndustry(data.preferredIndustry || 'Software & Internet / SaaS');
        setYearsOfExperience(data.yearsOfExperience || 0);
        setBio(data.bio || '');
        setSoftSkillsInput(data.softSkills ? data.softSkills.join(', ') : '');
      } catch (err: any) {
        setError(err.message || 'Failed to fetch user profile');
      } finally {
        setLoading(false);
      }
    };
    fetchFullProfile();
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(null);

    try {
      const softSkillsArr = softSkillsInput
        .split(',')
        .map((s) => s.trim())
        .filter((s) => s.length > 0);

      const updated = await analysisService.updateProfile({
        fullName,
        phoneNumber,
        location,
        degree,
        branch,
        college,
        graduationYear: Number(graduationYear),
        academicStatus,
        targetRole,
        preferredIndustry,
        yearsOfExperience: Number(yearsOfExperience),
        bio,
        softSkills: softSkillsArr,
      });

      setProfile(updated);
      await refreshUser();
      setSuccess('Career profile updated successfully!');
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to update profile');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <LoadingSpinner text="Loading profile details..." />;
  }

  return (
    <div className="max-w-5xl space-y-8">
      {/* Header */}
      <div>
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold mb-2">
          <UserIcon className="w-3.5 h-3.5 text-emerald-600" /> Career Profile & Settings
        </div>
        <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900 tracking-tight">
          My Profile
        </h1>
        <p className="text-sm text-slate-500">
          Manage your personal information, academic qualifications, and career preferences anytime.
        </p>
      </div>

      {success && <Alert type="success" message={success} onClose={() => setSuccess(null)} />}
      {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Column: Profile Card & Quick Stats */}
        <div className="space-y-6">
          <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs text-center space-y-4">
            <div className="w-20 h-20 mx-auto rounded-3xl bg-gradient-to-tr from-emerald-600 to-teal-500 text-white font-black text-2xl flex items-center justify-center shadow-lg shadow-emerald-500/20">
              {profile?.fullName?.charAt(0).toUpperCase() || 'U'}
            </div>
            <div>
              <h2 className="text-lg font-extrabold text-slate-900">{profile?.fullName}</h2>
              <p className="text-xs text-slate-500">{profile?.email}</p>
            </div>

            <div className="p-3.5 rounded-2xl bg-slate-50 border border-slate-100 text-xs space-y-1.5 text-slate-600 text-left">
              <div className="flex justify-between">
                <span className="font-bold text-slate-900">Target Role:</span>
                <span className="text-emerald-700 font-semibold">{profile?.targetRole || 'Not set'}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-bold text-slate-900">Status:</span>
                <span>{profile?.academicStatus || 'Student'}</span>
              </div>
              <div className="flex justify-between">
                <span className="font-bold text-slate-900">Experience:</span>
                <span>{profile?.yearsOfExperience || 0} years</span>
              </div>
              <div className="flex justify-between">
                <span className="font-bold text-slate-900">Onboarding:</span>
                <span className={profile?.onboardingCompleted ? 'text-emerald-600 font-bold' : 'text-amber-600 font-bold'}>
                  {profile?.onboardingCompleted ? 'Completed' : 'Pending'}
                </span>
              </div>
            </div>
          </div>

          {/* Captured Skills Badge Preview */}
          {profile?.technicalSkills && profile.technicalSkills.length > 0 && (
            <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-xs space-y-3">
              <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider flex items-center gap-1.5">
                <Code className="w-4 h-4 text-emerald-600" /> Recorded Technical Skills
              </h3>
              <div className="flex flex-wrap gap-1.5">
                {profile.technicalSkills.map((s, idx) => (
                  <span
                    key={idx}
                    className="px-2.5 py-1 rounded-lg bg-emerald-50 text-emerald-800 text-[11px] font-bold border border-emerald-100"
                  >
                    {s.skillName} ({s.proficiencyLevel})
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Projects Snapshot */}
          {profile?.projects && profile.projects.length > 0 && (
            <div className="bg-white p-6 rounded-3xl border border-slate-200 shadow-xs space-y-3">
              <h3 className="font-bold text-slate-900 text-xs uppercase tracking-wider flex items-center gap-1.5">
                <Briefcase className="w-4 h-4 text-emerald-600" /> Key Projects ({profile.projects.length})
              </h3>
              <div className="space-y-2">
                {profile.projects.map((p, idx) => (
                  <div key={idx} className="p-2.5 rounded-xl bg-slate-50 border border-slate-100 text-xs space-y-0.5">
                    <div className="font-bold text-slate-900">{p.title}</div>
                    <div className="text-[11px] text-slate-500 truncate">{p.technologies}</div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* Right Column: Full Edit Form */}
        <form onSubmit={handleSubmit} className="lg:col-span-2 bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-xs space-y-6">
          <h3 className="font-extrabold text-slate-900 text-base">Edit Career & Educational Profile</h3>

          {/* Section 1: Personal */}
          <div className="space-y-4">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Personal Information</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Full Name</label>
                <div className="relative">
                  <UserIcon className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Email</label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="email"
                    disabled
                    value={profile?.email || ''}
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 bg-slate-50 text-slate-400 text-xs cursor-not-allowed"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Phone Number</label>
                <div className="relative">
                  <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    placeholder="+1 (555) 000-0000"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Location</label>
                <div className="relative">
                  <MapPin className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="City, State / Country"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                  />
                </div>
              </div>
            </div>
          </div>

          {/* Section 2: Academic */}
          <div className="space-y-4 pt-4 border-t border-slate-100">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Academic Background</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Degree</label>
                <input
                  type="text"
                  value={degree}
                  onChange={(e) => setDegree(e.target.value)}
                  placeholder="e.g. B.Tech, M.S."
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Branch / Major</label>
                <input
                  type="text"
                  value={branch}
                  onChange={(e) => setBranch(e.target.value)}
                  placeholder="e.g. Computer Science"
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">College / University</label>
                <input
                  type="text"
                  value={college}
                  onChange={(e) => setCollege(e.target.value)}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Graduation Year</label>
                <input
                  type="number"
                  value={graduationYear}
                  onChange={(e) => setGraduationYear(Number(e.target.value))}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Academic Status</label>
                <select
                  value={academicStatus}
                  onChange={(e) => setAcademicStatus(e.target.value)}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium bg-white"
                >
                  <option value="Final Year Student">Final Year Student</option>
                  <option value="Pre-Final Year">Pre-Final Year</option>
                  <option value="Recent Graduate">Recent Graduate</option>
                  <option value="Working Professional">Working Professional</option>
                  <option value="Career Switcher">Career Switcher</option>
                </select>
              </div>
            </div>
          </div>

          {/* Section 3: Target Role & Industry */}
          <div className="space-y-4 pt-4 border-t border-slate-100">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-wider">Target Role & Preferences</h4>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Target Job Role</label>
                <input
                  type="text"
                  value={targetRole}
                  onChange={(e) => setTargetRole(e.target.value)}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Preferred Industry</label>
                <input
                  type="text"
                  value={preferredIndustry}
                  onChange={(e) => setPreferredIndustry(e.target.value)}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Years of Experience</label>
                <input
                  type="number"
                  min={0}
                  max={50}
                  value={yearsOfExperience}
                  onChange={(e) => setYearsOfExperience(Number(e.target.value))}
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Soft Skills (Comma-separated)</label>
                <input
                  type="text"
                  value={softSkillsInput}
                  onChange={(e) => setSoftSkillsInput(e.target.value)}
                  placeholder="Teamwork, Communication, Problem Solving"
                  className="w-full px-3 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden font-medium"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1">Professional Bio</label>
                <textarea
                  rows={3}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  className="w-full p-3 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden leading-relaxed"
                />
              </div>
            </div>
          </div>

          <div className="pt-2 flex justify-end">
            <button
              type="submit"
              disabled={saving}
              className="px-6 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2 transition-all disabled:opacity-50"
            >
              {saving ? (
                <>
                  <Loader2 className="w-4 h-4 animate-spin" /> Saving Changes...
                </>
              ) : (
                <>
                  <Save className="w-4 h-4" /> Save Profile Information
                </>
              )}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};
