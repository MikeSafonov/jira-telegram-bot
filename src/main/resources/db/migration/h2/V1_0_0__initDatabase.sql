create table chats
(
  id bigint IDENTITY  NOT NULL ,
  jira_id     varchar(100) NOT NULL unique,
  telegram_id bigint          NOT NULL unique,
  primary key (id)
);


create table templates
(
  id bigint IDENTITY  NOT NULL ,
  template_key varchar(100) not null unique,
  template CLOB not null,
  primary key (id)
);


