CREATE TABLE events_journal (
    id SERIAL PRIMARY KEY,
    user_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    price DECIMAL(18, 8),
    quantity DECIMAL(18, 8),
    side VARCHAR(10) NOT NULL,
    asset VARCHAR(30) NOT NULL,
    client_order_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);