CREATE TABLE stackoverflow_questions (
    id uuid NOT NULL default gen_random_uuid(),
    question_id bigint NOT NULL,
    created_at timestamp NOT NULL,
    updated_at timestamp,
    CONSTRAINT stackoverflow_questions_pk PRIMARY KEY (id),
    CONSTRAINT unique_question_id UNIQUE (question_id)
);

ALTER TABLE links
    ADD COLUMN stackoverflow_question_id uuid,

    ADD CONSTRAINT stackoverflow_question_fk FOREIGN KEY (stackoverflow_question_id)
        REFERENCES stackoverflow_questions (id),

    ADD CONSTRAINT points_to_one_resource
        CHECK ( num_nonnulls(stackoverflow_question_id, github_repository_id) = 1 );
