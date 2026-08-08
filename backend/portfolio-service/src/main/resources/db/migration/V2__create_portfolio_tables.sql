CREATE TABLE portfolio.app_users (
    id UUID PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE portfolio.portfolios (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    cash_balance NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_portfolios_user
        FOREIGN KEY (user_id)
        REFERENCES portfolio.app_users(id),

    CONSTRAINT ck_portfolios_cash_balance_non_negative
        CHECK (cash_balance >= 0)
);

CREATE TABLE portfolio.positions (
    id UUID PRIMARY KEY,
    portfolio_id UUID NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    average_price NUMERIC(19, 6) NOT NULL,
    last_price NUMERIC(19, 6),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_positions_portfolio
        FOREIGN KEY (portfolio_id)
        REFERENCES portfolio.portfolios(id),

    CONSTRAINT uq_positions_portfolio_symbol
        UNIQUE (portfolio_id, symbol),

    CONSTRAINT ck_positions_quantity_non_negative
        CHECK (quantity >= 0),

    CONSTRAINT ck_positions_average_price_non_negative
        CHECK (average_price >= 0),

    CONSTRAINT ck_positions_last_price_positive
        CHECK (last_price IS NULL OR last_price > 0)
);
