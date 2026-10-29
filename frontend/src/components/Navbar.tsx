import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Compass, Menu, X, User as UserIcon, LogOut, LayoutDashboard, BrainCircuit, BookOpen, FileText } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-md border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo & Tagline */}
          <Link to="/" className="flex items-center gap-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-emerald-600 to-teal-500 flex items-center justify-center text-white shadow-md shadow-emerald-500/20 group-hover:scale-105 transition-transform">
              <Compass className="w-5 h-5" />
            </div>
            <div>
              <span className="font-extrabold text-xl tracking-tight text-slate-900 group-hover:text-emerald-600 transition-colors">
                CareerGap<span className="text-emerald-600"> AI</span>
              </span>
              <span className="hidden sm:block text-[10px] uppercase font-semibold tracking-wider text-slate-400">
                Become Job-Ready
              </span>
            </div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex items-center gap-6 text-sm font-medium">
            {isAuthenticated ? (
              <>
                <Link to="/dashboard" className="text-slate-600 hover:text-emerald-600 transition-colors flex items-center gap-1.5">
                  <LayoutDashboard className="w-4 h-4" /> Dashboard
                </Link>
                <Link to="/analyzer" className="text-slate-600 hover:text-emerald-600 transition-colors flex items-center gap-1.5">
                  <BrainCircuit className="w-4 h-4" /> Skill Analyzer
                </Link>
                <Link to="/roadmap" className="text-slate-600 hover:text-emerald-600 transition-colors flex items-center gap-1.5">
                  <BookOpen className="w-4 h-4" /> Roadmap
                </Link>
                <Link to="/resume-builder" className="text-slate-600 hover:text-emerald-600 transition-colors flex items-center gap-1.5">
                  <FileText className="w-4 h-4" /> Resume Builder
                </Link>
              </>
            ) : (
              <>
                <Link to="/" className="text-slate-600 hover:text-emerald-600 transition-colors">Home</Link>
                <a href="#how-it-works" className="text-slate-600 hover:text-emerald-600 transition-colors">How it works</a>
                <a href="#features" className="text-slate-600 hover:text-emerald-600 transition-colors">Features</a>
              </>
            )}
          </nav>

          {/* Auth Action Buttons */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <div className="flex items-center gap-3">
                <Link
                  to="/profile"
                  className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-100 text-slate-700 hover:bg-slate-200 text-sm font-medium transition-colors"
                >
                  <UserIcon className="w-4 h-4 text-emerald-600" />
                  <span className="max-w-[120px] truncate">{user?.fullName?.split(' ')[0] || 'Profile'}</span>
                </Link>
                <button
                  onClick={handleLogout}
                  className="p-2 rounded-lg text-slate-500 hover:text-rose-600 hover:bg-rose-50 transition-colors"
                  title="Sign Out"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-3">
                <Link
                  to="/login"
                  className="px-4 py-2 text-sm font-semibold text-slate-700 hover:text-emerald-600 transition-colors"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-2 text-sm font-semibold text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg shadow-sm shadow-emerald-600/30 transition-all hover:shadow-md"
                >
                  Get Started Free
                </Link>
              </div>
            )}
          </div>

          {/* Mobile menu hamburger */}
          <div className="flex md:hidden items-center">
            <button
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              className="p-2 rounded-lg text-slate-600 hover:text-slate-900 hover:bg-slate-100"
              aria-label="Toggle menu"
            >
              {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer */}
      {mobileMenuOpen && (
        <div className="md:hidden border-b border-slate-200 bg-white px-4 pt-2 pb-6 space-y-3">
          {isAuthenticated ? (
            <>
              <div className="py-2 border-b border-slate-100 flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-emerald-100 text-emerald-700 font-bold flex items-center justify-center text-sm">
                  {user?.fullName?.charAt(0).toUpperCase()}
                </div>
                <div>
                  <div className="font-semibold text-slate-800 text-sm">{user?.fullName}</div>
                  <div className="text-xs text-slate-500">{user?.email}</div>
                </div>
              </div>
              <Link to="/dashboard" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Dashboard</Link>
              <Link to="/analyzer" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Skill Analyzer</Link>
              <Link to="/roadmap" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Learning Roadmap</Link>
              <Link to="/resume-builder" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Resume Builder</Link>
              <Link to="/profile" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">My Profile</Link>
              <Link to="/settings" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Settings</Link>
              <button
                onClick={() => { handleLogout(); setMobileMenuOpen(false); }}
                className="w-full text-left py-2 text-rose-600 font-semibold"
              >
                Log Out
              </button>
            </>
          ) : (
            <>
              <Link to="/" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Home</Link>
              <Link to="/login" onClick={() => setMobileMenuOpen(false)} className="block py-2 text-slate-700 font-medium">Sign In</Link>
              <Link
                to="/register"
                onClick={() => setMobileMenuOpen(false)}
                className="block text-center w-full py-2.5 px-4 bg-emerald-600 text-white font-semibold rounded-lg shadow-sm"
              >
                Get Started
              </Link>
            </>
          )}
        </div>
      )}
    </header>
  );
};
