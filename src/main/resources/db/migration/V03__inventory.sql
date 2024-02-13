create table inventory_movie
(
    movie_id          varchar(255) not null
        primary key,
    movie_title       varchar(255),
    movie_type        varchar(255),
    movie_description varchar(255)
);