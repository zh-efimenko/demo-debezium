create table if not exists public.dbz_signal
(
    id   varchar(64),
    type varchar(32),
    data varchar(2048)
);

create table if not exists public.dbz_heartbeat
(
    id bigserial primary key,
    ts timestamp default current_timestamp
);
