import React, { useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {
  ArrowLeft,
  ArrowRight,
  BookOpen,
  CheckCircle2,
  Clock,
  ExternalLink,
  GraduationCap,
  Loader2,
  Play,
  Target,
} from 'lucide-react';
import { learningService } from '../services/learningService';
import {
  LearningDashboardData,
  LearningModule,
  PracticeEvaluationResult,
} from '../types';

const STUDY_TIME_OPTIONS = [15, 30, 45, 60, 90, 120, 180, 240, 480];

const getErrorMessage = (error: unknown) =>
  error instanceof Error ? error.message : 'Something went wrong. Please try again.';

const parseEstimatedMinutes = (hours: string) => {
  const values = hours.match(/\d+(?:\.\d+)?/g)?.map(Number) ?? [];
  if (values.length === 0) return 240;
  const averageHours = values.reduce((sum, value) => sum + value, 0) / values.length;
  return Math.max(30, Math.round(averageHours * 60));
};

const renderExplanation = (content: string) =>
  content.split(/(```[\s\S]*?```)/g).filter(Boolean).map((part, index) => {
    if (part.startsWith('```')) {
      const code = part.replace(/^```[^\n]*\n?/, '').replace(/```$/, '').trim();
      return (
        <pre key={index} className="overflow-x-auto rounded-xl bg-slate-950 p-4 text-sm text-emerald-300">
          <code>{code}</code>
        </pre>
      );
    }
    return part.split(/\n\s*\n/).filter(Boolean).map((paragraph, paragraphIndex) => (
      <p key={`${index}-${paragraphIndex}`} className="whitespace-pre-line leading-7 text-slate-600">
        {paragraph.replace(/^#{1,6}\s*/, '')}
      </p>
    ));
  });

const statusLabel = (status: LearningModule['status']) =>
  status === 'IN_PROGRESS' ? 'In Progress' : status === 'COMPLETED' ? 'Completed' : 'Not Started';

const statusStyle = (status: LearningModule['status']) =>
  status === 'COMPLETED'
    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
    : status === 'IN_PROGRESS'
      ? 'bg-amber-50 text-amber-700 border-amber-200'
      : 'bg-slate-100 text-slate-600 border-slate-200';

export const LearningRoadmapPage: React.FC = () => {
  const { moduleId } = useParams<{ moduleId: string }>();
  const navigate = useNavigate();
  const [dashboard, setDashboard] = useState<LearningDashboardData | null>(null);
  const [selectedModule, setSelectedModule] = useState<LearningModule | null>(null);
  const [dailyMinutes, setDailyMinutes] = useState(60);
  const [loading, setLoading] = useState(true);
  const [savingPreference, setSavingPreference] = useState(false);
  const [updatingProgress, setUpdatingProgress] = useState(false);
  const [submittingQuestion, setSubmittingQuestion] = useState<number | null>(null);
  const [answers, setAnswers] = useState<Record<number, string>>({});
  const [evaluations, setEvaluations] = useState<Record<number, PracticeEvaluationResult>>({});
  const [completedDays, setCompletedDays] = useState<Record<number, boolean>>({});
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    const load = async () => {
      setLoading(true);
      setError('');
      setSelectedModule(null);
      setAnswers({});
      setEvaluations({});
      try {
        const data = await learningService.getDashboard();
        if (!active) return;
        setDashboard(data);
        setDailyMinutes(data.dailyStudyMinutes || 60);
        if (moduleId) {
          const id = Number(moduleId);
          if (!Number.isInteger(id) || id <= 0) {
            setError('That learning module could not be found.');
          } else {
            const module = await learningService.getModuleById(id);
            if (active) setSelectedModule(module);
          }
        }
      } catch (loadError) {
        if (active) setError(getErrorMessage(loadError));
      } finally {
        if (active) setLoading(false);
      }
    };
    load();
    return () => {
      active = false;
    };
  }, [moduleId]);

  const planDays = useMemo(() => {
    if (!selectedModule) return [];
    const totalMinutes = parseEstimatedMinutes(selectedModule.estimatedHours);
    const dayCount = Math.max(1, Math.ceil(totalMinutes / dailyMinutes));
    const objectives = selectedModule.learningObjectives.length
      ? selectedModule.learningObjectives
      : [`Study the core concepts of ${selectedModule.skillName}.`];
    return Array.from({ length: dayCount }, (_, index) => ({
      day: index + 1,
      minutes: Math.min(dailyMinutes, totalMinutes - index * dailyMinutes),
      topic: objectives[index % objectives.length],
      exercise: selectedModule.practiceExercises[index % Math.max(selectedModule.practiceExercises.length, 1)],
    }));
  }, [selectedModule, dailyMinutes]);

  const refreshDashboard = async () => {
    const data = await learningService.getDashboard();
    setDashboard(data);
    setDailyMinutes(data.dailyStudyMinutes || dailyMinutes);
  };

  const saveStudyTime = async () => {
    if (!dashboard?.roadmapId) return;
    setSavingPreference(true);
    setError('');
    try {
      const data = await learningService.updateDailyStudyTime(dashboard.roadmapId, dailyMinutes);
      setDashboard(data);
    } catch (saveError) {
      setError(getErrorMessage(saveError));
    } finally {
      setSavingPreference(false);
    }
  };

  const updateStatus = async (status: LearningModule['status']) => {
    if (!selectedModule) return;
    setUpdatingProgress(true);
    setError('');
    try {
      const updated = await learningService.updateProgress(selectedModule.id, { status });
      setSelectedModule(updated);
      await refreshDashboard();
    } catch (progressError) {
      setError(getErrorMessage(progressError));
    } finally {
      setUpdatingProgress(false);
    }
  };

  const submitPractice = async (questionIndex: number) => {
    if (!selectedModule) return;
    const question = selectedModule.practiceQuestions[questionIndex];
    const answer = answers[questionIndex]?.trim();
    if (!answer) {
      setError('Enter an answer before submitting the practice question.');
      return;
    }
    setSubmittingQuestion(questionIndex);
    setError('');
    try {
      const result = await learningService.evaluatePractice(selectedModule.id, {
        questionIndex,
        questionType: question.type,
        userAnswer: answer,
      });
      setEvaluations((current) => ({ ...current, [questionIndex]: result }));
      if (result.updatedModuleStatus === 'IN_PROGRESS') {
        setSelectedModule((current) => current ? { ...current, status: 'IN_PROGRESS' } : current);
        await refreshDashboard();
      }
    } catch (practiceError) {
      setError(getErrorMessage(practiceError));
    } finally {
      setSubmittingQuestion(null);
    }
  };

  const openModule = (module: LearningModule) => navigate(`/roadmap/${module.id}`);

  const renderStatusButton = (module: LearningModule) => (
    <button
      type="button"
      onClick={(event) => {
        event.stopPropagation();
        openModule(module);
      }}
      className="inline-flex items-center gap-2 rounded-lg bg-emerald-600 px-4 py-2.5 text-sm font-bold text-white transition hover:bg-emerald-700"
    >
      {module.status === 'NOT_STARTED' ? 'Start Learning' : module.status === 'COMPLETED' ? 'Review Learning' : 'Continue Learning'}
      <ArrowRight className="h-4 w-4" />
    </button>
  );

  if (loading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center gap-3 text-slate-600">
        <Loader2 className="h-5 w-5 animate-spin text-emerald-600" />
        Loading your personalized learning plan...
      </div>
    );
  }

  if (selectedModule) {
    const resources = selectedModule.suitableResources ?? [];
    return (
      <div className="space-y-6">
        <button
          type="button"
          onClick={() => navigate('/roadmap')}
          className="inline-flex items-center gap-2 text-sm font-semibold text-slate-600 hover:text-emerald-700"
        >
          <ArrowLeft className="h-4 w-4" /> Back to roadmap
        </button>

        {error && <div role="alert" className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}</div>}

        <section className="rounded-2xl bg-slate-900 p-6 text-white shadow-lg sm:p-8">
          <div className="flex flex-wrap items-center gap-2">
            <span className="rounded-full bg-emerald-500/20 px-3 py-1 text-xs font-bold text-emerald-300">{selectedModule.category}</span>
            <span className={`rounded-full border px-3 py-1 text-xs font-bold ${statusStyle(selectedModule.status)}`}>
              {statusLabel(selectedModule.status)}
            </span>
          </div>
          <h1 className="mt-4 text-3xl font-black">{selectedModule.skillName}</h1>
          <p className="mt-3 max-w-3xl text-sm leading-6 text-slate-300">{selectedModule.whyItMatters}</p>
          <div className="mt-6 flex flex-wrap gap-x-6 gap-y-3 text-sm text-slate-300">
            <span className="inline-flex items-center gap-2"><Clock className="h-4 w-4" />{selectedModule.estimatedHours}</span>
            <span className="inline-flex items-center gap-2"><Target className="h-4 w-4" />{selectedModule.currentProficiency} → {selectedModule.targetProficiency}</span>
            <span>Role: {dashboard?.targetRole || 'Your target role'}</span>
          </div>
          <div className="mt-6 flex flex-wrap gap-3">
            {selectedModule.status !== 'COMPLETED' && (
              <button
                type="button"
                disabled={updatingProgress}
                onClick={() => updateStatus('IN_PROGRESS')}
                className="inline-flex items-center gap-2 rounded-xl bg-emerald-500 px-5 py-3 font-bold text-slate-950 transition hover:bg-emerald-400 disabled:opacity-60"
              >
                <Play className="h-4 w-4 fill-current" />
                {selectedModule.status === 'IN_PROGRESS' ? 'Continue Learning' : 'Start Learning'}
              </button>
            )}
            <label className="inline-flex cursor-pointer items-center gap-2 rounded-xl border border-white/20 px-4 py-3 text-sm font-semibold">
              <input
                type="checkbox"
                checked={selectedModule.status === 'COMPLETED'}
                disabled={updatingProgress}
                onChange={(event) => updateStatus(event.target.checked ? 'COMPLETED' : 'IN_PROGRESS')}
                className="h-4 w-4 accent-emerald-500"
              />
              Mark module complete
            </label>
          </div>
        </section>

        <section className="grid gap-6 lg:grid-cols-[1.15fr_0.85fr]">
          <div className="space-y-6">
            <div className="rounded-2xl border border-slate-200 bg-white p-6">
              <div className="flex items-center gap-2">
                <Target className="h-5 w-5 text-emerald-600" />
                <h2 className="text-xl font-extrabold text-slate-900">What you’ll learn</h2>
              </div>
              <ol className="mt-5 space-y-3">
                {selectedModule.learningObjectives.map((objective, index) => (
                  <li key={objective} className="flex gap-3 rounded-xl bg-slate-50 p-4">
                    <span className="flex h-7 w-7 shrink-0 items-center justify-center rounded-full bg-emerald-100 text-xs font-bold text-emerald-700">{index + 1}</span>
                    <span className="text-sm leading-6 text-slate-700">{objective}</span>
                  </li>
                ))}
              </ol>
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6">
              <div className="flex items-center gap-2">
                <BookOpen className="h-5 w-5 text-emerald-600" />
                <h2 className="text-xl font-extrabold text-slate-900">Learning resources</h2>
              </div>
              <p className="mt-2 text-sm text-slate-500">Official documentation and curated learning links. Search links lead to provider results, not a specific video.</p>
              {resources.length > 0 ? (
                <div className="mt-5 grid gap-3 sm:grid-cols-2">
                  {resources.map((resource) => (
                    <a
                      key={`${resource.title}-${resource.url}`}
                      href={resource.url}
                      target="_blank"
                      rel="noreferrer"
                      className="group rounded-xl border border-slate-200 p-4 transition hover:border-emerald-400 hover:bg-emerald-50/40"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <span className="text-[11px] font-bold uppercase tracking-wide text-emerald-700">{resource.type}</span>
                          <h3 className="mt-1 font-bold text-slate-900 group-hover:text-emerald-700">{resource.title}</h3>
                        </div>
                        <ExternalLink className="mt-1 h-4 w-4 shrink-0 text-slate-400" />
                      </div>
                      <span className={`mt-3 inline-block text-xs ${resource.verification === 'verified' ? 'text-emerald-700' : 'text-amber-700'}`}>
                        {resource.verification === 'verified' ? 'Link checked' : 'Link not verified'}
                      </span>
                    </a>
                  ))}
                </div>
              ) : (
                <div className="mt-4 rounded-xl bg-amber-50 p-4 text-sm text-amber-900">
                  No curated external link is available for this skill yet. Use the learning objectives, examples, and hands-on tasks below as a guided alternative.
                </div>
              )}
            </div>

            <div className="rounded-2xl border border-slate-200 bg-white p-6">
              <div className="flex items-center gap-2">
                <GraduationCap className="h-5 w-5 text-emerald-600" />
                <h2 className="text-xl font-extrabold text-slate-900">Concept guide and example</h2>
              </div>
              <div className="mt-5 space-y-4">{renderExplanation(selectedModule.contentExplanations)}</div>
            </div>
          </div>

          <div className="space-y-6">
            <section className="rounded-2xl border border-slate-200 bg-white p-6">
              <div className="flex flex-wrap items-center justify-between gap-3">
                <div>
                  <h2 className="text-xl font-extrabold text-slate-900">Daily study plan</h2>
                  <p className="mt-1 text-sm text-slate-500">The planned activity time never exceeds your daily limit.</p>
                </div>
                <span className="rounded-lg bg-emerald-50 px-3 py-2 text-sm font-bold text-emerald-700">{dailyMinutes} min/day</span>
              </div>
              <div className="mt-5 space-y-3">
                {planDays.map((day) => (
                  <label key={day.day} className="flex cursor-pointer gap-3 rounded-xl border border-slate-200 p-4 hover:border-emerald-300">
                    <input
                      type="checkbox"
                      checked={completedDays[day.day] ?? false}
                      onChange={(event) => setCompletedDays((current) => ({ ...current, [day.day]: event.target.checked }))}
                      className="mt-1 h-4 w-4 accent-emerald-600"
                    />
                    <span className="min-w-0 flex-1">
                      <span className="flex items-center justify-between gap-2">
                        <span className="font-bold text-slate-900">Day {day.day}</span>
                        <span className="text-xs font-semibold text-slate-500">{day.minutes} min</span>
                      </span>
                      <span className="mt-1 block text-sm leading-5 text-slate-600">{day.topic}</span>
                      {day.exercise && <span className="mt-2 block text-xs text-emerald-800">Practice: {day.exercise}</span>}
                      {resources[0] && (
                        <a
                          href={resources[0].url}
                          target="_blank"
                          rel="noreferrer"
                          onClick={(event) => event.stopPropagation()}
                          className="mt-2 inline-flex items-center gap-1 text-xs font-semibold text-emerald-700 hover:underline"
                        >
                          Open {resources[0].title} <ExternalLink className="h-3 w-3" />
                        </a>
                      )}
                    </span>
                  </label>
                ))}
              </div>
            </section>

            <section className="rounded-2xl border border-slate-200 bg-white p-6">
              <h2 className="text-xl font-extrabold text-slate-900">Hands-on tasks</h2>
              <ul className="mt-4 space-y-3">
                {selectedModule.practiceExercises.map((exercise, index) => (
                  <li key={exercise} className="flex gap-3 text-sm leading-6 text-slate-700">
                    <CheckCircle2 className="mt-1 h-4 w-4 shrink-0 text-emerald-600" />
                    <span><strong>Task {index + 1}.</strong> {exercise}</span>
                  </li>
                ))}
              </ul>
            </section>

            {selectedModule.practiceQuestions.length > 0 && (
              <section className="rounded-2xl border border-slate-200 bg-white p-6">
                <h2 className="text-xl font-extrabold text-slate-900">Practice questions</h2>
                <div className="mt-4 space-y-5">
                  {selectedModule.practiceQuestions.map((question, index) => (
                    <div key={`${question.questionIndex}-${question.question}`} className="border-t border-slate-100 pt-4 first:border-0 first:pt-0">
                      <p className="text-sm font-semibold leading-6 text-slate-800">{question.question}</p>
                      {question.type === 'MULTIPLE_CHOICE' && question.options ? (
                        <div className="mt-3 space-y-2">
                          {question.options.map((option, optionIndex) => (
                            <label key={option} className="flex cursor-pointer items-start gap-2 text-sm text-slate-600">
                              <input
                                type="radio"
                                name={`question-${index}`}
                                value={optionIndex}
                                checked={answers[index] === String(optionIndex)}
                                onChange={(event) => setAnswers((current) => ({ ...current, [index]: event.target.value }))}
                                className="mt-1 accent-emerald-600"
                              />
                              {option}
                            </label>
                          ))}
                        </div>
                      ) : (
                        <textarea
                          value={answers[index] ?? ''}
                          onChange={(event) => setAnswers((current) => ({ ...current, [index]: event.target.value }))}
                          rows={3}
                          placeholder="Write your answer..."
                          className="mt-3 w-full rounded-xl border border-slate-300 p-3 text-sm outline-none focus:border-emerald-500 focus:ring-2 focus:ring-emerald-100"
                        />
                      )}
                      <button
                        type="button"
                        disabled={submittingQuestion === index}
                        onClick={() => submitPractice(index)}
                        className="mt-3 rounded-lg bg-slate-900 px-4 py-2 text-xs font-bold text-white hover:bg-slate-700 disabled:opacity-60"
                      >
                        {submittingQuestion === index ? 'Checking...' : 'Submit answer'}
                      </button>
                      {evaluations[index] && (
                        <div className={`mt-3 rounded-lg p-3 text-sm ${evaluations[index].correct ? 'bg-emerald-50 text-emerald-800' : 'bg-amber-50 text-amber-900'}`}>
                          <strong>{evaluations[index].correct ? 'Good work' : 'Review this concept'} · {evaluations[index].score}%</strong>
                          <p className="mt-1">{evaluations[index].feedback}</p>
                          {evaluations[index].correctAnswerExplanation && <p className="mt-1">{evaluations[index].correctAnswerExplanation}</p>}
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </section>
            )}
          </div>
        </section>
      </div>
    );
  }

  const modules = dashboard?.modules ?? [];
  return (
    <div className="space-y-7">
      {error && <div role="alert" className="rounded-xl border border-rose-200 bg-rose-50 p-4 text-sm text-rose-700">{error}</div>}

      <section className="rounded-2xl bg-slate-900 p-6 text-white shadow-lg sm:p-8">
        <div className="flex flex-col justify-between gap-5 md:flex-row md:items-center">
          <div>
            <span className="inline-flex items-center gap-2 rounded-full bg-emerald-500/20 px-3 py-1 text-xs font-bold text-emerald-300">
              <GraduationCap className="h-4 w-4" /> Your personalized roadmap
            </span>
            <h1 className="mt-4 text-3xl font-black">{dashboard?.roadmapTitle || 'Learning roadmap'}</h1>
            <p className="mt-2 text-sm text-slate-300">
              {dashboard?.targetRole ? `Built around your ${dashboard.targetRole} goal.` : 'Choose a skill-gap analysis to create your role-focused plan.'}
              {' '}Start with a skill to see objectives, resources, practice, and a realistic daily schedule.
            </p>
          </div>
          {modules.length > 0 && renderStatusButton(modules.find((module) => module.status !== 'COMPLETED') ?? modules[0])}
        </div>
      </section>

      {modules.length > 0 ? (
        <>
          <section className="grid grid-cols-2 gap-3 sm:grid-cols-4">
            {[
              { label: 'Overall progress', value: `${dashboard?.progressPercentage ?? 0}%` },
              { label: 'Modules completed', value: `${dashboard?.completedModules ?? 0}/${dashboard?.totalModules ?? modules.length}` },
              { label: 'In progress', value: dashboard?.inProgressModules ?? 0 },
              { label: 'Study streak', value: `${dashboard?.currentStreakDays ?? 0} days` },
            ].map((metric) => (
              <div key={metric.label} className="rounded-xl border border-slate-200 bg-white p-4 text-center">
                <strong className="text-2xl font-black text-slate-900">{metric.value}</strong>
                <p className="mt-1 text-xs font-semibold text-slate-500">{metric.label}</p>
              </div>
            ))}
          </section>

          <section className="flex flex-col justify-between gap-4 rounded-2xl border border-slate-200 bg-white p-5 sm:flex-row sm:items-center">
            <div>
              <h2 className="font-bold text-slate-900">Your daily study time</h2>
              <p className="mt-1 text-sm text-slate-500">Daily activities are split to fit this limit.</p>
            </div>
            <div className="flex flex-wrap items-center gap-2">
              <select
                value={dailyMinutes}
                onChange={(event) => setDailyMinutes(Number(event.target.value))}
                className="rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700"
                aria-label="Daily study time"
              >
                {!STUDY_TIME_OPTIONS.includes(dailyMinutes) && <option value={dailyMinutes}>{dailyMinutes} minutes</option>}
                {STUDY_TIME_OPTIONS.map((minutes) => <option key={minutes} value={minutes}>{minutes} minutes</option>)}
              </select>
              <button
                type="button"
                onClick={saveStudyTime}
                disabled={savingPreference || dailyMinutes < 15 || dailyMinutes > 480}
                className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-bold text-white hover:bg-emerald-700 disabled:opacity-60"
              >
                {savingPreference ? 'Saving...' : 'Save study time'}
              </button>
            </div>
          </section>

          <section>
            <div className="mb-4 flex items-end justify-between gap-3">
              <div>
                <h2 className="text-2xl font-black text-slate-900">Skills to develop</h2>
                <p className="mt-1 text-sm text-slate-500">Prioritized from your latest skill-gap analysis.</p>
              </div>
              {dashboard?.estimatedWeeks ? <span className="text-sm font-semibold text-slate-500">About {dashboard.estimatedWeeks} weeks</span> : null}
            </div>
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
              {modules.map((module) => (
                <article
                  key={module.id}
                  onClick={() => openModule(module)}
                  onKeyDown={(event) => event.key === 'Enter' && openModule(module)}
                  role="button"
                  tabIndex={0}
                  className="flex cursor-pointer flex-col rounded-2xl border border-slate-200 bg-white p-5 text-left shadow-sm transition hover:-translate-y-0.5 hover:border-emerald-400 hover:shadow-md"
                >
                  <div className="flex items-start justify-between gap-3">
                    <span className="rounded-lg bg-emerald-50 px-2.5 py-1 text-xs font-bold text-emerald-800">Module {module.orderIndex}</span>
                    <span className={`rounded-full border px-2.5 py-1 text-[11px] font-bold ${statusStyle(module.status)}`}>{statusLabel(module.status)}</span>
                  </div>
                  <h3 className="mt-4 text-xl font-extrabold text-slate-900">{module.skillName}</h3>
                  <p className="mt-2 line-clamp-3 flex-1 text-sm leading-6 text-slate-600">{module.whyItMatters}</p>
                  <div className="mt-4 space-y-2 border-t border-slate-100 pt-4 text-xs text-slate-500">
                    <p className="flex items-center gap-2"><Clock className="h-4 w-4" />{module.estimatedHours}</p>
                    <p className="flex items-center gap-2"><Target className="h-4 w-4" />{module.currentProficiency} → {module.targetProficiency}</p>
                  </div>
                  <div className="mt-4 flex items-center justify-between">
                    <span className="text-xs font-bold text-emerald-700">Explore Studio</span>
                    <ArrowRight className="h-4 w-4 text-emerald-700" />
                  </div>
                  <div className="mt-4" onClick={(event) => event.stopPropagation()}>{renderStatusButton(module)}</div>
                </article>
              ))}
            </div>
          </section>
        </>
      ) : (
        <section className="rounded-2xl border border-slate-200 bg-white p-8 text-center">
          <BookOpen className="mx-auto h-12 w-12 text-emerald-600" />
          <h2 className="mt-4 text-xl font-extrabold text-slate-900">Create your personalized roadmap</h2>
          <p className="mx-auto mt-2 max-w-xl text-sm leading-6 text-slate-600">
            Run a skill-gap analysis first. CareerGap AI will use the target role and identified gaps to create a learning path you can track here.
          </p>
          <Link to="/analyzer" className="mt-5 inline-flex items-center gap-2 rounded-xl bg-emerald-600 px-5 py-3 font-bold text-white hover:bg-emerald-700">
            Analyze skill gaps <ArrowRight className="h-4 w-4" />
          </Link>
        </section>
      )}
    </div>
  );
};

export default LearningRoadmapPage;
