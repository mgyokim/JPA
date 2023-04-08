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
            // 영속 상태
            Member member = em.find(Member.class, 150L);
            member.setName("AAAAA");

            // 준영속(detach) 상태 commit 하면 아무일도 일어나지 않음
//            em.detach(member);

            // 엔티티 매니저 안에 있는 영속성 컨텍스트를 통째로 다 지워버리기
            em.clear();

            Member member2 = em.find(Member.class, 150L);   // 앞서 em.clear를 한 것 때문에 영속성 컨텍스트에 없어서 SELECT 쿼리가 한번 더 나감.

            System.out.println("=================================");
            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
