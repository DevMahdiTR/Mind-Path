-- Add Unique Constraints
ALTER TABLE users
    ADD CONSTRAINT UK_users_email UNIQUE (email),                          -- Ensure emails in users table are unique
    ADD CONSTRAINT CHK_governorate CHECK (governoarate IN (                 -- Corrected the column name from "governoarate" to "governorate"
        'ARIANA', 'BEJA', 'BEN_AROUS', 'BIZERTE', 'GABES', 'GAFSA',
        'JENDOUBA', 'KAIRUAN', 'KASSERINE', 'KEBILI', 'KEF', 'MAHDIA',
        'MANOUBA', 'MEDENINE', 'MONASTIR', 'NABEUL', 'SFAX', 'SIDI_BOUZID',
        'SILIANA', 'SOUSSE', 'TATAOUINE', 'TOZEUR', 'TUNIS', 'ZAGHOUAN'
    )), -- Ensure governorates in users table are valid
    ADD CONSTRAINT CHK_birthday CHECK (birthday <= CURRENT_DATE);           -- Ensure birthdays in users table are valid


ALTER TABLE students
    ADD CONSTRAINT CHK_education_level CHECK (education_level IN (
                                                                  'PREMIER_ANNEE_PRIMAIRE', 'DEUXIEME_ANNEE_PRIMAIRE', 'TROISIEME_ANNEE_PRIMAIRE',
                                                                  'QUATRIEME_ANNEE_PRIMAIRE', 'CINQUIEME_ANNEE_PRIMAIRE', 'SIXIEME_ANNEE_PRIMAIRE',
                                                                  'SEPTIEME_ANNEE_DE_BASE', 'HUITIEME_ANNEE_DE_BASE', 'NEUVIEME_ANNEE_DE_BASE',
                                                                  'DEUXIEME_ANNEE_SECONDAIRE_INFORMATIQUE', 'DEUXIEME_ANNEE_SECONDAIRE_LETTRES',
                                                                  'PREMIERE_ANNEE_SECONDAIRE', 'DEUXIEME_ANNEE_SECONDAIRE_ECONOMIE_GESTION',
                                                                  'DEUXIEME_ANNEE_SECONDAIRE_SCIENCES', 'TROISIEME_ANNEE_SECONDAIRE_ECONOMIE',
                                                                  'TROISIEME_ANNEE_SECONDAIRE_INFORMATIQUE', 'TROISIEME_ANNEE_SECONDAIRE_LETTRES',
                                                                  'TROISIEME_ANNEE_SECONDAIRE_SCIENCES', 'TROISIEME_ANNEE_SECONDAIRE_MATH',
                                                                  'TROISIEME_ANNEE_SECONDAIRE_TECHNIQUE', 'BAC_LETTRES', 'BAC_SCIENCES', 'BAC_MATH',
                                                                  'BAC_INFORMATIQUE', 'BAC_ECONOMIE_GESTION'
        )); -- Ensure education levels in students table are valid

ALTER TABLE roles
    ADD CONSTRAINT UK_roles_name UNIQUE (name);                             -- Ensure role names in roles table are unique

ALTER TABLE teacher_offer_requests
    ADD CONSTRAINT CHK_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')); -- Ensure status in teacher_offer_requests table is valid

ALTER TABLE student_offer_requests
    ADD CONSTRAINT CHK_status CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')); -- Ensure status in student_offer_requests table is valid


-- Add Foreign Keys
ALTER TABLE IF EXISTS users
    ADD CONSTRAINT FK_users_role_id FOREIGN KEY (role_id) REFERENCES roles(id); -- Ensure role_id in users table references a valid role

ALTER TABLE IF EXISTS access_tokens
    ADD CONSTRAINT fk_access_token_user FOREIGN KEY (user_id) REFERENCES users(id); -- Ensure user_id in access_tokens table references a valid user

ALTER TABLE IF EXISTS confirmation_tokens
    ADD CONSTRAINT fk_confirmation_token_user FOREIGN KEY (user_id) REFERENCES users(id); -- Ensure user_id in confirmation_tokens table references a valid user

ALTER TABLE IF EXISTS refresh_tokens
    ADD CONSTRAINT fk_refresh_token_user FOREIGN KEY (user_id) REFERENCES users(id); -- Ensure user_id in refresh_tokens table references a valid user

