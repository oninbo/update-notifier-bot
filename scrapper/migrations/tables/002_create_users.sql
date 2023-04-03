CREATE TABLE users (
   id uuid NOT NULL DEFAULT gen_random_uuid(),
   chat_id bigint NOT NULL,
   CONSTRAINT unique_chat_id UNIQUE (chat_id),
   CONSTRAINT users_pk PRIMARY KEY (id)
);

-- noinspection SqlAddNotNullColumn
ALTER TABLE links
    ADD COLUMN user_id uuid NOT NULL,
    ADD CONSTRAINT user_id_fk FOREIGN KEY (user_id) REFERENCES users (id);
