-- CareerGap AI Schema Definition for PostgreSQL
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(50),
    location VARCHAR(255),
    degree VARCHAR(255),
    branch VARCHAR(255),
    college VARCHAR(255),
    graduation_year INT,
    academic_status VARCHAR(100),
    target_role VARCHAR(255),
    preferred_industry VARCHAR(255),
    years_of_experience INT DEFAULT 0,
    education VARCHAR(255),
    bio TEXT,
    soft_skills TEXT,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    reset_password_token VARCHAR(255),
    reset_password_expiry TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_skills (
    id BIGSERIAL PRIMARY KEY,
    skill_name VARCHAR(255) NOT NULL,
    proficiency_level VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL DEFAULT 'Technical',
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_projects (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    technologies VARCHAR(255),
    github_url VARCHAR(255),
    live_url VARCHAR(255),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_experiences (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    duration VARCHAR(100),
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS user_certifications (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    issuer VARCHAR(255) NOT NULL,
    issue_year INT,
    credential_url VARCHAR(255),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resumes (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(100),
    parsed_text TEXT,
    uploaded_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS skill_analyses (
    id BIGSERIAL PRIMARY KEY,
    target_role VARCHAR(255) NOT NULL,
    job_description TEXT NOT NULL,
    overall_match_score INT NOT NULL,
    technical_score INT NOT NULL,
    soft_skills_score INT NOT NULL,
    present_skills TEXT,
    missing_skills TEXT,
    strengths TEXT,
    weaknesses TEXT,
    recommendations TEXT,
    skills_analysis_json TEXT,
    calculation_criteria TEXT,
    high_priority_gaps_json TEXT,
    readiness_level VARCHAR(100),
    created_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS learning_roadmaps (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    target_role VARCHAR(255),
    estimated_weeks INT NOT NULL,
    daily_study_minutes INT NOT NULL DEFAULT 60,
    stages_json TEXT,
    created_at TIMESTAMP,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    analysis_id BIGINT REFERENCES skill_analyses(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS learning_modules (
    id BIGSERIAL PRIMARY KEY,
    roadmap_id BIGINT NOT NULL REFERENCES learning_roadmaps(id) ON DELETE CASCADE,
    skill_name VARCHAR(255) NOT NULL,
    category VARCHAR(50) DEFAULT 'Technical',
    order_index INT NOT NULL DEFAULT 1,
    estimated_hours VARCHAR(50),
    current_proficiency VARCHAR(100),
    target_proficiency VARCHAR(100),
    why_it_matters TEXT,
    learning_objectives_json TEXT,
    suitable_resources_json TEXT,
    content_explanations TEXT,
    practice_exercises_json TEXT,
    practice_questions_json TEXT,
    dependencies_json TEXT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_module_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    module_id BIGINT NOT NULL REFERENCES learning_modules(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL DEFAULT 'NOT_STARTED',
    score INT DEFAULT 0,
    user_notes TEXT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    last_accessed_at TIMESTAMP,
    CONSTRAINT uq_user_module UNIQUE (user_id, module_id)
);

CREATE TABLE IF NOT EXISTS practice_submissions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    module_id BIGINT NOT NULL REFERENCES learning_modules(id) ON DELETE CASCADE,
    question_index INT NOT NULL,
    question_type VARCHAR(50) NOT NULL,
    question_text TEXT,
    user_answer TEXT,
    is_correct BOOLEAN DEFAULT FALSE,
    score INT DEFAULT 0,
    feedback TEXT,
    submitted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mock_tests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_role VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    total_questions INT DEFAULT 10,
    time_limit_minutes INT DEFAULT 30,
    questions_json TEXT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS mock_test_attempts (
    id BIGSERIAL PRIMARY KEY,
    test_id BIGINT NOT NULL REFERENCES mock_tests(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    correct_count INT NOT NULL,
    strengths_json TEXT,
    weaknesses_json TEXT,
    revision_topics_json TEXT,
    answers_json TEXT,
    attempted_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_learning_streaks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    current_streak_days INT DEFAULT 0,
    longest_streak_days INT DEFAULT 0,
    last_activity_date DATE,
    total_completed_modules INT DEFAULT 0,
    total_practice_questions INT DEFAULT 0,
    total_mock_tests INT DEFAULT 0,
    total_learning_hours NUMERIC(6, 2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS skill_verifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    skill_name VARCHAR(255) NOT NULL,
    target_role VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'UNVERIFIED',
    verification_method VARCHAR(100),
    assessment_score INT,
    project_title VARCHAR(255),
    project_github_url VARCHAR(255),
    project_live_url VARCHAR(255),
    project_evidence_details TEXT,
    verification_badge VARCHAR(100),
    evaluator_feedback TEXT,
    verified_at TIMESTAMP,
    created_at TIMESTAMP,
    CONSTRAINT uq_user_skill_verify UNIQUE (user_id, skill_name)
);

CREATE TABLE IF NOT EXISTS ai_mentor_messages (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sender VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    suggested_actions_json TEXT,
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS resume_versions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    version_number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    target_job_title VARCHAR(255),
    target_job_description TEXT,
    resume_data_json TEXT NOT NULL,
    tailored_keywords_json TEXT,
    change_summary TEXT,
    created_at TIMESTAMP
);
