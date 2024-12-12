package com.ss.service;

import com.ss.domain.OrderType;
import com.ss.model.Coin;
import com.ss.model.Order;
import com.ss.model.OrderItem;
import com.ss.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {

    Order createOrder(User user, OrderItem orderItem, OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrderOfUser(Long userId, OrderType orderType, String assetSymbol);

    @Transactional
    Order buyAsset(Coin coin, double quantity, User user) throws Exception;

    @Transactional
    Order sellAsset(Coin coin, double quantity, User user) throws Exception;

    Order processOrder(Coin coin, double quantity, OrderType orderType) throws Exception;


}
