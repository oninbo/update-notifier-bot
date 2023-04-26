ALTER TABLE stackoverflow_questions
    ADD COLUMN answers_updated_at timestamptz NOT NULL DEFAULT now();
