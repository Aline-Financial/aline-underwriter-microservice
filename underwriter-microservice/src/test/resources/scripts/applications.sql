INSERT INTO application_type (id, name)
    VALUES (1, 'Checking'),
           (2, 'Savings'),
           (3, 'Checking and Savings'),
           (4, 'Credit Card'),
           (5, 'Loan');

INSERT INTO application_status (id, name)
    VALUES (1, 'Approved'),
           (2, 'Denied'),
           (3, 'Pending');

INSERT INTO application (id, application_type_id, application_status_id, primary_applicant_id)
    VALUES (1, 3, 1, 1),
           (2, 1, 3, 3),
           (3, 2, 1, 4),
           (4, 3, 2, 2);

INSERT INTO application_applicant (applicant_id, application_id)
    VALUES (1, 1),
           (2, 1),
           (3, 2),
           (4, 3);
