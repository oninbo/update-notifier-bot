CREATE TABLE stackoverflow_questions (
    id uuid NOT NULL default gen_random_uuid(),
    question_id bigint NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz,
    CONSTRAINT stackoverflow_questions_pk PRIMARY KEY (id),
    CONSTRAINT unique_question_id UNIQUE (question_id)
);
