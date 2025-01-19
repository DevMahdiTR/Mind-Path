-- Version: V3
-- Description: Insert initial roles and users

INSERT INTO roles (id, name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_SUPER_TEACHER'),
       (3, 'ROLE_TEACHER'),
       (4, 'ROLE_STUDENT');


INSERT INTO users (id, full_name, phone_number, email, password, is_enabled, governoarate, birthday, created_at, updated_at, role_id,user_type)
VALUES  ('36a4dac0-daaa-456e-b030-b71dacea3716', 'Foknje7ik', '22946781', 'medmahdidev@gmail.com', '$2a$10$Su4L9/48L3ZTTi1VUwCwNevVjuhVCftxHCIXmkhYLuNRXB/UhijBa', true, 'TUNIS', '2001-01-01', '2024-01-01 00:00:00', '2024-01-01 00:00:00', 1,'SUPERTEACHER');

INSERT INTO super_teachers(id) VALUES ('36a4dac0-daaa-456e-b030-b71dacea3716');

























