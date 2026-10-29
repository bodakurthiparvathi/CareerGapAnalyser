import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import resumeService from '../services/resumeService';
import {
  ResumeData,
  ResumeSkillItem,
  ResumeExperience,
  ResumeProject,
  ResumeEducation,
  ResumeCertification,
  PendingSkillUpdate,
  ResumeTailorResponse,
  ResumeVersion,
  BulletImprovement,
} from '../types';
import {
  FileText,
  Download,
  Sparkles,
  CheckCircle2,
  AlertTriangle,
  Plus,
  Trash2,
  RefreshCw,
  ExternalLink,
  X,
  Check,
  Award,
  BookOpen,
  ArrowRight,
  Target,
  FileCheck,
} from 'lucide-react';

const emptyResumeData: ResumeData = {
  fullName: '',
  email: '',
  phoneNumber: '',
  location: '',
  linkedinUrl: '',
  githubUrl: '',
  portfolioUrl: '',
  summary: '',
  skills: [],
  experiences: [],
  projects: [],
  education: [],
  certifications: [],
};

export const ResumeBuilderPage: React.FC = () => {
  const { user } = useAuth();

  const [resumeData, setResumeData] = useState<ResumeData>(emptyResumeData);
  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);
  const [exporting, setExporting] = useState<boolean>(false);
  const [notification, setNotification] = useState<{ type: 'success' | 'error' | 'info'; text: string } | null>(null);

  // Pending Skills State
  const [pendingSkills, setPendingSkills] = useState<PendingSkillUpdate[]>([]);
  const [showPendingModal, setShowPendingModal] = useState<boolean>(false);

  // Job Tailoring State
  const [showTailorModal, setShowTailorModal] = useState<boolean>(false);
  const [targetJobTitle, setTargetJobTitle] = useState<string>(user?.targetRole || 'Full Stack Software Engineer');
  const [targetJobDescription, setTargetJobDescription] = useState<string>('');
  const [tailoring, setTailoring] = useState<boolean>(false);
  const [tailorResult, setTailorResult] = useState<ResumeTailorResponse | null>(null);

  // Versions State
  const [showVersionModal, setShowVersionModal] = useState<boolean>(false);
  const [versions, setVersions] = useState<ResumeVersion[]>([]);
  const [versionTitle, setVersionTitle] = useState<string>('');
  const [changeSummary, setChangeSummary] = useState<string>('');

  // Active Editor Section
  const [activeSection, setActiveSection] = useState<'personal' | 'skills' | 'experience' | 'projects' | 'education' | 'certifications'>('personal');

  // Load profile resume on initial render
  useEffect(() => {
    loadProfileAndPending();
    loadVersions();
  }, []);

  const showMsg = (text: string, type: 'success' | 'error' | 'info' = 'success') => {
    setNotification({ text, type });
    setTimeout(() => setNotification(null), 5000);
  };

  const loadProfileAndPending = async () => {
    try {
      setLoading(true);
      const data = await resumeService.extractProfile();
      setResumeData(data);
      const pending = await resumeService.getPendingSkills(data);
      setPendingSkills(pending);
    } catch (err: any) {
      console.error('Error loading resume profile:', err);
      showMsg('Failed to fetch profile information from database', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadVersions = async () => {
    try {
      const vList = await resumeService.getVersions();
      setVersions(vList);
    } catch (err) {
      console.error('Error fetching resume versions:', err);
    }
  };

  // Skill updates approval workflow
  const approvePendingSkill = (item: PendingSkillUpdate) => {
    const existingIndex = resumeData.skills.findIndex(
      (s) => s.skillName.toLowerCase() === item.skillName.toLowerCase()
    );

    const newSkill: ResumeSkillItem = {
      skillName: item.skillName,
      category: item.category || 'Technical',
      proficiencyLevel: item.proficiencyLevel || 'Intermediate',
      skillType: item.skillType,
      verified: item.skillType === 'VERIFIED',
      verificationBadge: item.verificationBadge,
    };

    let updatedSkills = [...resumeData.skills];
    if (existingIndex >= 0) {
      updatedSkills[existingIndex] = newSkill;
    } else {
      updatedSkills.push(newSkill);
    }

    setResumeData({ ...resumeData, skills: updatedSkills });
    setPendingSkills(pendingSkills.filter((p) => p.skillName.toLowerCase() !== item.skillName.toLowerCase()));
    showMsg(`Approved and added "${item.skillName}" to resume as ${item.skillType}.`);
  };

  const declinePendingSkill = (item: PendingSkillUpdate) => {
    setPendingSkills(pendingSkills.filter((p) => p.skillName.toLowerCase() !== item.skillName.toLowerCase()));
    showMsg(`Dismissed update for "${item.skillName}".`, 'info');
  };

  // Tailoring
  const handleTailorResume = async () => {
    if (!targetJobDescription.trim()) {
      showMsg('Please paste a target job description', 'error');
      return;
    }

    try {
      setTailoring(true);
      const result = await resumeService.tailorResume({
        targetJobTitle,
        targetJobDescription,
        resumeData,
      });
      setTailorResult(result);
      showMsg(`Resume analyzed! Match Score: ${result.matchScore}%`);
    } catch (err: any) {
      console.error('Error tailoring resume:', err);
      showMsg('Failed to tailor resume against job description', 'error');
    } finally {
      setTailoring(false);
    }
  };

  const handleApplyTailoredSummary = () => {
    if (tailorResult?.tailoredResume?.summary) {
      setResumeData({
        ...resumeData,
        summary: tailorResult.tailoredResume.summary,
      });
      showMsg('Applied tailored summary to resume editor.');
    }
  };

  const handleAcceptBullet = (improvement: BulletImprovement) => {
    if (improvement.section === 'PROJECTS') {
      const updatedProjects = resumeData.projects.map((proj) => {
        if (proj.title.toLowerCase() === improvement.itemTitle.toLowerCase()) {
          const updatedBullets = proj.bullets.map((b) =>
            b === improvement.originalBullet ? improvement.improvedBullet : b
          );
          return { ...proj, bullets: updatedBullets };
        }
        return proj;
      });
      setResumeData({ ...resumeData, projects: updatedProjects });
    } else if (improvement.section === 'EXPERIENCE') {
      const updatedExperiences = resumeData.experiences.map((exp) => {
        const fullTitle = `${exp.role} at ${exp.company}`.toLowerCase();
        if (fullTitle === improvement.itemTitle.toLowerCase() || exp.role.toLowerCase() === improvement.itemTitle.toLowerCase()) {
          const updatedBullets = exp.bullets.map((b) =>
            b === improvement.originalBullet ? improvement.improvedBullet : b
          );
          return { ...exp, bullets: updatedBullets };
        }
        return exp;
      });
      setResumeData({ ...resumeData, experiences: updatedExperiences });
    }

    // Mark as accepted
    if (tailorResult) {
      setTailorResult({
        ...tailorResult,
        bulletImprovements: tailorResult.bulletImprovements.map((bi) =>
          bi === improvement ? { ...bi, accepted: true } : bi
        ),
      });
    }
    showMsg('Bullet improvement accepted and applied to resume!');
  };

  // Saving Version
  const handleSaveVersion = async () => {
    if (!versionTitle.trim()) {
      showMsg('Please specify a title for this version', 'error');
      return;
    }

    try {
      setSaving(true);
      const saved = await resumeService.saveVersion({
        title: versionTitle.trim(),
        targetJobTitle: targetJobTitle || 'Default',
        targetJobDescription: targetJobDescription || '',
        changeSummary: changeSummary.trim() || 'Saved candidate version',
        resumeData,
      });
      setVersions([saved, ...versions]);
      setShowVersionModal(false);
      setVersionTitle('');
      setChangeSummary('');
      showMsg(`Successfully saved "${saved.title}" (Version ${saved.versionNumber})!`);
    } catch (err: any) {
      console.error('Error saving resume version:', err);
      showMsg('Failed to save resume version', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleLoadVersion = (ver: ResumeVersion) => {
    setResumeData(ver.resumeData);
    setShowVersionModal(false);
    showMsg(`Loaded Version ${ver.versionNumber}: "${ver.title}"`);
  };

  // Export PDF
  const handleExportPdf = async () => {
    try {
      setExporting(true);
      const filename = `${resumeData.fullName ? resumeData.fullName.replace(/\s+/g, '_') : 'Resume'}_ATS.pdf`;
      await resumeService.exportPdf(resumeData, filename);
      showMsg('Downloaded ATS-friendly PDF successfully!');
    } catch (err: any) {
      console.error('Error downloading resume PDF:', err);
      showMsg('Failed to generate PDF. Please try again.', 'error');
    } finally {
      setExporting(false);
    }
  };

  const handleDownloadVersionPdf = async (ver: ResumeVersion) => {
    try {
      const filename = `resume-v${ver.versionNumber}.pdf`;
      await resumeService.downloadVersionPdf(ver.id, filename);
      showMsg(`Downloaded Version ${ver.versionNumber} PDF.`);
    } catch (err: any) {
      console.error('Error downloading version PDF:', err);
      showMsg('Failed to download version PDF', 'error');
    }
  };

  // CRUD for Sections
  const handleAddSkill = () => {
    setResumeData({
      ...resumeData,
      skills: [
        ...resumeData.skills,
        { skillName: 'New Skill', category: 'Technical', proficiencyLevel: 'Intermediate', skillType: 'CORE' },
      ],
    });
  };

  const handleRemoveSkill = (idx: number) => {
    setResumeData({
      ...resumeData,
      skills: resumeData.skills.filter((_, i) => i !== idx),
    });
  };

  const handleAddExperience = () => {
    setResumeData({
      ...resumeData,
      experiences: [
        ...resumeData.experiences,
        {
          role: 'Software Engineer',
          company: 'Company Name',
          duration: 'Jan 2024 - Present',
          location: 'San Francisco, CA',
          bullets: ['Engineered scalable microservices and enhanced platform reliability.'],
        },
      ],
    });
  };

  const handleRemoveExperience = (idx: number) => {
    setResumeData({
      ...resumeData,
      experiences: resumeData.experiences.filter((_, i) => i !== idx),
    });
  };

  const handleAddProject = () => {
    setResumeData({
      ...resumeData,
      projects: [
        ...resumeData.projects,
        {
          title: 'Full Stack Web Platform',
          technologies: 'React, Spring Boot, PostgreSQL',
          githubUrl: 'https://github.com/username/project',
          liveUrl: '',
          bullets: ['Architected modular backend REST API and created responsive user dashboard.'],
        },
      ],
    });
  };

  const handleRemoveProject = (idx: number) => {
    setResumeData({
      ...resumeData,
      projects: resumeData.projects.filter((_, i) => i !== idx),
    });
  };

  const handleAddBulletToProject = (pIdx: number) => {
    const updated = [...resumeData.projects];
    updated[pIdx].bullets.push('Developed core functionality with high test coverage.');
    setResumeData({ ...resumeData, projects: updated });
  };

  const handleRemoveBulletFromProject = (pIdx: number, bIdx: number) => {
    const updated = [...resumeData.projects];
    updated[pIdx].bullets = updated[pIdx].bullets.filter((_, i) => i !== bIdx);
    setResumeData({ ...resumeData, projects: updated });
  };

  const handleAddBulletToExp = (eIdx: number) => {
    const updated = [...resumeData.experiences];
    updated[eIdx].bullets.push('Collaborated with engineering team on sprints and code reviews.');
    setResumeData({ ...resumeData, experiences: updated });
  };

  const handleRemoveBulletFromExp = (eIdx: number, bIdx: number) => {
    const updated = [...resumeData.experiences];
    updated[eIdx].bullets = updated[eIdx].bullets.filter((_, i) => i !== bIdx);
    setResumeData({ ...resumeData, experiences: updated });
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] space-y-4">
        <RefreshCw className="w-8 h-8 text-indigo-600 animate-spin" />
        <p className="text-slate-600 font-medium">Extracting profile & verified credentials...</p>
      </div>
    );
  }

  const unapprovedCount = pendingSkills.filter((p) => !p.alreadyInResume).length;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-16">
      {/* Toast Notification */}
      {notification && (
        <div
          className={`fixed top-4 right-4 z-50 px-4 py-3 rounded-xl shadow-lg border text-sm font-semibold flex items-center gap-2 transition-all ${
            notification.type === 'error'
              ? 'bg-rose-50 border-rose-200 text-rose-800'
              : notification.type === 'info'
              ? 'bg-amber-50 border-amber-200 text-amber-800'
              : 'bg-emerald-50 border-emerald-200 text-emerald-800'
          }`}
        >
          {notification.type === 'error' ? (
            <AlertTriangle className="w-4 h-4 text-rose-600" />
          ) : (
            <CheckCircle2 className="w-4 h-4 text-emerald-600" />
          )}
          <span>{notification.text}</span>
          <button onClick={() => setNotification(null)} className="ml-2 text-slate-400 hover:text-slate-600">
            <X className="w-3.5 h-3.5" />
          </button>
        </div>
      )}

      {/* Header Bar */}
      <div className="bg-white rounded-2xl p-6 border border-slate-200/80 shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2 mb-1.5">
            <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-bold bg-indigo-50 text-indigo-700 border border-indigo-200">
              <FileCheck className="w-3.5 h-3.5" /> ATS-Compliant Single Column
            </span>
            {versions.length > 0 && (
              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-700">
                <FileText className="w-3 h-3" /> {versions.length} Saved {versions.length === 1 ? 'Version' : 'Versions'}
              </span>
            )}
          </div>
          <h1 className="text-2xl font-black text-slate-900 tracking-tight">AI Resume Builder & Tailor</h1>
          <p className="text-xs sm:text-sm text-slate-500">
            Generate an ATS-ready resume from verified credentials and tailor descriptions to your target job.
          </p>
        </div>

        {/* Action Controls */}
        <div className="flex flex-wrap items-center gap-2.5">
          {/* Pending Skills Button */}
          <button
            onClick={() => setShowPendingModal(true)}
            className="relative px-3.5 py-2 rounded-xl text-xs font-semibold bg-amber-50 hover:bg-amber-100 text-amber-900 border border-amber-200 transition-colors flex items-center gap-2"
          >
            <Award className="w-4 h-4 text-amber-600" />
            <span>Skill Updates</span>
            {unapprovedCount > 0 && (
              <span className="w-5 h-5 rounded-full bg-amber-600 text-white text-[10px] font-bold flex items-center justify-center">
                {unapprovedCount}
              </span>
            )}
          </button>

          {/* Job Tailoring Button */}
          <button
            onClick={() => setShowTailorModal(true)}
            className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-indigo-50 hover:bg-indigo-100 text-indigo-800 border border-indigo-200 transition-colors flex items-center gap-1.5"
          >
            <Sparkles className="w-4 h-4 text-indigo-600" />
            <span>Tailor to Job</span>
          </button>

          {/* Version History Button */}
          <button
            onClick={() => setShowVersionModal(true)}
            className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-slate-100 hover:bg-slate-200 text-slate-700 border border-slate-200 transition-colors flex items-center gap-1.5"
          >
            <FileText className="w-4 h-4 text-slate-600" />
            <span>Versions ({versions.length})</span>
          </button>

          {/* Save Version Button */}
          <button
            onClick={() => {
              setVersionTitle(`Resume v${versions.length + 1} - ${targetJobTitle || 'General'}`);
              setChangeSummary('');
              setShowVersionModal(true);
            }}
            className="px-3.5 py-2 rounded-xl text-xs font-semibold bg-slate-800 hover:bg-slate-900 text-white transition-colors flex items-center gap-1.5 shadow-sm"
          >
            <FileText className="w-4 h-4" />
            <span>Save Version</span>
          </button>

          {/* PDF Download Button */}
          <button
            onClick={handleExportPdf}
            disabled={exporting}
            className="px-4 py-2 rounded-xl text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white transition-all flex items-center gap-2 shadow-md hover:shadow-indigo-500/25 disabled:opacity-50"
          >
            {exporting ? (
              <RefreshCw className="w-4 h-4 animate-spin" />
            ) : (
              <Download className="w-4 h-4" />
            )}
            <span>Download PDF</span>
          </button>
        </div>
      </div>

      {/* Main Grid: Editor on Left, Live ATS Preview on Right */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* LEFT COLUMN: Section Navigation & Section Forms (5 cols) */}
        <div className="lg:col-span-5 space-y-4">
          {/* Section Selector Tabs */}
          <div className="flex flex-wrap gap-1 bg-slate-100/80 p-1 rounded-xl border border-slate-200 text-xs font-semibold text-slate-600">
            {[
              { key: 'personal', label: 'Contact' },
              { key: 'skills', label: `Skills (${resumeData.skills.length})` },
              { key: 'experience', label: `Exp (${resumeData.experiences.length})` },
              { key: 'projects', label: `Projects (${resumeData.projects.length})` },
              { key: 'education', label: 'Education' },
              { key: 'certifications', label: `Certs (${resumeData.certifications.length})` },
            ].map((tab) => (
              <button
                key={tab.key}
                onClick={() => setActiveSection(tab.key as any)}
                className={`flex-1 py-1.5 px-2 rounded-lg transition-all text-center ${
                  activeSection === tab.key
                    ? 'bg-white text-indigo-700 shadow-sm font-bold'
                    : 'hover:text-slate-900'
                }`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* Section: Contact & Personal */}
          {activeSection === 'personal' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider flex items-center gap-2">
                Personal & Contact Details
              </h2>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Full Name</label>
                  <input
                    type="text"
                    value={resumeData.fullName}
                    onChange={(e) => setResumeData({ ...resumeData, fullName: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="Candidate Name"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Email</label>
                  <input
                    type="email"
                    value={resumeData.email}
                    onChange={(e) => setResumeData({ ...resumeData, email: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="email@example.com"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Phone Number</label>
                  <input
                    type="text"
                    value={resumeData.phoneNumber || ''}
                    onChange={(e) => setResumeData({ ...resumeData, phoneNumber: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="+1 (555) 000-0000"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">Location</label>
                  <input
                    type="text"
                    value={resumeData.location || ''}
                    onChange={(e) => setResumeData({ ...resumeData, location: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="City, State / Country"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">LinkedIn Profile</label>
                  <input
                    type="text"
                    value={resumeData.linkedinUrl || ''}
                    onChange={(e) => setResumeData({ ...resumeData, linkedinUrl: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="linkedin.com/in/alex"
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-600 mb-1">GitHub Profile</label>
                  <input
                    type="text"
                    value={resumeData.githubUrl || ''}
                    onChange={(e) => setResumeData({ ...resumeData, githubUrl: e.target.value })}
                    className="w-full text-xs rounded-lg border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                    placeholder="github.com/alex"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-600 mb-1">Professional Summary</label>
                <textarea
                  rows={4}
                  value={resumeData.summary}
                  onChange={(e) => setResumeData({ ...resumeData, summary: e.target.value })}
                  className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                  placeholder="Concise overview of technical background and career objectives..."
                />
              </div>
            </div>
          )}

          {/* Section: Skills */}
          {activeSection === 'skills' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                    Technical & Verified Skills
                  </h2>
                  <p className="text-[11px] text-slate-500">
                    Distinguish between learned, assessed, and verified skills.
                  </p>
                </div>
                <button
                  onClick={handleAddSkill}
                  className="px-2.5 py-1 rounded-lg text-xs font-bold bg-indigo-50 text-indigo-700 hover:bg-indigo-100 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" /> Add Skill
                </button>
              </div>

              <div className="space-y-2.5 max-h-[460px] overflow-y-auto pr-1">
                {resumeData.skills.map((skill, idx) => (
                  <div
                    key={idx}
                    className="p-2.5 rounded-xl border border-slate-200 bg-slate-50/50 flex items-center justify-between gap-2"
                  >
                    <div className="flex-1 grid grid-cols-1 sm:grid-cols-3 gap-2">
                      <input
                        type="text"
                        value={skill.skillName}
                        onChange={(e) => {
                          const updated = [...resumeData.skills];
                          updated[idx].skillName = e.target.value;
                          setResumeData({ ...resumeData, skills: updated });
                        }}
                        className="text-xs font-semibold rounded border border-slate-300 px-2 py-1"
                        placeholder="Skill Name"
                      />
                      <input
                        type="text"
                        value={skill.category}
                        onChange={(e) => {
                          const updated = [...resumeData.skills];
                          updated[idx].category = e.target.value;
                          setResumeData({ ...resumeData, skills: updated });
                        }}
                        className="text-xs rounded border border-slate-300 px-2 py-1"
                        placeholder="Category (e.g. Languages, Tools)"
                      />
                      <div className="flex items-center gap-1.5">
                        <select
                          value={skill.skillType}
                          onChange={(e) => {
                            const updated = [...resumeData.skills];
                            const st = e.target.value as any;
                            updated[idx].skillType = st;
                            updated[idx].verified = st === 'VERIFIED';
                            setResumeData({ ...resumeData, skills: updated });
                          }}
                          className="text-[11px] rounded border border-slate-300 px-1.5 py-1 flex-1"
                        >
                          <option value="CORE">Core</option>
                          <option value="LEARNED">Learned (Course)</option>
                          <option value="ASSESSED">Assessed (Test)</option>
                          <option value="VERIFIED">Verified (Audit)</option>
                        </select>
                        {skill.verified && (
                          <span title="Verified Competency" className="text-emerald-600">
                            <CheckCircle2 className="w-4 h-4" />
                          </span>
                        )}
                      </div>
                    </div>
                    <button
                      onClick={() => handleRemoveSkill(idx)}
                      className="p-1 text-slate-400 hover:text-rose-600 rounded"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Section: Experience */}
          {activeSection === 'experience' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <div className="flex items-center justify-between">
                <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                  Professional Experience
                </h2>
                <button
                  onClick={handleAddExperience}
                  className="px-2.5 py-1 rounded-lg text-xs font-bold bg-indigo-50 text-indigo-700 hover:bg-indigo-100 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" /> Add Role
                </button>
              </div>

              <div className="space-y-4 max-h-[500px] overflow-y-auto pr-1">
                {resumeData.experiences.map((exp, eIdx) => (
                  <div key={eIdx} className="p-3 rounded-xl border border-slate-200 bg-slate-50 space-y-2.5">
                    <div className="flex items-center justify-between gap-2">
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 flex-1">
                        <input
                          type="text"
                          value={exp.role}
                          onChange={(e) => {
                            const updated = [...resumeData.experiences];
                            updated[eIdx].role = e.target.value;
                            setResumeData({ ...resumeData, experiences: updated });
                          }}
                          placeholder="Job Title / Role"
                          className="text-xs font-bold rounded border border-slate-300 px-2 py-1"
                        />
                        <input
                          type="text"
                          value={exp.company}
                          onChange={(e) => {
                            const updated = [...resumeData.experiences];
                            updated[eIdx].company = e.target.value;
                            setResumeData({ ...resumeData, experiences: updated });
                          }}
                          placeholder="Company"
                          className="text-xs rounded border border-slate-300 px-2 py-1"
                        />
                      </div>
                      <button
                        onClick={() => handleRemoveExperience(eIdx)}
                        className="text-slate-400 hover:text-rose-600 p-1"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <div className="grid grid-cols-2 gap-2">
                      <input
                        type="text"
                        value={exp.duration}
                        onChange={(e) => {
                          const updated = [...resumeData.experiences];
                          updated[eIdx].duration = e.target.value;
                          setResumeData({ ...resumeData, experiences: updated });
                        }}
                        placeholder="Dates (e.g. May 2024 - Present)"
                        className="text-xs rounded border border-slate-300 px-2 py-1"
                      />
                      <input
                        type="text"
                        value={exp.location || ''}
                        onChange={(e) => {
                          const updated = [...resumeData.experiences];
                          updated[eIdx].location = e.target.value;
                          setResumeData({ ...resumeData, experiences: updated });
                        }}
                        placeholder="Location"
                        className="text-xs rounded border border-slate-300 px-2 py-1"
                      />
                    </div>

                    {/* Bullets */}
                    <div className="space-y-1.5 pl-2 border-l-2 border-indigo-200">
                      <div className="flex items-center justify-between text-[11px] font-semibold text-slate-500">
                        <span>Action-Driven Bullets</span>
                        <button
                          onClick={() => handleAddBulletToExp(eIdx)}
                          className="text-indigo-600 hover:text-indigo-800 text-[10px]"
                        >
                          + Add Bullet
                        </button>
                      </div>
                      {exp.bullets.map((bullet, bIdx) => (
                        <div key={bIdx} className="flex items-center gap-1.5">
                          <span className="text-slate-400">•</span>
                          <input
                            type="text"
                            value={bullet}
                            onChange={(e) => {
                              const updated = [...resumeData.experiences];
                              updated[eIdx].bullets[bIdx] = e.target.value;
                              setResumeData({ ...resumeData, experiences: updated });
                            }}
                            className="flex-1 text-xs rounded border border-slate-300 px-2 py-1"
                          />
                          <button
                            onClick={() => handleRemoveBulletFromExp(eIdx, bIdx)}
                            className="text-slate-400 hover:text-rose-500"
                          >
                            <X className="w-3 h-3" />
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Section: Projects */}
          {activeSection === 'projects' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <div className="flex items-center justify-between">
                <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                  Technical Projects
                </h2>
                <button
                  onClick={handleAddProject}
                  className="px-2.5 py-1 rounded-lg text-xs font-bold bg-indigo-50 text-indigo-700 hover:bg-indigo-100 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" /> Add Project
                </button>
              </div>

              <div className="space-y-4 max-h-[500px] overflow-y-auto pr-1">
                {resumeData.projects.map((proj, pIdx) => (
                  <div key={pIdx} className="p-3 rounded-xl border border-slate-200 bg-slate-50 space-y-2.5">
                    <div className="flex items-center justify-between gap-2">
                      <input
                        type="text"
                        value={proj.title}
                        onChange={(e) => {
                          const updated = [...resumeData.projects];
                          updated[pIdx].title = e.target.value;
                          setResumeData({ ...resumeData, projects: updated });
                        }}
                        placeholder="Project Title"
                        className="text-xs font-bold rounded border border-slate-300 px-2 py-1 flex-1"
                      />
                      <button
                        onClick={() => handleRemoveProject(pIdx)}
                        className="text-slate-400 hover:text-rose-600 p-1"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </div>

                    <input
                      type="text"
                      value={proj.technologies}
                      onChange={(e) => {
                        const updated = [...resumeData.projects];
                        updated[pIdx].technologies = e.target.value;
                        setResumeData({ ...resumeData, projects: updated });
                      }}
                      placeholder="Tech Stack (e.g. Java, Spring Boot, React)"
                      className="w-full text-xs rounded border border-slate-300 px-2 py-1"
                    />

                    <div className="grid grid-cols-2 gap-2">
                      <input
                        type="text"
                        value={proj.githubUrl || ''}
                        onChange={(e) => {
                          const updated = [...resumeData.projects];
                          updated[pIdx].githubUrl = e.target.value;
                          setResumeData({ ...resumeData, projects: updated });
                        }}
                        placeholder="GitHub URL"
                        className="text-xs rounded border border-slate-300 px-2 py-1"
                      />
                      <input
                        type="text"
                        value={proj.liveUrl || ''}
                        onChange={(e) => {
                          const updated = [...resumeData.projects];
                          updated[pIdx].liveUrl = e.target.value;
                          setResumeData({ ...resumeData, projects: updated });
                        }}
                        placeholder="Demo URL"
                        className="text-xs rounded border border-slate-300 px-2 py-1"
                      />
                    </div>

                    {/* Bullets */}
                    <div className="space-y-1.5 pl-2 border-l-2 border-indigo-200">
                      <div className="flex items-center justify-between text-[11px] font-semibold text-slate-500">
                        <span>Project Description Bullets</span>
                        <button
                          onClick={() => handleAddBulletToProject(pIdx)}
                          className="text-indigo-600 hover:text-indigo-800 text-[10px]"
                        >
                          + Add Bullet
                        </button>
                      </div>
                      {proj.bullets.map((bullet, bIdx) => (
                        <div key={bIdx} className="flex items-center gap-1.5">
                          <span className="text-slate-400">•</span>
                          <input
                            type="text"
                            value={bullet}
                            onChange={(e) => {
                              const updated = [...resumeData.projects];
                              updated[pIdx].bullets[bIdx] = e.target.value;
                              setResumeData({ ...resumeData, projects: updated });
                            }}
                            className="flex-1 text-xs rounded border border-slate-300 px-2 py-1"
                          />
                          <button
                            onClick={() => handleRemoveBulletFromProject(pIdx, bIdx)}
                            className="text-slate-400 hover:text-rose-500"
                          >
                            <X className="w-3 h-3" />
                          </button>
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Section: Education */}
          {activeSection === 'education' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                Academic Background
              </h2>
              {resumeData.education.map((edu, idx) => (
                <div key={idx} className="p-3 rounded-xl border border-slate-200 bg-slate-50 space-y-2">
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      value={edu.degree}
                      onChange={(e) => {
                        const updated = [...resumeData.education];
                        updated[idx].degree = e.target.value;
                        setResumeData({ ...resumeData, education: updated });
                      }}
                      placeholder="Degree"
                      className="text-xs font-semibold rounded border border-slate-300 px-2 py-1"
                    />
                    <input
                      type="text"
                      value={edu.branch}
                      onChange={(e) => {
                        const updated = [...resumeData.education];
                        updated[idx].branch = e.target.value;
                        setResumeData({ ...resumeData, education: updated });
                      }}
                      placeholder="Branch / Major"
                      className="text-xs rounded border border-slate-300 px-2 py-1"
                    />
                  </div>
                  <input
                    type="text"
                    value={edu.institution}
                    onChange={(e) => {
                      const updated = [...resumeData.education];
                      updated[idx].institution = e.target.value;
                      setResumeData({ ...resumeData, education: updated });
                    }}
                    placeholder="Institution / College"
                    className="w-full text-xs rounded border border-slate-300 px-2 py-1"
                  />
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      value={edu.graduationYear}
                      onChange={(e) => {
                        const updated = [...resumeData.education];
                        updated[idx].graduationYear = e.target.value;
                        setResumeData({ ...resumeData, education: updated });
                      }}
                      placeholder="Graduation Year"
                      className="text-xs rounded border border-slate-300 px-2 py-1"
                    />
                    <input
                      type="text"
                      value={edu.gradeGpa || ''}
                      onChange={(e) => {
                        const updated = [...resumeData.education];
                        updated[idx].gradeGpa = e.target.value;
                        setResumeData({ ...resumeData, education: updated });
                      }}
                      placeholder="GPA / Grade"
                      className="text-xs rounded border border-slate-300 px-2 py-1"
                    />
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Section: Certifications */}
          {activeSection === 'certifications' && (
            <div className="bg-white rounded-2xl p-5 border border-slate-200 space-y-4 shadow-sm">
              <h2 className="text-sm font-bold text-slate-800 uppercase tracking-wider">
                Certifications & Accreditations
              </h2>
              {resumeData.certifications.map((cert, idx) => (
                <div key={idx} className="p-3 rounded-xl border border-slate-200 bg-slate-50 space-y-2">
                  <input
                    type="text"
                    value={cert.name}
                    onChange={(e) => {
                      const updated = [...resumeData.certifications];
                      updated[idx].name = e.target.value;
                      setResumeData({ ...resumeData, certifications: updated });
                    }}
                    placeholder="Certification Name"
                    className="w-full text-xs font-semibold rounded border border-slate-300 px-2 py-1"
                  />
                  <div className="grid grid-cols-2 gap-2">
                    <input
                      type="text"
                      value={cert.issuer}
                      onChange={(e) => {
                        const updated = [...resumeData.certifications];
                        updated[idx].issuer = e.target.value;
                        setResumeData({ ...resumeData, certifications: updated });
                      }}
                      placeholder="Issuing Organization"
                      className="text-xs rounded border border-slate-300 px-2 py-1"
                    />
                    <input
                      type="text"
                      value={cert.issueYear || ''}
                      onChange={(e) => {
                        const updated = [...resumeData.certifications];
                        updated[idx].issueYear = e.target.value;
                        setResumeData({ ...resumeData, certifications: updated });
                      }}
                      placeholder="Year"
                      className="text-xs rounded border border-slate-300 px-2 py-1"
                    />
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* RIGHT COLUMN: Live ATS-Compliant Document Preview (7 cols) */}
        <div className="lg:col-span-7">
          <div className="sticky top-6">
            <div className="bg-slate-900 rounded-t-2xl px-5 py-3 text-white flex items-center justify-between text-xs font-medium">
              <span className="flex items-center gap-1.5">
                <FileText className="w-4 h-4 text-emerald-400" />
                <span>Live ATS Document Preview (Single-Column Standard)</span>
              </span>
              <button
                onClick={handleExportPdf}
                disabled={exporting}
                className="text-indigo-300 hover:text-white flex items-center gap-1 font-semibold"
              >
                <Download className="w-3.5 h-3.5" /> PDF Preview
              </button>
            </div>

            {/* Document Paper Container */}
            <div className="bg-white rounded-b-2xl border-x border-b border-slate-200 p-8 shadow-md font-sans text-slate-800 space-y-5 min-h-[750px]">
              {/* 1. Header (Centered Name and Contact Line) */}
              <div className="text-center space-y-1.5 pb-2 border-b border-slate-300">
                <h1 className="text-xl sm:text-2xl font-black text-slate-900 tracking-tight">
                  {resumeData.fullName || 'Candidate Name'}
                </h1>
                <div className="text-[11px] text-slate-600 flex flex-wrap justify-center items-center gap-x-2 gap-y-0.5">
                  {resumeData.email && <span>{resumeData.email}</span>}
                  {resumeData.phoneNumber && <span>• {resumeData.phoneNumber}</span>}
                  {resumeData.location && <span>• {resumeData.location}</span>}
                  {resumeData.linkedinUrl && <span>• {resumeData.linkedinUrl}</span>}
                  {resumeData.githubUrl && <span>• {resumeData.githubUrl}</span>}
                </div>
              </div>

              {/* 2. Professional Summary */}
              {resumeData.summary && (
                <div className="space-y-1">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Professional Summary
                  </h3>
                  <p className="text-[11px] text-slate-700 leading-relaxed text-justify">
                    {resumeData.summary}
                  </p>
                </div>
              )}

              {/* 3. Technical Skills */}
              {resumeData.skills.length > 0 && (
                <div className="space-y-1.5">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Technical & Verified Skills
                  </h3>
                  <div className="space-y-1 text-[11px]">
                    {Object.entries(
                      resumeData.skills.reduce((acc, s) => {
                        const cat = s.category || 'Core Competencies';
                        if (!acc[cat]) acc[cat] = [];
                        acc[cat].push(s);
                        return acc;
                      }, {} as Record<string, ResumeSkillItem[]>)
                    ).map(([category, items]) => (
                      <div key={category} className="flex flex-wrap items-baseline gap-1">
                        <span className="font-bold text-slate-800">{category}:</span>
                        {items.map((it, idx) => (
                          <span key={idx} className="inline-flex items-center gap-1">
                            <span className="text-slate-700">{it.skillName}</span>
                            {it.verified ? (
                              <span className="text-[9px] font-bold px-1 rounded bg-emerald-100 text-emerald-800">
                                [Verified]
                              </span>
                            ) : it.skillType === 'ASSESSED' ? (
                              <span className="text-[9px] font-medium px-1 rounded bg-blue-50 text-blue-700">
                                [Assessed]
                              </span>
                            ) : it.skillType === 'LEARNED' ? (
                              <span className="text-[9px] font-medium px-1 rounded bg-amber-50 text-amber-700">
                                [Learned]
                              </span>
                            ) : null}
                            {idx < items.length - 1 && <span className="text-slate-400">,</span>}
                          </span>
                        ))}
                      </div>
                    ))}
                  </div>
                </div>
              )}

              {/* 4. Experience */}
              {resumeData.experiences.length > 0 && (
                <div className="space-y-2.5">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Professional Experience
                  </h3>
                  {resumeData.experiences.map((exp, idx) => (
                    <div key={idx} className="space-y-0.5 text-[11px]">
                      <div className="flex justify-between items-baseline font-bold text-slate-900">
                        <span>{exp.role}</span>
                        <span className="text-[10px] text-slate-500 font-normal">{exp.duration}</span>
                      </div>
                      <div className="text-[10px] text-slate-600 italic">
                        {exp.company} {exp.location ? `— ${exp.location}` : ''}
                      </div>
                      <ul className="list-disc list-outside pl-4 space-y-0.5 text-slate-700 pt-0.5">
                        {exp.bullets.map((b, bIdx) => (
                          <li key={bIdx} className="leading-snug">{b}</li>
                        ))}
                      </ul>
                    </div>
                  ))}
                </div>
              )}

              {/* 5. Projects */}
              {resumeData.projects.length > 0 && (
                <div className="space-y-2.5">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Technical Projects
                  </h3>
                  {resumeData.projects.map((proj, idx) => (
                    <div key={idx} className="space-y-0.5 text-[11px]">
                      <div className="flex justify-between items-baseline font-bold text-slate-900">
                        <span>
                          {proj.title}
                          {proj.technologies && (
                            <span className="font-normal text-slate-600 text-[10px] ml-1.5">
                              | Tech: {proj.technologies}
                            </span>
                          )}
                        </span>
                        {proj.githubUrl && (
                          <span className="text-[10px] text-indigo-600 font-normal">
                            {proj.githubUrl}
                          </span>
                        )}
                      </div>
                      <ul className="list-disc list-outside pl-4 space-y-0.5 text-slate-700 pt-0.5">
                        {proj.bullets.map((b, bIdx) => (
                          <li key={bIdx} className="leading-snug">{b}</li>
                        ))}
                      </ul>
                    </div>
                  ))}
                </div>
              )}

              {/* 6. Education */}
              {resumeData.education.length > 0 && (
                <div className="space-y-1.5">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Education
                  </h3>
                  {resumeData.education.map((edu, idx) => (
                    <div key={idx} className="text-[11px] flex justify-between items-baseline">
                      <div>
                        <span className="font-bold text-slate-900">{edu.degree}</span>
                        {edu.branch && <span> in {edu.branch}</span>}
                        <div className="text-[10px] text-slate-600">
                          {edu.institution} {edu.gradeGpa ? `| GPA: ${edu.gradeGpa}` : ''}
                        </div>
                      </div>
                      <span className="text-[10px] text-slate-500">{edu.graduationYear}</span>
                    </div>
                  ))}
                </div>
              )}

              {/* 7. Certifications */}
              {resumeData.certifications.length > 0 && (
                <div className="space-y-1.5">
                  <h3 className="text-xs font-bold text-slate-900 uppercase tracking-widest border-b border-slate-300 pb-0.5">
                    Certifications & Credentials
                  </h3>
                  <div className="space-y-0.5 text-[11px] text-slate-700">
                    {resumeData.certifications.map((cert, idx) => (
                      <div key={idx}>
                        • <span className="font-semibold text-slate-800">{cert.name}</span> — {cert.issuer}{' '}
                        {cert.issueYear && <span className="text-slate-500">({cert.issueYear})</span>}
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      {/* MODAL 1: Pending Skill Updates (Learned vs Assessed vs Verified) */}
      {showPendingModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-2xl w-full p-6 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <div className="flex items-center gap-2">
                <Award className="w-5 h-5 text-amber-600" />
                <h3 className="text-base font-bold text-slate-900">
                  Skill Updates & Verification Review
                </h3>
              </div>
              <button
                onClick={() => setShowPendingModal(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Crucial Ethical Guidance Alert */}
            <div className="p-3.5 rounded-xl bg-amber-50 border border-amber-200 text-xs text-amber-900 space-y-1">
              <p className="font-bold flex items-center gap-1.5">
                <AlertTriangle className="w-4 h-4 text-amber-700 shrink-0" />
                Transparent Credential Integrity Rule:
              </p>
              <p>
                Completing a lesson does <strong>not</strong> automatically imply professional proficiency.
                Skills are categorized as <strong>Learned</strong> (concept studied), <strong>Assessed</strong> (exam passed), or <strong>Verified</strong> (code audited). Review and approve each change before adding it to your resume.
              </p>
            </div>

            {pendingSkills.length === 0 ? (
              <div className="text-center py-8 text-slate-500 space-y-2">
                <CheckCircle2 className="w-10 h-10 text-emerald-500 mx-auto" />
                <p className="text-sm font-semibold">Your resume is fully synchronized with your verified skills!</p>
                <p className="text-xs">Take new module quizzes or pass mock tests to unlock additional resume recommendations.</p>
              </div>
            ) : (
              <div className="space-y-3">
                {pendingSkills.map((item, idx) => (
                  <div
                    key={idx}
                    className="p-4 rounded-xl border border-slate-200 bg-slate-50/70 space-y-2"
                  >
                    <div className="flex items-start justify-between gap-2">
                      <div>
                        <div className="flex items-center gap-2">
                          <span className="text-sm font-bold text-slate-900">{item.skillName}</span>
                          <span
                            className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                              item.skillType === 'VERIFIED'
                                ? 'bg-emerald-100 text-emerald-800 border border-emerald-300'
                                : item.skillType === 'ASSESSED'
                                ? 'bg-blue-100 text-blue-800 border border-blue-300'
                                : 'bg-amber-100 text-amber-800 border border-amber-300'
                            }`}
                          >
                            {item.skillType}
                          </span>
                          {item.alreadyInResume && (
                            <span className="text-[10px] font-medium text-slate-500">
                              (Already on Resume)
                            </span>
                          )}
                        </div>
                        <div className="text-xs text-slate-600 mt-0.5">
                          Source: <span className="font-semibold">{item.source}</span>
                        </div>
                      </div>

                      <div className="flex items-center gap-2">
                        <button
                          onClick={() => approvePendingSkill(item)}
                          className="px-3 py-1.5 rounded-lg text-xs font-bold bg-emerald-600 hover:bg-emerald-700 text-white flex items-center gap-1 shadow-sm"
                        >
                          <Check className="w-3.5 h-3.5" /> Approve & Add
                        </button>
                        <button
                          onClick={() => declinePendingSkill(item)}
                          className="px-2.5 py-1.5 rounded-lg text-xs font-semibold bg-slate-200 hover:bg-slate-300 text-slate-700"
                        >
                          Decline
                        </button>
                      </div>
                    </div>

                    <div className="text-xs text-slate-700 bg-white p-2.5 rounded-lg border border-slate-200">
                      <span className="font-semibold text-slate-900">Suggested Action: </span>
                      {item.suggestedBulletOrEntry}
                    </div>

                    <div className="text-[11px] text-slate-500 italic">
                      Note: {item.recommendationNote}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODAL 2: Job Tailoring & Bullet Optimization */}
      {showTailorModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-3xl w-full p-6 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <div className="flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-indigo-600" />
                <h3 className="text-base font-bold text-slate-900">
                  Target Job Description Tailor & Optimizer
                </h3>
              </div>
              <button
                onClick={() => setShowTailorModal(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Target Job Inputs */}
            <div className="space-y-3">
              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Target Job Title
                </label>
                <input
                  type="text"
                  value={targetJobTitle}
                  onChange={(e) => setTargetJobTitle(e.target.value)}
                  placeholder="e.g. Senior Backend Engineer"
                  className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <div>
                <label className="block text-xs font-semibold text-slate-700 mb-1">
                  Paste Target Job Description (JD)
                </label>
                <textarea
                  rows={5}
                  value={targetJobDescription}
                  onChange={(e) => setTargetJobDescription(e.target.value)}
                  placeholder="Paste the target job requirements, qualifications, and role responsibilities..."
                  className="w-full text-xs rounded-lg border border-slate-300 p-2.5 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>

              <button
                onClick={handleTailorResume}
                disabled={tailoring || !targetJobDescription.trim()}
                className="w-full py-2.5 rounded-xl text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white transition-all flex items-center justify-center gap-2 shadow-sm disabled:opacity-50"
              >
                {tailoring ? (
                  <>
                    <RefreshCw className="w-4 h-4 animate-spin" />
                    <span>Analyzing ATS Keywords & Formulating Stronger Bullets...</span>
                  </>
                ) : (
                  <>
                    <Target className="w-4 h-4" />
                    <span>Analyze & Tailor Resume</span>
                  </>
                )}
              </button>
            </div>

            {/* Tailor Results */}
            {tailorResult && (
              <div className="space-y-4 pt-4 border-t border-slate-200">
                {/* Score & Summary */}
                <div className="p-4 rounded-xl bg-indigo-50 border border-indigo-200 flex flex-col sm:flex-row items-center justify-between gap-4">
                  <div>
                    <div className="text-xs font-bold text-indigo-900 uppercase tracking-wider">
                      Target Role Match Score
                    </div>
                    <div className="text-xs text-indigo-700 mt-0.5">
                      {tailorResult.alignmentSummary}
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-3xl font-black text-indigo-900">{tailorResult.matchScore}%</span>
                    <span className="text-[11px] text-indigo-600 max-w-[80px]">ATS Keyword Density</span>
                  </div>
                </div>

                {/* Keywords Matched vs Missing */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                  <div className="p-3 rounded-xl border border-emerald-200 bg-emerald-50/50 space-y-1.5">
                    <div className="font-bold text-emerald-900 flex items-center gap-1.5">
                      <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                      <span>Matched Keywords ({tailorResult.matchedKeywords.length})</span>
                    </div>
                    <div className="flex flex-wrap gap-1">
                      {tailorResult.matchedKeywords.map((kw, i) => (
                        <span key={i} className="px-2 py-0.5 rounded-full bg-emerald-100 text-emerald-800 text-[10px] font-semibold">
                          {kw}
                        </span>
                      ))}
                    </div>
                  </div>

                  <div className="p-3 rounded-xl border border-amber-200 bg-amber-50/50 space-y-1.5">
                    <div className="font-bold text-amber-900 flex items-center gap-1.5">
                      <AlertTriangle className="w-4 h-4 text-amber-600" />
                      <span>Missing Target Keywords ({tailorResult.missingKeywords.length})</span>
                    </div>
                    <div className="flex flex-wrap gap-1">
                      {tailorResult.missingKeywords.map((kw, i) => (
                        <span key={i} className="px-2 py-0.5 rounded-full bg-amber-100 text-amber-800 text-[10px] font-semibold">
                          {kw}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Recommendations */}
                {tailorResult.keywordRecommendations.length > 0 && (
                  <div className="p-3.5 rounded-xl border border-slate-200 bg-slate-50 text-xs space-y-1.5">
                    <span className="font-bold text-slate-800">Actionable ATS Recommendations:</span>
                    <ul className="list-disc list-inside space-y-0.5 text-slate-600">
                      {tailorResult.keywordRecommendations.map((rec, i) => (
                        <li key={i}>{rec}</li>
                      ))}
                    </ul>
                  </div>
                )}

                {/* Recommended Bullet Improvements */}
                {tailorResult.bulletImprovements.length > 0 && (
                  <div className="space-y-2">
                    <div className="flex items-center justify-between">
                      <span className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                        Recommended Action-Oriented Bullet Enhancements
                      </span>
                      <span className="text-[10px] text-slate-500 italic">
                        Never invents qualifications; highlights technical impact.
                      </span>
                    </div>

                    <div className="space-y-2 max-h-[250px] overflow-y-auto">
                      {tailorResult.bulletImprovements.map((imp, i) => (
                        <div key={i} className="p-3 rounded-xl border border-slate-200 bg-slate-50 text-xs space-y-1.5">
                          <div className="flex items-center justify-between">
                            <span className="font-bold text-indigo-900">
                              [{imp.section}] {imp.itemTitle}
                            </span>
                            {imp.accepted ? (
                              <span className="text-emerald-700 font-bold flex items-center gap-1 text-[11px]">
                                <Check className="w-3.5 h-3.5" /> Accepted
                              </span>
                            ) : (
                              <button
                                onClick={() => handleAcceptBullet(imp)}
                                className="px-2.5 py-1 rounded bg-indigo-600 hover:bg-indigo-700 text-white font-bold text-[10px] flex items-center gap-1"
                              >
                                Accept & Apply
                              </button>
                            )}
                          </div>

                          <div className="text-slate-500 line-through">
                            {imp.originalBullet}
                          </div>

                          <div className="text-emerald-900 font-medium bg-emerald-50 p-2 rounded border border-emerald-200">
                            {imp.improvedBullet}
                          </div>

                          <div className="text-[10px] text-slate-500">
                            Rationale: {imp.rationale}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {/* Apply Tailored Summary */}
                {tailorResult.tailoredResume?.summary && (
                  <div className="p-3 rounded-xl border border-indigo-200 bg-indigo-50/50 flex items-center justify-between gap-2">
                    <div>
                      <div className="text-xs font-bold text-indigo-900">Suggested Tailored Summary</div>
                      <div className="text-[11px] text-slate-600 line-clamp-2 mt-0.5">
                        {tailorResult.tailoredResume.summary}
                      </div>
                    </div>
                    <button
                      onClick={handleApplyTailoredSummary}
                      className="px-3 py-1.5 rounded-lg text-xs font-bold bg-indigo-600 hover:bg-indigo-700 text-white shrink-0"
                    >
                      Apply Summary
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>
        </div>
      )}

      {/* MODAL 3: Version History & Save */}
      {showVersionModal && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl max-w-2xl w-full p-6 space-y-4 shadow-2xl max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-slate-200">
              <div className="flex items-center gap-2">
                <FileText className="w-5 h-5 text-slate-700" />
                <h3 className="text-base font-bold text-slate-900">
                  Resume Version History & Snapshotting
                </h3>
              </div>
              <button
                onClick={() => setShowVersionModal(false)}
                className="text-slate-400 hover:text-slate-600 p-1"
              >
                <X className="w-5 h-5" />
              </button>
            </div>

            {/* Save New Version Form */}
            <div className="p-4 rounded-xl bg-slate-50 border border-slate-200 space-y-3">
              <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Save Current Resume as New Version
              </h4>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                <input
                  type="text"
                  value={versionTitle}
                  onChange={(e) => setVersionTitle(e.target.value)}
                  placeholder="Version Title (e.g. Fintech Senior Backend v2)"
                  className="text-xs rounded border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
                <input
                  type="text"
                  value={changeSummary}
                  onChange={(e) => setChangeSummary(e.target.value)}
                  placeholder="Change Summary (e.g. Added verified Spring Boot)"
                  className="text-xs rounded border border-slate-300 p-2 focus:ring-2 focus:ring-indigo-500 focus:outline-none"
                />
              </div>
              <button
                onClick={handleSaveVersion}
                disabled={saving || !versionTitle.trim()}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-slate-900 hover:bg-slate-800 text-white flex items-center gap-1.5 shadow-sm disabled:opacity-50"
              >
                {saving ? (
                  <RefreshCw className="w-3.5 h-3.5 animate-spin" />
                ) : (
                  <Check className="w-3.5 h-3.5" />
                )}
                <span>Save New Version</span>
              </button>
            </div>

            {/* Version List */}
            <div className="space-y-2">
              <h4 className="text-xs font-bold text-slate-900 uppercase tracking-wider">
                Preserved Versions ({versions.length})
              </h4>
              {versions.length === 0 ? (
                <p className="text-xs text-slate-500 italic py-4 text-center">
                  No previous versions saved yet. Save a snapshot above to preserve resume iterations.
                </p>
              ) : (
                <div className="space-y-2 max-h-[300px] overflow-y-auto">
                  {versions.map((ver) => (
                    <div
                      key={ver.id}
                      className="p-3.5 rounded-xl border border-slate-200 bg-white hover:bg-slate-50 transition-colors flex items-center justify-between gap-3 text-xs"
                    >
                      <div className="space-y-0.5">
                        <div className="flex items-center gap-2">
                          <span className="font-bold text-slate-900">
                            v{ver.versionNumber}: {ver.title}
                          </span>
                          <span className="text-[10px] text-slate-500">
                            {new Date(ver.createdAt).toLocaleDateString()}
                          </span>
                        </div>
                        {ver.changeSummary && (
                          <div className="text-[11px] text-slate-600">
                            Summary: {ver.changeSummary}
                          </div>
                        )}
                        {ver.targetJobTitle && (
                          <div className="text-[10px] text-indigo-700 font-medium">
                            Target: {ver.targetJobTitle}
                          </div>
                        )}
                      </div>

                      <div className="flex items-center gap-1.5">
                        <button
                          onClick={() => handleLoadVersion(ver)}
                          className="px-2.5 py-1.5 rounded-lg text-[11px] font-semibold bg-slate-100 hover:bg-slate-200 text-slate-700"
                        >
                          Load
                        </button>
                        <button
                          onClick={() => handleDownloadVersionPdf(ver)}
                          className="px-2.5 py-1.5 rounded-lg text-[11px] font-bold bg-indigo-50 hover:bg-indigo-100 text-indigo-700 flex items-center gap-1"
                        >
                          <Download className="w-3 h-3" /> PDF
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ResumeBuilderPage;
