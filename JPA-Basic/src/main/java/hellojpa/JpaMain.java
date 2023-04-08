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
            // 영속
//            Member findMember1 = em.find(Member.class, 99L);    // SELECT 쿼리는 한번만 나감
//            Member findMember2 = em.find(Member.class, 99L);    // 1차캐시에 저장해둔 영속 엔티티를 찾음
//
//            System.out.println("result = " + (findMember1 == findMember2)); // result = true영속성 엔티티의 동일성 보장

            // 트랜잭션을 지원하는 쓰기 지연
//            Member member1 = new Member(150L, "A");
//            Member member2 = new Member(160L, "B");
//
//            em.persist(member1);
//            em.persist(member2);
//
//            System.out.println("=============================");    // 쿼리 나가는 거 보기 위한 선
            // 변경 감지를 통한 엔티티 수정 - 플러시 (엔티티와 스탭샷 비교)
            Member member = em.find(Member.class, 150L);
            member.setName("ZZZZZ");

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
