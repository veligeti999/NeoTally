CREATE TABLE user (
    id BIGINT NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    adhar_id VARCHAR(100) NOT NULL,
    dob date NOT NULL,
    male BOOL NOT NULL,
    active BOOL NOT NULL,


    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip long NOT NULL,
    country VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY (adhar_id)
) ENGINE = INNODB;

CREATE TABLE merchant (
    id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    license_id VARCHAR(100) NOT NULL,
    active BOOL NOT NULL,

    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip long NOT NULL,
    country VARCHAR(100) NOT NULL,


    PRIMARY KEY (id),
    UNIQUE KEY(license_id)
) ENGINE = INNODB;

CREATE TABLE merchant_branch (
    id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    manager_name VARCHAR(100) NOT NULL,
    head_quarter BOOL,

    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip long NOT NULL,
    country VARCHAR(100) NOT NULL,

    UNIQUE KEY (merchant_id, head_quarter),
    UNIQUE KEY (merchant_id, name),
    PRIMARY KEY (id),
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE
) ENGINE = INNODB;

CREATE TABLE branch_counter (
    branch_id BIGINT NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    active BOOL NOT NULL,

    UNIQUE KEY (branch_id, phone),
    PRIMARY KEY (password),

    FOREIGN KEY (branch_id) REFERENCES merchant_branch(id) ON DELETE CASCADE
)  ENGINE = INNODB;

