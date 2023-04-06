CREATE TABLE tg_chats (
   id uuid NOT NULL DEFAULT gen_random_uuid(),
   chat_id bigint NOT NULL,
   CONSTRAINT unique_chat_id UNIQUE (chat_id),
   CONSTRAINT tg_chats_pk PRIMARY KEY (id)
);

-- noinspection SqlAddNotNullColumn
ALTER TABLE links
    ADD COLUMN tg_chat_id uuid NOT NULL,
    ADD CONSTRAINT tg_chat_id_fk FOREIGN KEY (tg_chat_id) REFERENCES tg_chats (id);
