CREATE TABLE properties (
  name VARCHAR(255) NOT NULL,
  value VARCHAR(255) NULL,
  CONSTRAINT pk_properties PRIMARY KEY (name)
);

CREATE TABLE queries (
  id BIGINT AUTO_INCREMENT NOT NULL,
  account VARCHAR(255) NULL,
  signature VARCHAR(255) NULL,
  body LONGTEXT NULL,
  processing_error BIT(1) NULL,
  request_date datetime NULL,
  processing_result_code INT NULL,
  processing_date datetime NULL,
  message_type VARCHAR(255) NULL,
  message_token VARCHAR(255) NULL,
  message_user_id VARCHAR(255) NULL,
  processing_error_message LONGTEXT NULL,
  retry BIT(1) NULL,
  CONSTRAINT pk_queries PRIMARY KEY (id)
);

CREATE TABLE query_sequence (
  next_val BIGINT DEFAULT NULL
);

CREATE TABLE users (
  id BIGINT AUTO_INCREMENT NOT NULL,
  account_id VARCHAR(255) NULL,
  login VARCHAR(255) NULL,
  password VARCHAR(255) NULL,
  CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE users_sequence (
  next_val BIGINT DEFAULT NULL
);