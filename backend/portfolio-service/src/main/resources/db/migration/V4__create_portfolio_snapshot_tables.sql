CREATE TABLE portfolio.daily_portfolio_snapshots (
    id UUID PRIMARY KEY,

    portfolio_id UUID NOT NULL,

    snapshot_date DATE NOT NULL,

    cash_balance NUMERIC(19, 2) NOT NULL,

    total_value NUMERIC(19, 2) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_daily_portfolio_snapshots_portfolio
        FOREIGN KEY (portfolio_id)
        REFERENCES portfolio.portfolios(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_daily_portfolio_snapshots_portfolio_date
        UNIQUE (portfolio_id, snapshot_date),

    CONSTRAINT ck_daily_portfolio_snapshots_cash_balance_non_negative
        CHECK (cash_balance >= 0),

    CONSTRAINT ck_daily_portfolio_snapshots_total_value_non_negative
        CHECK (total_value >= 0)
);


CREATE TABLE portfolio.position_snapshots (
    id UUID PRIMARY KEY,

    daily_snapshot_id UUID NOT NULL,

    symbol VARCHAR(10) NOT NULL,

    quantity BIGINT NOT NULL,

    close_price NUMERIC(19, 6) NOT NULL,

    market_value NUMERIC(19, 6) NOT NULL,

    CONSTRAINT fk_position_snapshots_daily_snapshot
        FOREIGN KEY (daily_snapshot_id)
        REFERENCES portfolio.daily_portfolio_snapshots(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_position_snapshots_daily_snapshot_symbol
        UNIQUE (daily_snapshot_id, symbol),

    CONSTRAINT ck_position_snapshots_quantity_non_negative
        CHECK (quantity >= 0),

    CONSTRAINT ck_position_snapshots_close_price_positive
        CHECK (close_price > 0),

    CONSTRAINT ck_position_snapshots_market_value_non_negative
        CHECK (market_value >= 0)
);
