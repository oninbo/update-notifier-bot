CREATE TABLE links (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    url text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    tg_chat_id uuid NOT NULL,
    github_repository_id uuid,
    stackoverflow_question_id uuid,

    CONSTRAINT links_pk PRIMARY KEY (id),
    CONSTRAINT tg_chat_id_fk FOREIGN KEY (tg_chat_id) REFERENCES tg_chats (id),
    CONSTRAINT github_repository_fk FOREIGN KEY (github_repository_id) REFERENCES github_repositories (id),
    CONSTRAINT unique_github_repository_id_tg_chat_id UNIQUE (tg_chat_id, github_repository_id),

    CONSTRAINT stackoverflow_question_fk FOREIGN KEY (stackoverflow_question_id)
        REFERENCES stackoverflow_questions (id),

    CONSTRAINT unique_stackoverflow_question_id_tg_chat_id UNIQUE (tg_chat_id, stackoverflow_question_id),

    CONSTRAINT points_to_one_resource
        CHECK ( num_nonnulls(stackoverflow_question_id, github_repository_id) = 1 )
);
