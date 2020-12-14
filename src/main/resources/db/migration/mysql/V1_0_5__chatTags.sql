create table tags
(
    id bigint AUTO_INCREMENT NOT NULL,
    tag varchar(100) NOT NULL,
    primary key (id)
) DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;

create table chats_tags (
                          id_tag bigint NOT NULL,
                          id_chat bigint NOT NULL,
                          primary key (id_tag, id_chat)
) DEFAULT CHARSET = utf8 COLLATE = utf8_bin ENGINE = InnoDB;

ALTER TABLE chats_tags
    ADD FOREIGN KEY (id_tag)
        REFERENCES tags(id);

ALTER TABLE chats_tags
    ADD FOREIGN KEY (id_chat)
        REFERENCES chats(id);
