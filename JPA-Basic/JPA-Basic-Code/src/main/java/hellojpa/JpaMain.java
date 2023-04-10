package hellojpa;

import org.hibernate.Hibernate;

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
            Member member1 = new Member();
            member1.setUsername("member1");
            em.persist(member1);

            em.flush();
            em.clear();

            // 프록시 객체는 원본 엔티티를 상속받음. 따라서 타입 체크시 주의해야함(==비교 실패, 대신 instance of 사용)

            // 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환, 그리고 그 반대도 마찬가지 (== 비교 true 보장)
            Member refMember = em.getReference(Member.class, member1.getId());
            System.out.println("refMember.getClass() = " + refMember.getClass());  //Proxy
            Hibernate.initialize(refMember);    // 강제 초기화 (하이버네이트가 제공함. JPA표준은 강제 초기화 없음)


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void logic(Member m1, Member m2) {
        System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass()));
        // 어떤 로직이 있을 때 파라미터로 프록시객체가 넘어올지, 실제 엔티티가 넘어올지 모른다. 따라서 만약에 타입 비교를 해야한다면, == 비교하지말고, instanceof로 비교할 것.
        System.out.println("m1 == m2 : " + (m1 instanceof Member));
        System.out.println("m1 == m2 : " + (m2 instanceof Member));
    }
}
