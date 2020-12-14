create table tags
(
    id bigint IDENTITY NOT NULL,
    tag varchar(100) NOT NULL,
    primary key (id)
);

create table chats_tags (
    id_tag bigint NOT NULL,
    id_chat bigint NOT NULL,
    primary key (id_tag, id_chat)
);

ALTER TABLE chats_tags
    ADD FOREIGN KEY (id_tag)
        REFERENCES tags(id);

ALTER TABLE chats_tags
    ADD FOREIGN KEY (id_chat)
        REFERENCES chats(id);
