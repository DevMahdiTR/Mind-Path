-- Version: V1
-- Description: Create tables and indexes

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE access_tokens
(
    id      BIGSERIAL    NOT NULL,
    expired BOOLEAN      NOT NULL,
    revoked BOOLEAN      NOT NULL,
    token   VARCHAR(255) NOT NULL,
    user_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE confirmation_tokens
(
    id      BIGSERIAL    NOT NULL,
    expired BOOLEAN      NOT NULL,
    revoked BOOLEAN      NOT NULL,
    token   VARCHAR(255) NOT NULL,
    user_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE refresh_tokens
(
    id      BIGSERIAL    NOT NULL,
    expired BOOLEAN      NOT NULL,
    revoked BOOLEAN      NOT NULL,
    token   VARCHAR(255) NOT NULL,
    user_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE roles
(
    id   BIGINT       NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    user_type    varchar(31)  not null,
    id           UUID         NOT NULL,
    birthday     DATE         NOT NULL,
    created_at   TIMESTAMP(6) NOT NULL,
    email        VARCHAR(255) NOT NULL,
    full_name    VARCHAR(255) NOT NULL,
    governoarate VARCHAR(255) NOT NULL,
    is_enabled   BOOLEAN      NOT NULL,
    password     VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    updated_at   TIMESTAMP(6) NOT NULL,
    role_id      BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE teachers
(
    id uuid not null,
    primary key (id)
);

CREATE TABLE students
(
    id uuid not null,
    education_level varchar(255) not null,
    primary key (id)
);

CREATE TABLE super_teachers
(
    id uuid not null,
    primary key (id)
);

CREATE TABLE superteacher_teacher
(
    super_teacher_id uuid NOT NULL,
    teacher_id       uuid NOT NULL,
    PRIMARY KEY (super_teacher_id, teacher_id),
    CONSTRAINT fk_superteacher_teacher_super_teacher_id FOREIGN KEY (super_teacher_id)
        REFERENCES super_teachers (id) ON DELETE CASCADE,
    CONSTRAINT fk_superteacher_teacher_teacher_id FOREIGN KEY (teacher_id)
        REFERENCES teachers (id) ON DELETE CASCADE
);

CREATE TABLE groups
(
    id               BIGINT       NOT NULL,
    title            VARCHAR(255) NOT NULL,
    education_level      VARCHAR(255) NOT NULL,
    background_image_url TEXT         NOT NULL,
    main_image_url       TEXT         NOT NULL,
    created_at       TIMESTAMP(6) NOT NULL,
    updated_at       TIMESTAMP(6) NOT NULL,
    is_public BOOLEAN NOT NULL,
    super_teacher_id UUID,
    PRIMARY KEY (id)
);

CREATE TABLE events
(
    id  BIGINT  NOT NULL ,
    title VARCHAR(255) NOT NULL,
    start_time TIMESTAMP(6) NOT NULL,
    end_time TIMESTAMP(6) NOT NULL,
    background_color VARCHAR(255) NOT NULL,
    group_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE sections
(
    id            BIGINT       NOT NULL,
    section_name  VARCHAR(255) NOT NULL,
    section_color VARCHAR(255) NOT NULL,
    subject_id   BIGINT NOT NULL ,
    PRIMARY KEY (id)
);

CREATE TABLE subjects
(
    id                   BIGINT       NOT NULL,
    speciality           VARCHAR(255) NOT NULL,
    level                VARCHAR(255) NOT NULL,
    background_image_url TEXT         NOT NULL,
    main_image_url       TEXT         NOT NULL,
    teacher_id           UUID,
    group_id BIGINT not null,
    PRIMARY KEY (id)
);


CREATE TABLE Group_Student
(
    group_id   BIGINT NOT NULL,
    student_id uuid   NOT NULL,
    PRIMARY KEY (group_id, student_id)
);
CREATE TABLE student_offers
(
    id        BIGINT       NOT NULL,
    title    VARCHAR(50) NOT NULL,
    sub_title VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    price   FLOAT NOT NULL,
    monthly_period INT NOT NULL,
    image_url TEXT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    offer_details TEXT NOT NULL,
    class_id   BIGINT NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE teacher_offers
(
    id        BIGINT       NOT NULL,
    title    VARCHAR(50) NOT NULL,
    sub_title VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    price   FLOAT NOT NULL,
    monthly_period INT NOT NULL,
    image_url TEXT NOT NULL,
    offer_details TEXT NOT NULL,
    class_number INT NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE teacher_offer_requests
(
    id                BIGINT       NOT NULL,
    payment_image_url TEXT         NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    request_date      TIMESTAMP(6) NOT NULL,
    start_date        TIMESTAMP(6),
    end_date          TIMESTAMP(6),
    super_teacher_id  UUID,
    teacher_offer_id  BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE student_offer_requests
(
    id                BIGINT       NOT NULL,
    payment_image_url TEXT         NOT NULL,
    status            VARCHAR(50)  NOT NULL,
    request_date      TIMESTAMP(6) NOT NULL,
    start_date        TIMESTAMP(6),
    end_date          TIMESTAMP(6),
    student_id        UUID,
    student_offer_id  BIGINT,
    PRIMARY KEY (id)
);

CREATE TABLE playlists
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    subject_id  BIGINT
);

-- V1: Create Item subclasses tables
CREATE TABLE videos
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    playlist_id BIGINT
);

CREATE TABLE fiches
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    playlist_id BIGINT
);

CREATE TABLE exercices
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    playlist_id BIGINT
);

CREATE TABLE corrections
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    playlist_id BIGINT
);

CREATE TABLE qcms
(
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    url         TEXT NOT NULL,
    is_completed BOOLEAN NOT NULL,
    playlist_id BIGINT
);

CREATE TABLE messages
(
    id          BIGINT PRIMARY KEY,
    content     TEXT         NOT NULL,
    timestamp   TIMESTAMP(6) NOT NULL,
    sender_id   UUID         NOT NULL,
    receiver_id UUID         NOT NULL
);

CREATE TABLE password_reset
(
    id BIGINT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL,
    expiry_at TIMESTAMP(6) NOT NULL
);

-- Create sequences
CREATE SEQUENCE role_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE group_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE offer_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE student_request_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE teacher_request_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE subject_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE section_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE item_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE playlist_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE event_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE message_sequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE password_reset_sequence START WITH 1 INCREMENT BY 1;



-- Create indexes to improve query performance

-- Indexes on foreign key columns
CREATE INDEX idx_users_role_id ON users (role_id);
CREATE INDEX idx_access_tokens_user_id ON access_tokens (user_id);
CREATE INDEX idx_confirmation_tokens_user_id ON confirmation_tokens (user_id);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_super_teachers_id ON super_teachers (id);
CREATE INDEX idx_teachers_id ON teachers (id);
CREATE INDEX idx_students_id ON students (id);
CREATE INDEX idx_groups_super_teacher_id ON groups (super_teacher_id);
CREATE INDEX idx_group_student_group_id ON group_student (group_id);
CREATE INDEX idx_group_student_student_id ON group_student (student_id);
CREATE INDEX idx_teacher_offer_requests_teacher_id ON teacher_offer_requests (super_teacher_id);
CREATE INDEX idx_teacher_offer_requests_teacher_offer_id ON teacher_offer_requests (teacher_offer_id);
CREATE INDEX idx_student_offer_requests_student_id ON student_offer_requests (student_id);
CREATE INDEX idx_student_offer_requests_student_offer_id ON student_offer_requests (student_offer_id);
CREATE INDEX idx_subjects_teacher_id ON subjects (teacher_id);
CREATE INDEX idx_subjects_group_id ON subjects (group_id);
CREATE INDEX idx_sections_subject_id ON sections (subject_id);
CREATE INDEX idx_videos_playlist_id ON videos (playlist_id);
CREATE INDEX idx_fiches_playlist_id ON fiches (playlist_id);
CREATE INDEX idx_exercices_playlist_id ON exercices (playlist_id);
CREATE INDEX idx_corrections_playlist_id ON corrections (playlist_id);
CREATE INDEX idx_qcms_playlist_id ON qcms (playlist_id);

-- Indexes on columns that are likely to be filtered, sorted, or joined frequently
CREATE INDEX idx_users_governorate ON users (governoarate);
CREATE INDEX idx_users_birthday ON users (birthday);
CREATE INDEX idx_teacher_offer_requests_status ON teacher_offer_requests (status);
CREATE INDEX idx_student_offer_requests_status ON student_offer_requests (status);
CREATE INDEX idx_students_education_level ON students (education_level);

-- Full-text or partial indexes (useful for searches)
CREATE INDEX idx_users_email_trgm ON users USING gin (email gin_trgm_ops); -- Trigram index for fast text search on email
CREATE INDEX idx_users_phone_number_trgm ON users USING gin (phone_number gin_trgm_ops); -- Trigram index for fast text search on phone number

-- Additional unique indexes (if not covered by constraints)
CREATE UNIQUE INDEX idx_roles_name ON roles (name); -- Already unique by constraint, but ensure fast lookup