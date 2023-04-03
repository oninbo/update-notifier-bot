CREATE TABLE links (
    id uuid NOT NULL DEFAULT gen_random_uuid(),
    url text NOT NULL,
    created_at timestamp NOT NULL DEFAULT now(),
    CONSTRAINT links_pk PRIMARY KEY (id)
);
