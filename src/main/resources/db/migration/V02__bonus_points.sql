create table bonus_point_movie
(
    movie_id   varchar(255) not null
        primary key,
    movie_type varchar(255)
);

create table bonus_point
(
    bonus_points_id varchar(255) primary key,
    owner           varchar(255),
    number          integer
)