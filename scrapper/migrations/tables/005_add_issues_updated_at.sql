ALTER TABLE github_repositories
    ADD COLUMN issues_updated_at timestamptz NOT NULL DEFAULT now();
