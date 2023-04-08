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
//            저장
//            Member member = new Member();
//            member.setId(2L);
//            member.setName("HelloB");
//            em.persist(member);

//            저장한 것을 찾고 싶을 때
//            Member findMember = em.find(Member.class, 1L);
//            System.out.println("findMember.id = " + findMember.getId());
//            System.out.println("findMember.name = " + findMember.getName());

//            저장한 것을 삭제하고 싶을 때
//            em.remove(findMember);

//            저장한 것을 수정하고 싶을 때
//            findMember.setName("HelloJPA");

//            예를 들어서, 전체 회원을 조회하고 싶다면? (JPQL - 객체를 대상으로하는 객체지향 쿼리)
            List<Member> result = em.createQuery("select m from Member as m", Member.class)
//                    .setFirstResult(5)  // 페이징 하고싶을 때, "5번부터
//                    .setMaxResults(8)   // 8개 가져와" 라는 뜻
                    .getResultList();
            for (Member member : result) {
                System.out.println("member.name = " + member.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }
}
