INSERT INTO portfolio.app_users (
    id,
    display_name
)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'PulseDesk Demo User'
);

INSERT INTO portfolio.portfolios (
    id,
    user_id,
    cash_balance
)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    100000.00
);
