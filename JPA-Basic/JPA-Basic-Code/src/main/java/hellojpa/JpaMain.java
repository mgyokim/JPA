package hellojpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
            // JPQL 예시
//            List<Member> result = em.createQuery("select m from Member as m where m.username like '%kim%'", Member.class)
//                    .getResultList();
//
//            for (Member member : result) {
//                System.out.println("member = " + member);
//            }

        // Criteria 예시 (자바 코드로 짜기 때문에 오타가 나더라도 컴파일 오류로 파악 가능하고, 동적 쿼리를 짜기 좋다는 장점이 있지만, SQL 스럽지 않다는 단점이 있다.) - 근데 조건이 좀 늘어나면 SQL스럽지 않아서 알아보기 힘들어서 유지보수가 힘듦. 그래서 실무에서는 이거 말고 QueryDSL 사용하자.
            // 그냥 이런게 있다 정도로 알고가자.
            // Criteria 사용 준비
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Member> query = cb.createQuery(Member.class);

            Root<Member> m = query.from(Member.class);

            CriteriaQuery<Member> cq = query.select(m).where(cb.equal(m.get("username"), "kim"));
            em.createQuery(cq)
                    .getResultList();

            // QueryDSL은 설정 해야하지만 일단 코드만 비교용으로 작성했음
//            QMember m = QMember.member;
//            Lsit<Member> result = queryFactory
//                    .select(m)
//                    .from(m)
//                    .where(m.name.like("kim"))
//                    .fetch();

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
