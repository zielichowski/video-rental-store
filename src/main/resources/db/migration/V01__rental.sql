create table rental
(
    rental_id    varchar(255)                not null
        primary key,
    denomination numeric(38, 2)              not null,
    rental_date  timestamp(6) with time zone not null,
    client_id    varchar(255)                not null,
    currency     varchar(3)                  not null

);

create table return
(
    return_id              varchar(255)                not null
        primary key,
    return_date            timestamp(6) with time zone not null,
    surcharge_denomination numeric(38, 2)              not null,
    surcharge_currency     varchar(3)                  not null,
    rental_id              varchar(255) unique         not null
);


create index rental_client_id_index
    on rental (client_id);

create table rental_item
(
    rental_days integer      not null,
    rental_id   varchar(255) not null
        constraint fka20tqix3c9aesqmluuq9lkn9b
            references rental,
    movie_id    varchar(255) not null,
    primary key (rental_id, movie_id)
);