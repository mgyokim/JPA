package jbabook.jpashop.service;

import jbabook.jpashop.domain.Delivery;
import jbabook.jpashop.domain.Member;
import jbabook.jpashop.domain.Order;
import jbabook.jpashop.domain.OrderItem;
import jbabook.jpashop.domain.item.Item;
import jbabook.jpashop.repository.ItemRepository;
import jbabook.jpashop.repository.MemberRepository;
import jbabook.jpashop.repository.OrderRepository;
import jbabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);    // Order의 orderItems와 delivery의 옵션을 cascade = CascadeType.ALL로 주었기 때문에, Order가 persisst될 때, orderItems와 delivery도 persist된다.

        return order.getId();

    }

    /**
     * 주문 취소
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancle();
    }


    /**
     * 검색
     */
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByCriteria(orderSearch);
    }
}
