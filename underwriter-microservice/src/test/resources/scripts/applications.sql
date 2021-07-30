INSERT INTO application (id, application_type, primary_applicant_id, application_status)
    VALUES (1, 'CHECKING_AND_SAVINGS', 1, 'APPROVED'),
           (2, 'CHECKING', 3, 'DENIED'),
           (3, 'SAVINGS', 4, 'PENDING'),
           (4, 'CREDIT_CARD', 3, 'APPROVED');

INSERT INTO application_applicant (applicant_id, application_id)
    VALUES (1, 1),
           (2, 1),
           (3, 2),
           (4, 3);
