CREATE SEQUENCE  IF NOT EXISTS query_sequence START WITH 1 INCREMENT BY 10;

CREATE SEQUENCE  IF NOT EXISTS users_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE properties (
  name VARCHAR(255) NOT NULL,
   value VARCHAR(255),
   CONSTRAINT pk_properties PRIMARY KEY (name)
);

CREATE TABLE queries (
  id BIGINT NOT NULL,
   account VARCHAR(255),
   signature VARCHAR(255),
   body TEXT,
   processing_error BOOLEAN,
   request_date TIMESTAMP WITHOUT TIME ZONE,
   processing_result_code INTEGER,
   processing_date TIMESTAMP WITHOUT TIME ZONE,
   message_type VARCHAR(255),
   message_token VARCHAR(255),
   message_user_id VARCHAR(255),
   processing_error_message TEXT,
   retry BOOLEAN,
   CONSTRAINT pk_queries PRIMARY KEY (id)
);

CREATE TABLE users (
  id BIGINT NOT NULL,
   account_id VARCHAR(255),
   login VARCHAR(255),
   password VARCHAR(255),
   CONSTRAINT pk_users PRIMARY KEY (id)
);