CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    login    TEXT   NOT NULL UNIQUE,
    password TEXT   NOT NULL,
    roles    TEXT[] NOT NULL DEFAULT '{}' -- '{ROLE1, ROLE2}' - массивы в PostgreSQL
);

CREATE TABLE tokens(
                       value TEXT PRIMARY KEY,
                       user_id BIGINT NOT NULL REFERENCES users
);

--CREATE TABLE accounts (
   -- id TEXT PRIMARY KEY,
   -- owner TEXT NOT NULL,
   -- balance INT NOT NULL DEFAULT 0
--);

--CREATE TABLE users (
 --   id BIGSERIAL PRIMARY KEY,
  --  login TEXT NOT NULL UNIQUE,
   -- password TEXT NOT NULL
--);

CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users,
    text TEXT NOT NULL,
    done BOOL NOT NULL DEFAULT FALSE
);




CREATE TABLE results(
id BIGSERIAL PRIMARY KEY ,
task_id BIGINT references tasks,
name_file TEXT NOT NULL,
number_line BIGINT NOT NULL
);
