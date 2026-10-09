CREATE USER dev_user WITH PASSWORD 'dev_password';

CREATE USER test_user WITH PASSWORD 'test_password';

CREATE DATABASE devdb OWNER dev_user;
CREATE DATABASE testdb OWNER test_user;
