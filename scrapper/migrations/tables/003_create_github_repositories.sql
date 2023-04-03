CREATE TABLE github_repositories (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name text NOT NULL,
    user_name text NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp,
    CONSTRAINT github_repositories_pk PRIMARY KEY (id),
    CONSTRAINT unique_name_user UNIQUE (name, user_name)
);

ALTER TABLE links
    ADD COLUMN github_repository_id uuid,
    ADD CONSTRAINT github_repository_fk FOREIGN KEY (github_repository_id) REFERENCES github_repositories (id),
    ADD CONSTRAINT unique_github_repository_id_user_id UNIQUE (user_id, github_repository_id);
