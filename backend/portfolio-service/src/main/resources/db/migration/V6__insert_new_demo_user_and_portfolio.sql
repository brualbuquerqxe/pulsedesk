INSERT INTO portfolio.app_users (
    id,
    display_name
)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    'PulseDesk Demo User'
)
ON CONFLICT DO NOTHING;

INSERT INTO portfolio.portfolios (
    id,
    user_id,
    cash_balance
)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    '33333333-3333-3333-3333-333333333333',
    100000.00
)
ON CONFLICT DO NOTHING;
