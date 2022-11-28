– liquibase formatted sql

– changeset serebrovev:1
CREATE TABLE notification (
    id SERIAL,
    user BIGINT,
    event TEXT,
    date TIMESTAMP
)