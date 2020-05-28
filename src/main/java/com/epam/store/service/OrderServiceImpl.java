package com.epam.store.service;

import com.epam.store.dao.*;
import com.epam.store.entity.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class OrderServiceImpl implements OrderService {

    private static final String ORDER_MUST_NOT_BE_NULL = "Order must not be null";
    private static final String ORDER_CARD_MUST_NOT_BE_NULL = "Order card must not be null";
    private static final String ORDER_STATUS_MUST_NOT_BE_NULL = "Order status must not be null";
    public static final String ACCOUNT_ID_MUST_NOT_BE_NULL = "Account id must not be null";
    public static final String ORDER_CARD_ID_MUST_NOT_BE_NULL = "Order card Id must not be null";

    private OrderDAO orderDAO;
    private PhoneDAO phoneDAO;
    private OrderStatusHistoryDAO orderStatusHistoryDAO;
    private OrderCardDAO orderCardDAO;

    public OrderServiceImpl(OrderDAO orderDAO, PhoneDAO phoneDAO, OrderStatusHistoryDAO orderStatusHistoryDAO, OrderCardDAO orderCardDAO) {
        this.orderDAO = orderDAO;
        this.phoneDAO = phoneDAO;
        this.orderStatusHistoryDAO = orderStatusHistoryDAO;
        this.orderCardDAO = orderCardDAO;
    }

    @Override
    public List<Order> findAllByAccountId(Long accountId) {
        Objects.requireNonNull(accountId, ACCOUNT_ID_MUST_NOT_BE_NULL);
        return orderDAO.findAllByAccountId(accountId);
    }

    @Override
    public List<Order> findAll() {
        return orderDAO.findAll();
    }

    @Override
    public Optional<Order> findById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        return orderDAO.findById(id);
    }

    @Override
    public Order save(Order order) {
        Objects.requireNonNull(order, ORDER_MUST_NOT_BE_NULL);
        List<OrderCard> orderCards = orderCardDAO.findAllByOrderId(order.getId());
        if (order.getId() == null) {
            if (order.getStatus() == null) {
                addOrderStatus(order, OrderStatus.NOT_STARTED);
            }
            if (order.getStatus() == OrderStatus.NOT_STARTED) {
                if (orderCards.isEmpty()) {
                    return orderDAO.save(order);
                } else {
                    throw new IllegalArgumentException("New order cannot have order cards");
                }
            }
        } else {
            Objects.requireNonNull(order.getStatus(), ORDER_STATUS_MUST_NOT_BE_NULL);
            if (order.getStatus() != OrderStatus.NOT_STARTED && (orderCards.isEmpty())) {
                throw new IllegalArgumentException("Started order cannot have empty order cards but actual is " + orderCards);
            }
        }
        Account account = order.getAccount();
        if (order.getStatus() == OrderStatus.CANCEL) {
            cancelOrder(order, account, orderCards);
        } else {
            calculateAccountAmount(order, account);
        }
        return orderDAO.save(order);
    }

    private void addOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
        orderStatusHistory.setOrder(order);
        orderStatusHistory.setTimeStamp(LocalDateTime.now());
        orderStatusHistory.setOrderStatus(status);
        List<OrderStatusHistory> orderStatusHistoryList;
        if (order.getId() == null) {
            orderStatusHistoryList = new ArrayList<>();
        } else {
            orderStatusHistoryList = orderStatusHistoryDAO.findAllByOrderId(order.getId());
        }
        orderStatusHistoryList.add(orderStatusHistory);
        order.setOrderStatusHistoryList(orderStatusHistoryList);
    }

    private void cancelOrder(Order order, Account account, List<OrderCard> orderCards) {
        BigDecimal orderTotalSum = order.getTotalSum();
        account.setAmount(account.getAmount().add(orderTotalSum));
        for (OrderCard orderCard : orderCards) {
            Phone phone = orderCard.getPhone();
            phone.setCount(phone.getCount() + orderCard.getItemCount());
        }
        order.setOrderCards(orderCards);
        orderDAO.save(order);
    }

    private void calculateAccountAmount(Order order, Account account) {
        BigDecimal orderTotalSum = order.getTotalSum();
        BigDecimal accountAmount = account.getAmount();
        if (accountAmount.compareTo(orderTotalSum) < 0) {
            throw new IllegalArgumentException("Order's total sum must be less or equal to account's amount");
        }
        account.setAmount(account.getAmount().subtract(orderTotalSum));
        order.setTotalSum(orderTotalSum);
    }

    public void deleteOrderCard(Long orderId, Long orderCardId) {
        Objects.requireNonNull(orderId, ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(orderCardId, ORDER_CARD_ID_MUST_NOT_BE_NULL);
        Order order = orderDAO.findById(orderId).get();
        if (order.getStatus() != OrderStatus.NOT_STARTED) {
            throw new IllegalArgumentException("You can't delete order card from active order. Order status: " + order.getStatus());
        }
        Optional<OrderCard> orderCardOptional = orderCardDAO.findById(orderCardId);
        if (orderCardOptional.isPresent()) {
            OrderCard orderCard = orderCardOptional.get();
            calculateOrderTotalSum(orderCard, order, true);
            List<OrderCard> orderCards = orderCardDAO.findAllByOrderId(orderId);
            orderCards.removeIf((e) -> e.getId().equals(orderCardId));
            order.setOrderCards(orderCards);
            orderDAO.save(order);
        } else {
            throw new IllegalArgumentException("Order card with id =" + orderCardId + "is not found");
        }
    }

    public void addOrderCard(Long orderId, OrderCard orderCard) {
        Objects.requireNonNull(orderId, ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(orderCard, ORDER_CARD_MUST_NOT_BE_NULL);
        Order order = orderDAO.findById(orderId).get();
        if (order.getStatus() != OrderStatus.NOT_STARTED) {
            String errorMessage;
            if (orderCard.getId() != null) {
                errorMessage = "You can't update order card in active order. Order status: " + order.getStatus();
            } else {
                errorMessage = "You can't add order card to active order. Order status: " + order.getStatus();
            }
            throw new IllegalArgumentException(errorMessage);
        }
        calculateOrderTotalSum(orderCard, order, false);
        List<OrderCard> orderCards = orderCardDAO.findAllByOrderId(orderId);
        if (orderCard.getId() == null) {
            orderCards.add(orderCard);
        } else {
            Optional<OrderCard> cardOptional = orderCards.stream().filter((oc) -> oc.getId().equals(orderCard.getId())).findFirst();
            if (cardOptional.isPresent()) {
                OrderCard card = cardOptional.get();
                card.setOrder(orderCard.getOrder());
                card.setPhone(orderCard.getPhone());
                card.setItemCount(orderCard.getItemCount());
            } else {
                throw new IllegalArgumentException("Order card with id =" + orderCard.getId() + "is not found");
            }
        }
        order.setOrderCards(orderCards);
        orderDAO.save(order);
    }

    private void calculateOrderTotalSum(OrderCard orderCard, Order order, boolean isDelete) {
        BigDecimal orderTotalSum = order.getTotalSum();
        if (orderTotalSum == null) {
            orderTotalSum = BigDecimal.ZERO;
            BigDecimal phonePrice;
            Long itemCount;
            List<OrderCard> orderCards = orderCardDAO.findAllByOrderId(order.getId());
            for (OrderCard card : orderCards) {
                itemCount = card.getItemCount();
                phonePrice = card.getPhone().getPrice();
                orderTotalSum = orderTotalSum.add(phonePrice.multiply(BigDecimal.valueOf(itemCount)));
            }
        }
        Phone phone = orderCard.getPhone();
        BigDecimal phonePrice = phone.getPrice();
        Long itemCount = orderCard.getItemCount();
        Long phoneCount = phone.getCount();
        if (phoneCount < itemCount) {
            throw new IllegalArgumentException("Item count must be less or equal to phone count. Phone count: " + phoneCount + ". Item count: " + itemCount);
        }
        if (!isDelete) {
            phone.setCount(phoneCount - itemCount);
            order.setTotalSum(orderTotalSum.add(phonePrice.multiply(BigDecimal.valueOf(itemCount))));
        } else {
            phone.setCount(phoneCount + itemCount);
            order.setTotalSum(orderTotalSum.subtract(phonePrice.multiply(BigDecimal.valueOf(itemCount))));
        }
    }

    @Override
    public void deleteById(Long id) {
        Objects.requireNonNull(id, ID_MUST_NOT_BE_NULL);
        Optional<Order> orderOptional = findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus() == OrderStatus.PROCESSING) {
                throw new IllegalArgumentException("Cannot delete order because it has processing status, order id=" + order.getId());
            }
        }
        orderDAO.deleteById(id);
    }
}