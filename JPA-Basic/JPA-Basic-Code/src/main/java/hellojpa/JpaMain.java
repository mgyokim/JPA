package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        // EntityManagerFactory DB당 1개(하나만 생성에서 애플리케이션 전체에서 공유)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // EntityManager는 고객의 요청당 하나씩 썻다가 버렸다가 함. (쓰레드간에 공유X, 사용하고 버려야 한다.)
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Address address = new Address("city", "street", "10000");

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setHomeAddress(address);
            em.persist(member1);

//            member1.getHomeAddress().setCity("NewCity"); // setter에서 private으로 생성자를 만들었기 때문에 수정 불가(immutable Object)로 설계

            // 만약에 member1의 city값을 "NewCity"로 바꾸고 싶으면, 즉 immutalbe Object의 값을 변경하고 싶다면 객체를 새로 만들어서 하자
            Address newAddress = new Address("NewCity", address.getStreet(), address.getZipcode());
            member1.setHomeAddress(newAddress); // member1의 Address값을 완전히 통으로 갈아 끼워야 한다. - 불변이라는 작은 제약으로 부작용이라는 큰 재앙을 막을 수 있다.

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
