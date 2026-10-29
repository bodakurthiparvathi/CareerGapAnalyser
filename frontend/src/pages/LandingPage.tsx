import React from 'react';
import { Link } from 'react-router-dom';
import {
  Compass,
  ArrowRight,
  BrainCircuit,
  FileCheck2,
  GitFork,
  Target,
  Sparkles,
  Zap,
  CheckCircle2,
  TrendingUp,
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Hero Section */}
      <section className="relative overflow-hidden pt-12 pb-24 md:pt-20 md:pb-32 bg-gradient-to-b from-emerald-50/50 via-white to-white">
        <div className="absolute inset-0 bg-[radial-gradient(#e2e8f0_1px,transparent_1px)] [background-size:16px_16px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)] opacity-40 pointer-events-none" />

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
          <div className="text-center max-w-3xl mx-auto space-y-6">
            {/* Pill tag */}
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-emerald-100 text-emerald-800 text-xs font-bold border border-emerald-200 shadow-xs">
              <Sparkles className="w-3.5 h-3.5 text-emerald-600" />
              <span>Full-Stack AI Career Development Engine</span>
            </div>

            {/* Main Headline */}
            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-slate-900 tracking-tight leading-[1.15]">
              Know what you're missing.{' '}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-emerald-600 to-teal-600">
                Learn what matters.
              </span>{' '}
              Become job-ready.
            </h1>

            {/* Subheading */}
            <p className="text-lg sm:text-xl text-slate-600 leading-relaxed font-normal">
              Compare your resume against real target job descriptions. Uncover exact skill gaps, compute job readiness, and follow an actionable step-by-step learning roadmap to land your dream role.
            </p>

            {/* Action buttons */}
            <div className="pt-4 flex flex-col sm:flex-row items-center justify-center gap-4">
              <Link
                to="/register"
                className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-base shadow-lg shadow-emerald-600/25 flex items-center justify-center gap-2 transition-all hover:scale-[1.02]"
              >
                Start Free Analysis <ArrowRight className="w-4 h-4" />
              </Link>
              <Link
                to="/login"
                className="w-full sm:w-auto px-8 py-3.5 rounded-xl bg-white hover:bg-slate-50 text-slate-700 font-semibold text-base border border-slate-200 shadow-xs flex items-center justify-center transition-all"
              >
                Sign In to Account
              </Link>
            </div>

            {/* Stats preview */}
            <div className="pt-10 grid grid-cols-2 md:grid-cols-4 gap-4 max-w-4xl mx-auto">
              {[
                { label: 'PDF & DOCX Support', value: '100% Native' },
                { label: 'Readiness Index', value: '0 - 100% Score' },
                { label: 'Target Skill Gaps', value: 'Instant Mapping' },
                { label: 'Dynamic Roadmap', value: 'Phased Weeks' },
              ].map((stat, i) => (
                <div key={i} className="p-4 rounded-xl bg-white border border-slate-100 shadow-xs text-left">
                  <div className="text-lg sm:text-xl font-bold text-slate-900">{stat.value}</div>
                  <div className="text-xs text-slate-500 font-medium">{stat.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* How it works Section */}
      <section id="how-it-works" className="py-20 bg-slate-50 border-y border-slate-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-16 space-y-3">
            <h2 className="text-xs font-bold text-emerald-600 uppercase tracking-widest">Process</h2>
            <p className="text-3xl font-extrabold text-slate-900">How CareerGap AI Works</p>
            <p className="text-slate-600 text-sm">
              From raw resume to personalized job readiness in four simple, intelligent steps.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            {[
              {
                step: '01',
                title: 'Upload Resume',
                desc: 'Upload your current resume in PDF or DOCX format or paste your raw text.',
                icon: FileCheck2,
              },
              {
                step: '02',
                title: 'Paste Job Spec',
                desc: 'Provide the job description for the role you are targeting at your dream company.',
                icon: Target,
              },
              {
                step: '03',
                title: 'Analyze Gaps',
                desc: 'Our semantic analyzer cross-references keywords, missing technologies, and competencies.',
                icon: BrainCircuit,
              },
              {
                step: '04',
                title: 'Follow Roadmap',
                desc: 'Receive a structured, phased curriculum with milestones and recommended resources.',
                icon: GitFork,
              },
            ].map((item, idx) => {
              const Icon = item.icon;
              return (
                <div
                  key={idx}
                  className="relative p-6 rounded-2xl bg-white border border-slate-200 shadow-xs hover:shadow-md transition-shadow"
                >
                  <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center mb-4">
                    <Icon className="w-6 h-6" />
                  </div>
                  <div className="text-xs font-bold text-emerald-600 mb-1">Step {item.step}</div>
                  <h3 className="text-lg font-bold text-slate-900 mb-2">{item.title}</h3>
                  <p className="text-sm text-slate-600 leading-relaxed">{item.desc}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section id="features" className="py-20 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center max-w-2xl mx-auto mb-16 space-y-3">
            <h2 className="text-xs font-bold text-emerald-600 uppercase tracking-widest">Platform Capabilities</h2>
            <p className="text-3xl font-extrabold text-slate-900">Engineered for Real Career Outcomes</p>
            <p className="text-slate-600 text-sm">
              Designed specifically to take the guesswork out of job hunting and technical preparation.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="p-8 rounded-2xl border border-slate-200 bg-white shadow-xs space-y-4">
              <div className="w-10 h-10 rounded-lg bg-teal-50 text-teal-600 flex items-center justify-center">
                <TrendingUp className="w-5 h-5" />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Accurate Match Score</h3>
              <p className="text-slate-600 text-sm leading-relaxed">
                Receive granular technical match percentages, soft skill alignments, and an overall readiness classification.
              </p>
              <ul className="space-y-2 text-xs text-slate-500 font-medium">
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-500" /> Technical skills separation</li>
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-500" /> Missing competency alerts</li>
              </ul>
            </div>

            <div className="p-8 rounded-2xl border border-emerald-200 bg-emerald-50/30 shadow-xs space-y-4 relative">
              <div className="absolute top-4 right-4 bg-emerald-600 text-white text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider">
                Featured
              </div>
              <div className="w-10 h-10 rounded-lg bg-emerald-100 text-emerald-700 flex items-center justify-center">
                <BrainCircuit className="w-5 h-5" />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Personalized Roadmap</h3>
              <p className="text-slate-600 text-sm leading-relaxed">
                Get an organized week-by-week learning plan built around your exact missing competencies with milestone project ideas.
              </p>
              <ul className="space-y-2 text-xs text-slate-600 font-medium">
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Focus areas & targeted topics</li>
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-600" /> Curated learning resources</li>
              </ul>
            </div>

            <div className="p-8 rounded-2xl border border-slate-200 bg-white shadow-xs space-y-4">
              <div className="w-10 h-10 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
                <Zap className="w-5 h-5" />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Resume Builder & Optimizer</h3>
              <p className="text-slate-600 text-sm leading-relaxed">
                Draft modern, ATS-friendly resume sections designed to highlight matching keywords and strengths.
              </p>
              <ul className="space-y-2 text-xs text-slate-500 font-medium">
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-500" /> Pre-formatted career templates</li>
                <li className="flex items-center gap-2"><CheckCircle2 className="w-4 h-4 text-emerald-500" /> Instant PDF download ready</li>
              </ul>
            </div>
          </div>
        </div>
      </section>

      {/* Call to Action Banner */}
      <section className="py-16 bg-gradient-to-tr from-slate-900 via-slate-800 to-emerald-950 text-white">
        <div className="max-w-5xl mx-auto px-4 text-center space-y-6">
          <div className="w-12 h-12 mx-auto rounded-2xl bg-emerald-500/20 text-emerald-400 flex items-center justify-center border border-emerald-500/30">
            <Compass className="w-6 h-6" />
          </div>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight">
            Stop guessing what recruiters want.
          </h2>
          <p className="text-slate-300 text-base max-w-xl mx-auto leading-relaxed">
            Join CareerGap AI today to analyze your skills, pinpoint gaps, and start your tailored career trajectory.
          </p>
          <div className="pt-2">
            <Link
              to="/register"
              className="inline-flex items-center gap-2 px-8 py-3.5 rounded-xl bg-emerald-500 hover:bg-emerald-600 text-white font-bold text-sm shadow-lg shadow-emerald-500/20 transition-all hover:scale-105"
            >
              Get Started Now — Free <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};
