create table users
(
    id         bigint primary key,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    income     bigint       not null default 100,
    created_at timestamp    not null default now(),
    updated_at timestamp    not null default now()
);

insert into users (id, first_name, last_name)
values (1, 'Zhenya', 'Yefimenka'),
       (2, 'Nikolay', 'Ivanov');

create table sport
(
    id     bigint primary key,
    code   varchar(255) not null unique,
    status varchar(30)  not null
);

create table category
(
    id       bigint       not null,
    sport_id bigint       not null references sport (id),
    code     varchar(255) not null unique,
    status   varchar(30)  not null,
    PRIMARY KEY (id, sport_id)
);

insert into sport (id, code, status)
values (1, 'FOOTBALL', 'OPENED'),
       (2, 'BASKETBALL', 'OPENED');

insert into category (id, sport_id, code, status)
values (1, 1, 'RU', 'OPENED'),
       (2, 1, 'EN', 'OPENED');
