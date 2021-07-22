INSERT INTO application_type (id, name)
    VALUES (1, 'Checking'),
           (2, 'Savings'),
           (3, 'Checking and Savings'),
           (4, 'Credit Card'),
           (5, 'Loan');

INSERT INTO application (id, application_type_id)
    VALUES (1, 3),
           (2, 1),
           (3, 2),
           (4, 3);

INSERT INTO application_applicant (applicant_id, application_id)
    VALUES (1, 1),
           (2, 1),
           (3, 2),
           (4, 3);
