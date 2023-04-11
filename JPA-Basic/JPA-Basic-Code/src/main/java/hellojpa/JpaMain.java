package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        // EntityManagerFactory DB당 1개(하나만 생성에서 애플리케이션 전체에서 공유)
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        // EntityManager는 고객의 요청당 하나씩 썻다가 버렸다가 함. (쓰레드간에 공유X, 사용하고 버려야 한다.)
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "12314"));

            member.getFavoriteFoods().add("닭가슴살");
            member.getFavoriteFoods().add("연어");
            member.getFavoriteFoods().add("비타민");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "12314"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "12314"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("==============START================");
            Member findMember = em.find(Member.class, member.getId()); // 쿼리를 확인해보면, member만 select한다. 즉, 값 타입 컬렉션은 지연로딩 이라는 의미.
            System.out.println("===================================");

            // homeCity -> newCity
//            Address a = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));


            // 닭가슴살 -> 한식 (컬렉션 값 타입의 변경, 여기서 알 수 있는 것은, 컬렉션의 값만 변경해도 실제 데이터베이스에 쿼리가 나가면서 JPA가 알아서 바꿔준다.)
//            findMember.getFavoriteFoods().remove("닭가슴살");
//            findMember.getFavoriteFoods().add("한식");

            // 주소변경
//            findMember.getAddressHistory().remove(new Address("old1", "street", "12314")); // 기본적으로 컬렉션들은 대부분 이런 것을 찾을 때 equals를 사용한다. 그래서 equals나 hashCode가 제대로 구현되어 있지 않으면 문제가생긴다. 잘 만들어 놓자.(오버라이딩을 하든..)
//            findMember.getAddressHistory().add(new Address("newCity1", "street", "10000"));


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
