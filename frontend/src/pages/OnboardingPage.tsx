import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { analysisService } from '../services/analysisService';
import { OnboardingRequest, SkillItem, ProjectItem, ExperienceItem, CertificationItem } from '../types';
import { Alert } from '../components/Alert';
import {
  Compass,
  ArrowRight,
  ArrowLeft,
  Briefcase,
  GraduationCap,
  CheckCircle2,
  Sparkles,
  Loader2,
  User,
  Mail,
  Phone,
  MapPin,
  Building2,
  Code,
  Layers,
  Award,
  Plus,
  Trash2,
  Check,
} from 'lucide-react';

export const OnboardingPage: React.FC = () => {
  const { user, refreshUser } = useAuth();
  const navigate = useNavigate();

  const [currentStep, setCurrentStep] = useState(1);
  const totalSteps = 6;

  // Step 1: Personal
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [email] = useState(user?.email || '');
  const [phoneNumber, setPhoneNumber] = useState(user?.phoneNumber || '');
  const [location, setLocation] = useState(user?.location || '');

  // Step 2: Academic
  const [degree, setDegree] = useState(user?.degree || 'B.Tech');
  const [branch, setBranch] = useState(user?.branch || 'Computer Science & Engineering');
  const [college, setCollege] = useState(user?.college || '');
  const [graduationYear, setGraduationYear] = useState<number>(user?.graduationYear || new Date().getFullYear());
  const [academicStatus, setAcademicStatus] = useState(user?.academicStatus || 'Final Year Student');

  // Step 3: Target Role & Industry
  const [targetRole, setTargetRole] = useState(user?.targetRole || 'Full Stack Java Developer');
  const [preferredIndustry, setPreferredIndustry] = useState(user?.preferredIndustry || 'Software & Internet / SaaS');
  const [yearsOfExperience, setYearsOfExperience] = useState<number>(user?.yearsOfExperience || 0);
  const [bio, setBio] = useState(user?.bio || 'Passionate software developer focused on building scalable, reliable distributed systems.');

  // Step 4: Skills
  const [technicalSkills, setTechnicalSkills] = useState<SkillItem[]>([
    { skillName: 'Java', proficiencyLevel: 'Advanced', category: 'Technical' },
    { skillName: 'Spring Boot', proficiencyLevel: 'Intermediate', category: 'Technical' },
    { skillName: 'React', proficiencyLevel: 'Intermediate', category: 'Technical' },
    { skillName: 'PostgreSQL', proficiencyLevel: 'Intermediate', category: 'Technical' },
  ]);
  const [newSkillName, setNewSkillName] = useState('');
  const [newSkillLevel, setNewSkillLevel] = useState<'Beginner' | 'Intermediate' | 'Advanced' | 'Expert'>('Intermediate');

  const [softSkills, setSoftSkills] = useState<string[]>([
    'Problem Solving',
    'Communication',
    'Team Collaboration',
  ]);
  const [newSoftSkill, setNewSoftSkill] = useState('');

  // Step 5: Projects
  const [projects, setProjects] = useState<ProjectItem[]>([
    {
      title: 'CareerGap AI Platform',
      description: 'AI-assisted resume skill gap analyzer that generates personalized week-by-week learning roadmaps.',
      technologies: 'Spring Boot, React, TypeScript, PostgreSQL',
      githubUrl: 'https://github.com/example/careergap',
      liveUrl: '',
    },
  ]);

  // Step 6: Certifications & Experience
  const [experiences, setExperiences] = useState<ExperienceItem[]>([
    {
      title: 'Software Engineering Intern',
      company: 'Tech Solutions Inc',
      duration: 'May 2024 - Aug 2024',
      description: 'Assisted in building RESTful microservices and frontend client components.',
    },
  ]);

  const [certifications, setCertifications] = useState<CertificationItem[]>([
    {
      name: 'Oracle Certified Associate: Java SE',
      issuer: 'Oracle',
      issueYear: 2024,
      credentialUrl: '',
    },
  ]);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Skill Handlers
  const addTechnicalSkill = () => {
    if (!newSkillName.trim()) return;
    if (technicalSkills.some((s) => s.skillName.toLowerCase() === newSkillName.trim().toLowerCase())) return;
    setTechnicalSkills([...technicalSkills, { skillName: newSkillName.trim(), proficiencyLevel: newSkillLevel, category: 'Technical' }]);
    setNewSkillName('');
  };

  const removeTechnicalSkill = (index: number) => {
    setTechnicalSkills(technicalSkills.filter((_, i) => i !== index));
  };

  const addSoftSkill = () => {
    if (!newSoftSkill.trim()) return;
    if (softSkills.includes(newSoftSkill.trim())) return;
    setSoftSkills([...softSkills, newSoftSkill.trim()]);
    setNewSoftSkill('');
  };

  const removeSoftSkill = (index: number) => {
    setSoftSkills(softSkills.filter((_, i) => i !== index));
  };

  // Project Handlers
  const addProject = () => {
    setProjects([
      ...projects,
      {
        title: '',
        description: '',
        technologies: '',
        githubUrl: '',
        liveUrl: '',
      },
    ]);
  };

  const removeProject = (index: number) => {
    setProjects(projects.filter((_, i) => i !== index));
  };

  // Experience Handlers
  const addExperience = () => {
    setExperiences([
      ...experiences,
      {
        title: '',
        company: '',
        duration: '',
        description: '',
      },
    ]);
  };

  const removeExperience = (index: number) => {
    setExperiences(experiences.filter((_, i) => i !== index));
  };

  // Certification Handlers
  const addCertification = () => {
    setCertifications([
      ...certifications,
      {
        name: '',
        issuer: '',
        issueYear: new Date().getFullYear(),
        credentialUrl: '',
      },
    ]);
  };

  const removeCertification = (index: number) => {
    setCertifications(certifications.filter((_, i) => i !== index));
  };

  const handleFinish = async () => {
    setLoading(true);
    setError(null);
    try {
      const payload: OnboardingRequest = {
        fullName,
        phoneNumber,
        location,
        degree,
        branch,
        college,
        graduationYear,
        academicStatus,
        targetRole,
        preferredIndustry,
        yearsOfExperience,
        bio,
        technicalSkills,
        softSkills,
        projects: projects.filter((p) => p.title.trim().length > 0),
        experiences: experiences.filter((e) => e.title.trim().length > 0),
        certifications: certifications.filter((c) => c.name.trim().length > 0),
      };

      await analysisService.saveOnboarding(payload);
      await refreshUser();
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to save onboarding profile');
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] bg-slate-50 py-10 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto space-y-8">
        
        {/* Progress Bar & Header */}
        <div className="bg-white p-6 sm:p-8 rounded-3xl border border-slate-200 shadow-sm space-y-4">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-500 text-white flex items-center justify-center shadow-md shadow-emerald-500/20">
                <Compass className="w-5 h-5" />
              </div>
              <div>
                <h1 className="text-xl font-extrabold text-slate-900">Career Onboarding</h1>
                <p className="text-xs text-slate-500">Step {currentStep} of {totalSteps}: {
                  currentStep === 1 ? 'Personal & Contact' :
                  currentStep === 2 ? 'Academic Background' :
                  currentStep === 3 ? 'Target Role & Industry' :
                  currentStep === 4 ? 'Skills & Competencies' :
                  currentStep === 5 ? 'Projects & Portfolio' :
                  'Certifications & Experience'
                }</p>
              </div>
            </div>

            <div className="flex items-center gap-1.5">
              {Array.from({ length: totalSteps }).map((_, idx) => (
                <div
                  key={idx}
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all ${
                    currentStep === idx + 1
                      ? 'bg-emerald-600 text-white ring-4 ring-emerald-100'
                      : currentStep > idx + 1
                      ? 'bg-emerald-100 text-emerald-800'
                      : 'bg-slate-100 text-slate-400'
                  }`}
                >
                  {currentStep > idx + 1 ? <Check className="w-3.5 h-3.5" /> : idx + 1}
                </div>
              ))}
            </div>
          </div>

          <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
            <div
              className="bg-emerald-600 h-2 rounded-full transition-all duration-300"
              style={{ width: `${(currentStep / totalSteps) * 100}%` }}
            />
          </div>
        </div>

        {error && <Alert type="error" message={error} onClose={() => setError(null)} />}

        {/* Step 1: Personal & Contact */}
        {currentStep === 1 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="space-y-1">
              <h2 className="text-xl font-extrabold text-slate-900">Personal & Contact Details</h2>
              <p className="text-xs text-slate-500">Verify your name and optional contact info for recruiter-ready exports.</p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Full Name *</label>
                <div className="relative">
                  <User className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    required
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="Alex Johnson"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Email Address</label>
                <div className="relative">
                  <Mail className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="email"
                    disabled
                    value={email}
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-200 bg-slate-50 text-slate-400 text-sm cursor-not-allowed"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Phone Number (Optional)</label>
                <div className="relative">
                  <Phone className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={phoneNumber}
                    onChange={(e) => setPhoneNumber(e.target.value)}
                    placeholder="+1 (555) 000-0000"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Current City / Location (Optional)</label>
                <div className="relative">
                  <MapPin className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="e.g. Austin, TX or Remote"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>
            </div>

            <div className="pt-4 flex justify-end">
              <button
                type="button"
                onClick={() => setCurrentStep(2)}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2"
              >
                Continue to Academic Info <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {/* Step 2: Academic Background */}
        {currentStep === 2 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="space-y-1">
              <h2 className="text-xl font-extrabold text-slate-900">Academic & Educational Details</h2>
              <p className="text-xs text-slate-500">Tell us about your educational background and graduation timeline.</p>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Degree</label>
                <input
                  type="text"
                  value={degree}
                  onChange={(e) => setDegree(e.target.value)}
                  placeholder="e.g. B.Tech / B.E. / B.S. / M.S."
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Branch / Major</label>
                <input
                  type="text"
                  value={branch}
                  onChange={(e) => setBranch(e.target.value)}
                  placeholder="e.g. Computer Science, Information Technology, AI"
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">College / University Name</label>
                <div className="relative">
                  <GraduationCap className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    value={college}
                    onChange={(e) => setCollege(e.target.value)}
                    placeholder="e.g. National Institute of Technology"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Graduation Year</label>
                <input
                  type="number"
                  min={2000}
                  max={2035}
                  value={graduationYear}
                  onChange={(e) => setGraduationYear(Number(e.target.value))}
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                />
              </div>

              <div className="sm:col-span-2">
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Current Status</label>
                <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
                  {['Final Year Student', 'Pre-Final Year', 'Recent Graduate', 'Working Professional'].map((status) => (
                    <button
                      key={status}
                      type="button"
                      onClick={() => setAcademicStatus(status)}
                      className={`p-3 rounded-xl border text-center text-xs font-bold transition-all ${
                        academicStatus === status
                          ? 'border-emerald-600 bg-emerald-50 text-emerald-800 ring-2 ring-emerald-500/20'
                          : 'border-slate-200 text-slate-600 hover:border-slate-300'
                      }`}
                    >
                      {status}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            <div className="pt-4 flex justify-between">
              <button
                type="button"
                onClick={() => setCurrentStep(1)}
                className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-bold flex items-center gap-1.5"
              >
                <ArrowLeft className="w-3.5 h-3.5" /> Back
              </button>
              <button
                type="button"
                onClick={() => setCurrentStep(3)}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2"
              >
                Continue to Career Target <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {/* Step 3: Target Role & Industry */}
        {currentStep === 3 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="space-y-1">
              <h2 className="text-xl font-extrabold text-slate-900">Target Role & Industry Preferences</h2>
              <p className="text-xs text-slate-500">Define what roles you want our AI to benchmark your skills against.</p>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Target Job Role *</label>
                <div className="relative">
                  <Briefcase className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <input
                    type="text"
                    required
                    value={targetRole}
                    onChange={(e) => setTargetRole(e.target.value)}
                    placeholder="e.g. Java Backend Engineer, React Frontend Developer"
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Preferred Industry</label>
                <div className="relative">
                  <Building2 className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
                  <select
                    value={preferredIndustry}
                    onChange={(e) => setPreferredIndustry(e.target.value)}
                    className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium bg-white"
                  >
                    <option value="Software & Internet / SaaS">Software & Internet / SaaS</option>
                    <option value="Fintech & Banking">Fintech & Banking</option>
                    <option value="Healthtech & Biotech">Healthtech & Biotech</option>
                    <option value="AI & Machine Learning">AI & Machine Learning</option>
                    <option value="E-commerce & Retail">E-commerce & Retail</option>
                    <option value="Cybersecurity">Cybersecurity</option>
                    <option value="Cloud Infrastructure & DevOps">Cloud Infrastructure & DevOps</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Years of Relevant Experience</label>
                <input
                  type="number"
                  min={0}
                  max={30}
                  value={yearsOfExperience}
                  onChange={(e) => setYearsOfExperience(Number(e.target.value))}
                  className="w-full px-4 py-2.5 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-sm outline-hidden font-medium"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-slate-700 uppercase mb-1.5">Professional Bio & Career Objective</label>
                <textarea
                  rows={3}
                  value={bio}
                  onChange={(e) => setBio(e.target.value)}
                  placeholder="Summarize your strengths and what kind of technical impact you want to make..."
                  className="w-full p-3 rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 text-xs outline-hidden leading-relaxed"
                />
              </div>
            </div>

            <div className="pt-4 flex justify-between">
              <button
                type="button"
                onClick={() => setCurrentStep(2)}
                className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-bold flex items-center gap-1.5"
              >
                <ArrowLeft className="w-3.5 h-3.5" /> Back
              </button>
              <button
                type="button"
                onClick={() => setCurrentStep(4)}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2"
              >
                Continue to Skills <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {/* Step 4: Skills & Competencies */}
        {currentStep === 4 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="space-y-1">
              <h2 className="text-xl font-extrabold text-slate-900">Technical & Soft Skills</h2>
              <p className="text-xs text-slate-500">Add the skills you already possess along with your proficiency level.</p>
            </div>

            {/* Technical Skills */}
            <div className="space-y-3">
              <label className="block text-xs font-bold text-slate-700 uppercase">Technical Skills</label>
              
              <div className="flex flex-col sm:flex-row gap-2">
                <input
                  type="text"
                  placeholder="e.g. Docker, TypeScript, Microservices..."
                  value={newSkillName}
                  onChange={(e) => setNewSkillName(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addTechnicalSkill(); } }}
                  className="flex-grow px-3 py-2 text-xs rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 outline-hidden font-medium"
                />
                <select
                  value={newSkillLevel}
                  onChange={(e) => setNewSkillLevel(e.target.value as any)}
                  className="px-3 py-2 text-xs rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 outline-hidden font-medium bg-white"
                >
                  <option value="Beginner">Beginner</option>
                  <option value="Intermediate">Intermediate</option>
                  <option value="Advanced">Advanced</option>
                  <option value="Expert">Expert</option>
                </select>
                <button
                  type="button"
                  onClick={addTechnicalSkill}
                  className="px-4 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold flex items-center justify-center gap-1.5"
                >
                  <Plus className="w-3.5 h-3.5" /> Add Skill
                </button>
              </div>

              <div className="flex flex-wrap gap-2 pt-2">
                {technicalSkills.map((skill, idx) => (
                  <span
                    key={idx}
                    className="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-emerald-50 text-emerald-900 text-xs font-bold border border-emerald-200"
                  >
                    <span>{skill.skillName}</span>
                    <span className="text-[10px] font-semibold text-emerald-600 bg-white px-1.5 py-0.5 rounded-md border border-emerald-100">
                      {skill.proficiencyLevel}
                    </span>
                    <button
                      type="button"
                      onClick={() => removeTechnicalSkill(idx)}
                      className="text-emerald-500 hover:text-rose-600 transition-colors"
                    >
                      <Trash2 className="w-3 h-3" />
                    </button>
                  </span>
                ))}
              </div>
            </div>

            {/* Soft Skills */}
            <div className="space-y-3 pt-4 border-t border-slate-100">
              <label className="block text-xs font-bold text-slate-700 uppercase">Soft Skills & Competencies</label>
              
              <div className="flex gap-2">
                <input
                  type="text"
                  placeholder="e.g. Critical Thinking, Conflict Resolution..."
                  value={newSoftSkill}
                  onChange={(e) => setNewSoftSkill(e.target.value)}
                  onKeyDown={(e) => { if (e.key === 'Enter') { e.preventDefault(); addSoftSkill(); } }}
                  className="flex-grow px-3 py-2 text-xs rounded-xl border border-slate-300 focus:ring-2 focus:ring-emerald-500 outline-hidden font-medium"
                />
                <button
                  type="button"
                  onClick={addSoftSkill}
                  className="px-4 py-2 rounded-xl bg-slate-900 hover:bg-slate-800 text-white text-xs font-bold flex items-center gap-1.5"
                >
                  <Plus className="w-3.5 h-3.5" /> Add
                </button>
              </div>

              <div className="flex flex-wrap gap-2 pt-1">
                {softSkills.map((ss, idx) => (
                  <span
                    key={idx}
                    className="inline-flex items-center gap-1.5 px-3 py-1 rounded-xl bg-slate-100 text-slate-700 text-xs font-semibold"
                  >
                    {ss}
                    <button
                      type="button"
                      onClick={() => removeSoftSkill(idx)}
                      className="text-slate-400 hover:text-rose-600 transition-colors"
                    >
                      <Trash2 className="w-3 h-3" />
                    </button>
                  </span>
                ))}
              </div>
            </div>

            <div className="pt-4 flex justify-between">
              <button
                type="button"
                onClick={() => setCurrentStep(3)}
                className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-bold flex items-center gap-1.5"
              >
                <ArrowLeft className="w-3.5 h-3.5" /> Back
              </button>
              <button
                type="button"
                onClick={() => setCurrentStep(5)}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2"
              >
                Continue to Projects <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {/* Step 5: Projects & Portfolio */}
        {currentStep === 5 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <h2 className="text-xl font-extrabold text-slate-900">Key Projects & Portfolio</h2>
                <p className="text-xs text-slate-500">Showcase your technical projects, repositories, and architecture.</p>
              </div>
              <button
                type="button"
                onClick={addProject}
                className="px-3 py-1.5 rounded-xl bg-emerald-50 text-emerald-700 hover:bg-emerald-100 text-xs font-bold flex items-center gap-1.5"
              >
                <Plus className="w-3.5 h-3.5" /> Add Project
              </button>
            </div>

            <div className="space-y-4">
              {projects.map((proj, idx) => (
                <div key={idx} className="p-5 rounded-2xl bg-slate-50 border border-slate-200 space-y-3">
                  <div className="flex items-center justify-between">
                    <div className="font-bold text-xs text-slate-600 uppercase">Project #{idx + 1}</div>
                    {projects.length > 1 && (
                      <button
                        type="button"
                        onClick={() => removeProject(idx)}
                        className="text-slate-400 hover:text-rose-600 p-1"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    )}
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <input
                      type="text"
                      placeholder="Project Title"
                      value={proj.title}
                      onChange={(e) => {
                        const updated = [...projects];
                        updated[idx].title = e.target.value;
                        setProjects(updated);
                      }}
                      className="p-2.5 text-xs rounded-xl border border-slate-300 bg-white font-bold"
                    />
                    <input
                      type="text"
                      placeholder="Technologies (e.g. React, Spring Boot)"
                      value={proj.technologies}
                      onChange={(e) => {
                        const updated = [...projects];
                        updated[idx].technologies = e.target.value;
                        setProjects(updated);
                      }}
                      className="p-2.5 text-xs rounded-xl border border-slate-300 bg-white font-medium"
                    />
                  </div>

                  <textarea
                    rows={2}
                    placeholder="Short description of technical architecture and personal contributions..."
                    value={proj.description}
                    onChange={(e) => {
                      const updated = [...projects];
                      updated[idx].description = e.target.value;
                      setProjects(updated);
                    }}
                    className="w-full p-2.5 text-xs rounded-xl border border-slate-300 bg-white"
                  />

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                    <input
                      type="text"
                      placeholder="GitHub Link (https://github.com/...)"
                      value={proj.githubUrl || ''}
                      onChange={(e) => {
                        const updated = [...projects];
                        updated[idx].githubUrl = e.target.value;
                        setProjects(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white text-slate-600"
                    />
                    <input
                      type="text"
                      placeholder="Live Demo URL (optional)"
                      value={proj.liveUrl || ''}
                      onChange={(e) => {
                        const updated = [...projects];
                        updated[idx].liveUrl = e.target.value;
                        setProjects(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white text-slate-600"
                    />
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-4 flex justify-between">
              <button
                type="button"
                onClick={() => setCurrentStep(4)}
                className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-bold flex items-center gap-1.5"
              >
                <ArrowLeft className="w-3.5 h-3.5" /> Back
              </button>
              <button
                type="button"
                onClick={() => setCurrentStep(6)}
                className="px-6 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-bold text-xs shadow-md shadow-emerald-600/30 flex items-center gap-2"
              >
                Continue to Certifications & Experience <ArrowRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        )}

        {/* Step 6: Certifications & Experience (Final Review) */}
        {currentStep === 6 && (
          <div className="bg-white p-6 sm:p-10 rounded-3xl border border-slate-200 shadow-sm space-y-6">
            <div className="space-y-1">
              <h2 className="text-xl font-extrabold text-slate-900">Certifications & Work Experience</h2>
              <p className="text-xs text-slate-500">Add any credentials or past internships to validate your skills.</p>
            </div>

            {/* Certifications Section */}
            <div className="space-y-3">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-slate-700 uppercase flex items-center gap-1.5">
                  <Award className="w-4 h-4 text-emerald-600" /> Certifications & Licenses
                </label>
                <button
                  type="button"
                  onClick={addCertification}
                  className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" /> Add
                </button>
              </div>

              {certifications.map((cert, idx) => (
                <div key={idx} className="p-3.5 rounded-2xl bg-slate-50 border border-slate-200 space-y-2">
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                    <input
                      type="text"
                      placeholder="Certification Name"
                      value={cert.name}
                      onChange={(e) => {
                        const updated = [...certifications];
                        updated[idx].name = e.target.value;
                        setCertifications(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white font-bold"
                    />
                    <input
                      type="text"
                      placeholder="Issuer (e.g. AWS, Oracle)"
                      value={cert.issuer}
                      onChange={(e) => {
                        const updated = [...certifications];
                        updated[idx].issuer = e.target.value;
                        setCertifications(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white font-medium"
                    />
                    <div className="flex items-center gap-2">
                      <input
                        type="number"
                        placeholder="Year"
                        value={cert.issueYear}
                        onChange={(e) => {
                          const updated = [...certifications];
                          updated[idx].issueYear = Number(e.target.value);
                          setCertifications(updated);
                        }}
                        className="w-24 p-2 text-xs rounded-xl border border-slate-300 bg-white"
                      />
                      {certifications.length > 1 && (
                        <button
                          type="button"
                          onClick={() => removeCertification(idx)}
                          className="text-slate-400 hover:text-rose-600 p-1"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Experience Section */}
            <div className="space-y-3 pt-4 border-t border-slate-100">
              <div className="flex items-center justify-between">
                <label className="text-xs font-bold text-slate-700 uppercase flex items-center gap-1.5">
                  <Briefcase className="w-4 h-4 text-emerald-600" /> Work & Internship Experience
                </label>
                <button
                  type="button"
                  onClick={addExperience}
                  className="text-xs font-bold text-emerald-600 hover:text-emerald-700 flex items-center gap-1"
                >
                  <Plus className="w-3.5 h-3.5" /> Add
                </button>
              </div>

              {experiences.map((exp, idx) => (
                <div key={idx} className="p-4 rounded-2xl bg-slate-50 border border-slate-200 space-y-2">
                  <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                    <input
                      type="text"
                      placeholder="Role / Title"
                      value={exp.title}
                      onChange={(e) => {
                        const updated = [...experiences];
                        updated[idx].title = e.target.value;
                        setExperiences(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white font-bold"
                    />
                    <input
                      type="text"
                      placeholder="Company"
                      value={exp.company}
                      onChange={(e) => {
                        const updated = [...experiences];
                        updated[idx].company = e.target.value;
                        setExperiences(updated);
                      }}
                      className="p-2 text-xs rounded-xl border border-slate-300 bg-white font-medium"
                    />
                    <div className="flex items-center gap-2">
                      <input
                        type="text"
                        placeholder="Duration"
                        value={exp.duration}
                        onChange={(e) => {
                          const updated = [...experiences];
                          updated[idx].duration = e.target.value;
                          setExperiences(updated);
                        }}
                        className="w-full p-2 text-xs rounded-xl border border-slate-300 bg-white"
                      />
                      {experiences.length > 1 && (
                        <button
                          type="button"
                          onClick={() => removeExperience(idx)}
                          className="text-slate-400 hover:text-rose-600 p-1"
                        >
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </div>
                  </div>
                  <textarea
                    rows={2}
                    placeholder="Brief achievements and tasks..."
                    value={exp.description}
                    onChange={(e) => {
                      const updated = [...experiences];
                      updated[idx].description = e.target.value;
                      setExperiences(updated);
                    }}
                    className="w-full p-2 text-xs rounded-xl border border-slate-300 bg-white"
                  />
                </div>
              ))}
            </div>

            {/* Completion Summary Card */}
            <div className="p-5 rounded-2xl bg-gradient-to-r from-emerald-50 to-teal-50 border border-emerald-200 text-xs space-y-1.5 text-emerald-900">
              <div className="font-extrabold text-sm flex items-center gap-1.5 text-emerald-800">
                <Sparkles className="w-4 h-4" /> Ready to complete onboarding!
              </div>
              <p className="text-emerald-700">
                You will be able to edit any of this information anytime in <strong>My Profile</strong>.
              </p>
            </div>

            <div className="pt-4 flex justify-between">
              <button
                type="button"
                onClick={() => setCurrentStep(5)}
                className="px-4 py-2 rounded-xl text-slate-600 hover:bg-slate-100 text-xs font-bold flex items-center gap-1.5"
              >
                <ArrowLeft className="w-3.5 h-3.5" /> Back
              </button>
              <button
                type="button"
                onClick={handleFinish}
                disabled={loading}
                className="px-8 py-3 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-extrabold text-xs shadow-lg shadow-emerald-600/30 flex items-center gap-2 transition-all hover:scale-105 disabled:opacity-50"
              >
                {loading ? (
                  <>
                    <Loader2 className="w-4 h-4 animate-spin" /> Saving Profile...
                  </>
                ) : (
                  <>
                    Complete & Enter Dashboard <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </div>
          </div>
        )}

      </div>
    </div>
  );
};
