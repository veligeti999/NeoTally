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
);

CREATE TABLE merchant (
    id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
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
    UNIQUE KEY(license)
);

CREATE TABLE merchant_branch (
    id BIGINT NOT NULL,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    manager_name VARCHAR(100) NOT NULL,
    
    -- should be only set to true otherwise null
    primary BOOL, 

    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip long NOT NULL,
    country VARCHAR(100) NOT NULL,

    UNIQUE KEY (merchant_id, primary)
    UNIQUE KEY (merchant_id, name)
    PRIMARY KEY (id),
    FOREIGN KEY (merchant_id) REFERENCES merchant(id) ON DELETE CASCADE
);

CREATE TABLE merchant_counter (
    id BIGINT NOT NULL,
    branch_id BIGINT NOT NULL,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(100) NOT NULL,
    active BOOL NOT NULL,

    UNIQUE KEY (branch_id, id)

    FOREIGN KEY (branch_id) REFERENCES merchant_branch(id) ON DELETE CASCADE
);
