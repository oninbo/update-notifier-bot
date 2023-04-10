CREATE TABLE github_repositories (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    name text NOT NULL,
    username text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz,
    CONSTRAINT github_repositories_pk PRIMARY KEY (id),
    CONSTRAINT unique_name_user UNIQUE (name, username)
);
