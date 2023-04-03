ALTER TABLE links
    ALTER created_at SET DEFAULT now();

ALTER TABLE users
    ALTER created_at SET DEFAULT now();

ALTER TABLE github_repositories
    ALTER created_at SET DEFAULT now();

ALTER TABLE stackoverflow_questions
    ALTER created_at SET DEFAULT now();
