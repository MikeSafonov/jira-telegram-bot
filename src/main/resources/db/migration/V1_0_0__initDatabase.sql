create table chats
(
  id serial primary key,
  jira_id     varchar(100) NOT NULL unique,
  telegram_id bigint          NOT NULL unique
);


create table templates
(
  id serial primary key,
  template_key varchar(100) not null unique,
  template TEXT not null
);


