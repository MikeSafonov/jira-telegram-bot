create table authorizations
(
  id bigint NOT NULL primary key,
  access_token varchar(100),
  secret_token varchar(100)
  );