create table user (
    id bigint not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    middle_name varchar(100),
    password varchar(100) not null,
    adhar_id varchar(100) not null,
    dob date not null,
    gender bool not null,
    phone varchar(100) not null,
    email varchar(100),

    address varchar(200) not null,
    city varchar(100) not null,
    state varchar(100) not null,
    zip long not null,
    country varchar(100) not null,

    primary key (id),
    unique key(adhar_id)
);
