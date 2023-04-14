package jpql;

import javax.persistence.*;
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



        try{

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member");
            member.setAge(10);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            String query = "select m from Member m inner join m.team t"; //  내부 조인, inner 생략 가능
            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            String query2 = "select m from Member m left outer join m.team t";  // 외부 조인, outer 생략 가능
            List<Member> resul2 = em.createQuery(query2, Member.class)
                    .getResultList();

            String query3 = "select m from Member m, Team t where m.username = t.name";  // 세타 조인
            List<Member> resul3 = em.createQuery(query3, Member.class)
                    .getResultList();


            String query4 = "select m from Member m left join m.team t  on t.name = 'teanA'";  // join ON 절 이용한 조회 대상 필터링
            List<Member> resul4 = em.createQuery(query4, Member.class)
                    .getResultList();

            String query5 = "select m from Member m join Team t on m.username = t.name";  // join ON 절 이용한 연관관계 없는 엔티티 내부 조인
            List<Member> resul5 = em.createQuery(query5, Member.class)
                    .getResultList();

            String query6 = "select m from Member m left join Team t on m.username = t.name";  // join ON 절 이용한 연관관계 없는 엔티티 외부 조인
            List<Member> resul6 = em.createQuery(query6, Member.class)
                    .getResultList();

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
