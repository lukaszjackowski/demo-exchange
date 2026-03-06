INSERT INTO events_journal (id, user_id, event_type, price, quantity, side, asset, client_order_id, created_at) VALUES
(1, 'userBob', 'ORDER_NEW', 0.500, 500, 'SELL', 'BTC_USD', 'IK-2', CURRENT_TIMESTAMP),
(2, 'userAlice', 'ORDER_NEW', 0.500, 100, 'BUY', 'BTC_USD', 'IK-1', CURRENT_TIMESTAMP);
