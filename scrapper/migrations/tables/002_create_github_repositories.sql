CREATE TABLE github_repositories (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name text NOT NULL,
    username text NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    updated_at timestamp,
    CONSTRAINT github_repositories_pk PRIMARY KEY (id),
    CONSTRAINT unique_name_user UNIQUE (name, username)
);
