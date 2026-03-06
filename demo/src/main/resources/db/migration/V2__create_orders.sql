CREATE TABLE orders (
    id VARCHAR(100) PRIMARY KEY,
    client_order_id VARCHAR(100) NOT NULL UNIQUE,
    user_id VARCHAR(100) NOT NULL,
    side VARCHAR(10) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    quantity BIGINT NOT NULL,
    asset VARCHAR(20) NOT NULL,
    remaining_quantity BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL
);

CREATE INDEX idx_orders_user_id ON orders(user_id);