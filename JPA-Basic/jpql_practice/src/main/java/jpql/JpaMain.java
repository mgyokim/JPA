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
            member.setUsername("관리자");
            member.setAge(10);
            member.setType(MemberType.ADMIN);

            member.setTeam(team);

            em.persist(member);

            em.flush();
            em.clear();

            // 기본 CASE 식
            String query = "select " +
                                "case when m.age <= 10 then '학생요금'" +
                                "     when m.age >= 60 then '경로요금'" +
                                "     else '일반요금'" +
                                "end " +
                           "from Member m";
            List<String> result = em.createQuery(query, String.class)
                    .getResultList();

            for (String s : result) {
                System.out.println("s = " + s);
            }
            
            // COALESCE : 하나씩 조회해서 null이 아니면 반환
            String query2 = "select coalesce(m.username, '이름 없는 회원') from Member m";
            List<String> result2 = em.createQuery(query2, String.class)
                    .getResultList();

            for (String s : result2) {
                System.out.println("s = " + s);
            }

            // NULLIF : 두 값이 같으면 null 반환, 다르면 첫번째 값 반환
            String query3 = "select nullif(m.username, '관리자') as username " +
                    "from Member m";
            List<String> result3 = em.createQuery(query3, String.class)
                    .getResultList();

            for (String s : result3) {
                System.out.println("s = " + s);
            }



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
