create table chats
(
  id BIGINT AUTO_INCREMENT NOT NULL ,
  jira_id     varchar(100) NOT NULL UNIQUE,
  telegram_id bigint          NOT NULL UNIQUE,
  primary key (id)
)DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;


create table templates
(
  id BIGINT AUTO_INCREMENT NOT NULL ,
  template_key varchar(100) NOT NULL UNIQUE,
  template TEXT NOT NULL,
  primary key (id)
)DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;


