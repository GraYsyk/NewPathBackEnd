package com.graysenko.NewPathBackEnd.Services.Admin;

import com.graysenko.NewPathBackEnd.DTOs.Stats.BestsellerDTO;
import com.graysenko.NewPathBackEnd.DTOs.Stats.IncomePointDTO;
import com.graysenko.NewPathBackEnd.DTOs.Stats.MonthlyGrowthDTO;
import com.graysenko.NewPathBackEnd.DTOs.Stats.OrderStatusDTO;
import com.graysenko.NewPathBackEnd.Entities.Item.Item;
import com.graysenko.NewPathBackEnd.Entities.Order.Order;
import com.graysenko.NewPathBackEnd.Repositories.Order.OrderRepository;
import com.graysenko.NewPathBackEnd.Services.Item.ItemService;
import com.graysenko.NewPathBackEnd.Services.Order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AdminService {
    private final ItemService itemService;
    private final OrderRepository orderRepository;

    //BESTSELLER SECTION

    public BestsellerDTO getBestseller() {
        List<Object[]> result = orderRepository.findBestsellerItemId();
        if (result.isEmpty()) return null;

        Object[] bestseller = result.getFirst();
        Long itemId = ((Number) bestseller[0]).longValue();
        Long totalSold = ((Number) bestseller[1]).longValue();

        Item item = itemService.findById(itemId).orElse(null);
        if (item == null) return null;

        return new BestsellerDTO(
                itemId,
                item.getName(),
                item.getFrontImage(),
                totalSold
        );
    }

    //ORDERS STATUS SECTION

    public List<OrderStatusDTO> getOrderStatus() {
        List<Object[]> results = orderRepository.countByOrderStatus();

        return results.stream()
                .map(o -> new OrderStatusDTO(
                        o[0].toString(),((Number) o[1]).longValue()
                )).toList();
    }

    //MONTHLY GROWTH SECTION

    public List<MonthlyGrowthDTO> getMonthlyGrowth() {
        int year = LocalDate.now().getYear();
        List<Object[]> results = orderRepository.countOrdersByMonth(year);

        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        Map<Integer, Long> map = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put(i, 0L);

        results.forEach(row -> {
            int month = ((Number) row[0]).intValue();
            long count = ((Number) row[1]).longValue();
            map.put(month, count);
        });

        return map.entrySet().stream()
                .map(e -> new MonthlyGrowthDTO(
                        months[e.getKey() - 1], e.getValue()
                )).toList();
    }

    //LAST INCOME SECTION

    public List<IncomePointDTO> getIncome(String period) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp from;

        switch(period) {
            case "day" -> from = new Timestamp(now.getTime() - 24L * 60 * 60 * 1000);
            case "week" -> from = new Timestamp(now.getTime() - 7 * 24 * 60 * 60 * 1000);
            case "month" -> from = new Timestamp(now.getTime() - 30L * 24 * 60 * 60 * 1000);
            case "year" -> from = new Timestamp(now.getTime() - 365L * 24 * 60 * 60 * 1000);
            default -> from = new Timestamp(now.getTime() - 7L * 24 * 60 * 60 * 1000);
        }

        List<Order> orders = orderRepository.findAllByOrderDateBetween(from, now);

        return switch (period) {
            case "day" -> groupByHour(orders);
            case "week" -> groupByDay(orders, 7);
            case "month" -> groupByDay(orders, 30);
            case "year" -> groupByMonth(orders);
            default -> groupByDay(orders, 7);
        };
    }

    private List<IncomePointDTO> groupByHour(List<Order> orders) {
        Map<Integer, Double> map = new LinkedHashMap<>();
        for(int i=0; i < 24; i++) map.put(i, 0.0);

        orders.forEach(order -> {
            int hour = order.getOrderDate().toLocalDateTime().getHour();
            map.merge(hour, order.getTotalAmount(), Double::sum);
        });

        return map.entrySet().stream()
                .map(e -> new IncomePointDTO(
                        e.getKey() + ":00", e.getValue()))
                .toList();
    }

    private List<IncomePointDTO> groupByDay(List<Order> orders, int days) {
        Map<String, Double> map = new LinkedHashMap<>();
        LocalDate today =  LocalDate.now();

        for(int i = days - 1; i >= 0; i--) map.put(today.minusDays(i).toString(), 0.0);

        orders.forEach(order -> {
            String day = order.getOrderDate().toLocalDateTime().toLocalDate().toString();
            map.merge(day, order.getTotalAmount(), Double::sum);
        });

        return map.entrySet().stream()
                .map(e -> new IncomePointDTO(
                        e.getKey(), e.getValue()
                )).toList();
    }

    private List<IncomePointDTO> groupByMonth(List<Order> orders) {
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        Map<Integer, Double> map = new LinkedHashMap<>();

        for (int i = 1; i <= 12; i++) {
            map.put(i, 0.0);
        }

        orders.forEach(order -> {
            int month = order.getOrderDate().toLocalDateTime().getMonthValue();
            map.merge(month, order.getTotalAmount(), Double::sum);
        });

        return map.entrySet().stream()
                .map(e -> new IncomePointDTO(
                        months[e.getKey() - 1], e.getValue()
                )).toList();
    }
}
