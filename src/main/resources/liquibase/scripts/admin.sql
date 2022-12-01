-- liquibase formatted sql

--changeset eserebrov:1
CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    "event" TEXT,
    "date" TIMESTAMP
)