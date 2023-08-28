create table filters_subscriptions
(
  id_chat bigint NOT NULL ,
  id_filter  bigint NOT NULL,
  primary key (id_chat, id_filter)
);

ALTER TABLE filters_subscriptions ADD FOREIGN KEY (id_chat) REFERENCES chats(id);