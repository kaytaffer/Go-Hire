-- This script inserts initial data into the 'role', 'application_status',and 'person' tables

INSERT INTO role (role_id, name) VALUES (1, 'recruiter');
INSERT INTO role (role_id, name) VALUES (2, 'applicant');

INSERT INTO application_status (application_status_id, status) VALUES (1, 'accepted');
INSERT INTO application_status (application_status_id, status) VALUES (2, 'rejected');
INSERT INTO application_status (application_status_id, status) VALUES (3, 'unhandled');

INSERT INTO person (name, surname, pnr, email, password, username, role_id, application_status_id) VALUES
    ('validApplicantUser', 'validApplicantUser', '19909090-9090', 'email@kth.se', '$2a$10$ZVeEAO5d9j5SyUmUzIfiiuHE/UpKkeFrY5qut6lKUH3cGPBP1u2iq', 'validApplicantUser', 2, 3);
INSERT INTO person (name, surname, pnr, email, password, username, role_id, application_status_id) VALUES
    ('validRecruiterUser', 'validRecruiterUser', '19909090-9090', 'email@kth.se', '$2a$10$9u.byUPFFXkM.GRdzZ5.1eIXuag5fXDkPq3jjSvUh3Azrqt23k9/G', 'validRecruiterUser', 1, null);