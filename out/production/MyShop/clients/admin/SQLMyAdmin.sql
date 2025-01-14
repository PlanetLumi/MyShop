CREATE TABLE AdminAccounts(
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255),
    salt VARCHAR(255),
    role VARCHAR(50)
);

INSERT INTO AdminAccounts(username, password, salt, role) DEFAULT VALUES('admin1',null,null,null)