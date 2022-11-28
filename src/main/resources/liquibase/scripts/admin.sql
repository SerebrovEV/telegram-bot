-- liquibase formatted sql

--changeset eserebrov:1
CREATE TABLE notification (
    id SERIAL,
    user_id BIGINT,
    "event" TEXT,
    "date" TIMESTAMP
)