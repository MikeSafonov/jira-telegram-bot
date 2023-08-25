create table filters_subscriptions
(
  id_chat BIGINT NOT NULL ,
  id_filter  BIGINT NOT NULL,
  primary key (id_chat, id_filter)
)DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;

ALTER TABLE filters_subscriptions ADD FOREIGN KEY (id_chat) REFERENCES chats(id);