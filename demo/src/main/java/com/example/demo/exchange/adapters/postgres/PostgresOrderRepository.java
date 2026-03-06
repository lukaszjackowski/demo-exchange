package com.example.demo.exchange.adapters.postgres;

import com.example.demo.exchange.domain.model.Asset;
import com.example.demo.exchange.domain.model.ClientOrderId;
import com.example.demo.exchange.domain.model.Order;
import com.example.demo.exchange.domain.model.OrderId;
import com.example.demo.exchange.domain.model.OrderRepository;
import com.example.demo.exchange.domain.model.OrderStatus;
import com.example.demo.exchange.domain.model.Price;
import com.example.demo.exchange.domain.model.Quantity;
import com.example.demo.exchange.domain.model.Side;
import com.example.demo.exchange.domain.model.UserId;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PostgresOrderRepository implements OrderRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PostgresOrderRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void updateAll(List<Order> orders) {
        String sql = """
            INSERT INTO orders (id, client_order_id, user_id, side, price, quantity, asset, remaining_quantity, status)
            VALUES (:id, :client_order_id, :user_id, :side, :price, :quantity, :asset, :remaining_quantity, :status)
            ON CONFLICT(client_order_id)
            DO UPDATE SET
                user_id = EXCLUDED.user_id,
                client_order_id = EXCLUDED.client_order_id,
                side = EXCLUDED.side,
                price = EXCLUDED.price,
                quantity = EXCLUDED.quantity,
                asset = EXCLUDED.asset,
                remaining_quantity = EXCLUDED.remaining_quantity,
                status = EXCLUDED.status
            """;

        var params = orders.stream().map(this::fromOrder).toArray(MapSqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, params);
    }

    @Override
    public List<Order> getOrders(UserId userId) {
        var params = new MapSqlParameterSource("user_id", userId.value());

        return namedParameterJdbcTemplate.query(
                    """
                        select id, client_order_id, user_id, side, price, quantity, asset, remaining_quantity, status
                        from orders
                        where user_id = :user_id
                    """,
                params,
                (rs, _) ->  fromResultSet(rs)
        );
    }

    private MapSqlParameterSource fromOrder(Order order) {
        return new MapSqlParameterSource()
                .addValue("id", order.getId().value())
                .addValue("client_order_id", order.getClientOrderId().value())
                .addValue("user_id", order.getUserId().value())
                .addValue("side", order.getSide().name())
                .addValue("price", order.getPrice().amount())
                .addValue("quantity", order.getQuantity().value())
                .addValue("asset", order.getAsset().name())
                .addValue("remaining_quantity", order.getRemainingQuantity().value())
                .addValue("status", order.getStatus().name());
    }

    private Order fromResultSet(ResultSet rs) throws SQLException {
        return new Order(
                new OrderId(rs.getString("id")),
                new ClientOrderId(rs.getString("client_order_id")),
                new UserId(rs.getString("user_id")),
                Side.valueOf(rs.getString("side")),
                new Price(rs.getBigDecimal("price")),
                new Quantity(rs.getLong("quantity")),
                Asset.valueOf(rs.getString("asset")),
                new Quantity(rs.getLong("remaining_quantity")),
                OrderStatus.valueOf(rs.getString("status"))
        );
    }
}
