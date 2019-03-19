create table authorizations
(
  id BIGINT NOT NULL,
  access_token varchar(100),
  secret_token varchar(100),
   primary key (id)
  )DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;