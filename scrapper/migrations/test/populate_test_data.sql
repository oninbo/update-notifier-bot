INSERT INTO github_repositories (name, user_name)
VALUES ('tinkoff-java-course-2022', 'sanyarnd');

INSERT INTO stackoverflow_questions (question_id)
VALUES ('42307687');

INSERT INTO users (chat_id)
VALUES ('1');

INSERT INTO links (url,
                   user_id,
                   github_repository_id,
                   stackoverflow_question_id)
VALUES ('https://stackoverflow.com/questions/42307687/get-complete-jdk-source-code-in-intellij-or-other-ide',
        (select id from users limit 1),
        null,
        (select id from stackoverflow_questions limit 1)),

       ('https://github.com/sanyarnd/tinkoff-java-course-2022',
        (select id from users limit 1),
        (select id from github_repositories limit 1),
        null);
