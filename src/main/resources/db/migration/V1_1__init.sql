create table if not exists marketing.user
(
    id         bigserial primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    income     bigint       not null default 100,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now()
);

insert into marketing.user (first_name, last_name)
values ('Zhenya', 'Yefimenka'),
       ('Nikolay', 'Ivanov');

create table if not exists custom.sport
(
    id     bigserial primary key,
    code   varchar(255) not null unique,
    status varchar(30)  not null
);

create table if not exists custom.category
(
    id       bigint       not null,
    sport_id bigint       not null references custom.sport (id),
    code     varchar(255) not null unique,
    status   varchar(30)  not null,
    PRIMARY KEY (id, sport_id)
);

insert into custom.sport (id, code, status)
values (1, 'FOOTBALL', 'OPENED'),
       (2, 'BASKETBALL', 'OPENED');

insert into custom.category (id, sport_id, code, status)
values (1, 1, 'RU', 'OPENED'),
       (2, 1, 'EN', 'OPENED');