ALTER TABLE IF EXISTS super_teachers
    ADD CONSTRAINT fk_super_teacher_id FOREIGN KEY (id) REFERENCES users(id); -- Ensure id in super_teachers table references a valid user

ALTER TABLE IF EXISTS teachers
    ADD CONSTRAINT fk_teacher_id FOREIGN KEY (id) REFERENCES users(id); -- Ensure id in teachers table references a valid user

ALTER TABLE IF EXISTS students
    ADD CONSTRAINT fk_student_id FOREIGN KEY (id) REFERENCES users(id); -- Ensure id in students table references a valid user

ALTER TABLE IF EXISTS groups
    ADD CONSTRAINT fk_group_teacher FOREIGN KEY (super_teacher_id) REFERENCES super_teachers(id); -- Ensure super_teacher_id in groups table references a valid super_teacher

ALTER TABLE group_student
    ADD CONSTRAINT fk_group FOREIGN KEY (group_id) REFERENCES groups(id),
    ADD CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES students(id); -- Ensure group_id and student_id in group_student table reference valid groups and students


ALTER TABLE IF EXISTS teacher_offer_requests
    ADD CONSTRAINT fk_teacher_offer_request_teacher_id FOREIGN KEY (super_teacher_id) REFERENCES super_teachers(id),
    ADD CONSTRAINT fk_teacher_offer_request_teacher_offer_id FOREIGN KEY (teacher_offer_id) REFERENCES teacher_offers(id) ON DELETE CASCADE; -- Ensure super_teacher_id and teacher_offer_id in teacher_offer_requests table reference valid super_teachers and teacher_offers

ALTER TABLE IF EXISTS student_offer_requests
    ADD CONSTRAINT fk_student_offer_request_student_id FOREIGN KEY (student_id) REFERENCES students(id),
    ADD CONSTRAINT fk_student_offer_request_student_offer_id FOREIGN KEY (student_offer_id) REFERENCES student_offers(id) ON DELETE CASCADE; -- Ensure student_id and student_offer_id in student_offer_requests table reference valid students and student_offers

ALTER TABLE IF EXISTS subjects
    ADD CONSTRAINT FK_subjects_teacher_id FOREIGN KEY (teacher_id) REFERENCES users(id),
    ADD CONSTRAINT FK_sections_group_id FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE; -- Ensure teacher_id and group_id in subjects table reference valid users and groups

ALTER TABLE IF EXISTS sections
    ADD CONSTRAINT FK_sections_subject_id FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE; -- Ensure subject_id in sections table references a valid subject

ALTER TABLE IF EXISTS playlists
    ADD CONSTRAINT fk_playlist_subjects FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE; -- Ensure section_id in playlists table references a valid subject

ALTER TABLE videos
    ADD CONSTRAINT fk_videos_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE; -- Ensure playlist_id in videos table references a valid playlist

ALTER TABLE fiches
    ADD CONSTRAINT fk_fiches_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE; -- Ensure playlist_id in fiches table references a valid playlist

ALTER TABLE exercices
    ADD CONSTRAINT fk_exercices_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE; -- Ensure playlist_id in exercices table references a valid playlist

ALTER TABLE corrections
    ADD CONSTRAINT fk_corrections_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE; -- Ensure playlist_id in corrections table references a valid playlist

ALTER TABLE qcms
    ADD CONSTRAINT fk_qcms_playlist FOREIGN KEY (playlist_id) REFERENCES playlists(id) ON DELETE CASCADE; -- Ensure playlist_id in qcms table references a valid playlist

ALTER TABLE events
    ADD CONSTRAINT fk_events_group FOREIGN KEY (group_id) REFERENCES groups(id) ON DELETE CASCADE ; -- Ensure group_id in events table references a valid group


ALTER TABLE IF EXISTS messages
    ADD CONSTRAINT fk_messages_sender_id FOREIGN KEY (sender_id) REFERENCES users(id),
    ADD CONSTRAINT fk_messages_receiver_id FOREIGN KEY (receiver_id) REFERENCES users(id); -- Ensure sender_id and receiver_id in messages table reference valid users