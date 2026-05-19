package com.graysenko.NewPathBackEnd.Repositories.Order;

import com.graysenko.NewPathBackEnd.Entities.Order.Order;
import com.graysenko.NewPathBackEnd.Entities.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findAllByUser(User user);

    // Income
    @Query("SELECT o FROM Order o WHERE o.orderDate >= :from AND o.orderDate <= :to")
    List<Order> findAllByOrderDateBetween(
            @Param("from") Timestamp from,
            @Param("to") Timestamp to
    );

    @Query("SELECT o.orderStatus, COUNT(o) FROM Order o GROUP BY o.orderStatus")
    List<Object[]> countByOrderStatus();

    //Month (native SQL)
    @Query(value = "SELECT EXTRACT(MONTH FROM order_date) as month, COUNT(*) as count " +
            "FROM orders WHERE EXTRACT(YEAR FROM order_date) = :year " +
            "GROUP BY month ORDER BY month", nativeQuery = true)
    List<Object[]> countOrdersByMonth(@Param("year") int year);

    //Bestseller (native)
    @Query(value = "SELECT item_id, SUM(quantity) as total_sold FROM order_items GROUP BY item_id ORDER BY total_sold DESC LIMIT 1", nativeQuery = true)
    List<Object[]> findBestsellerItemId();
}
