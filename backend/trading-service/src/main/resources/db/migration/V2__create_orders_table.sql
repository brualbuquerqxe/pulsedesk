CREATE TABLE trading.orders (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    symbol VARCHAR(10) NOT NULL,
    side VARCHAR(10) NOT NULL,
    quantity BIGINT NOT NULL,
    status VARCHAR(10) NOT NULL,
    price NUMERIC(18, 6),
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP

);
