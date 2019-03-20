create table authorizations
(
  id bigint NOT NULL,
  access_token varchar(100),
  secret_token varchar(100),
   primary key (id)
);