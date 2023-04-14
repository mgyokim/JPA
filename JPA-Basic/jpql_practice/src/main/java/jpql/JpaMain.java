package jpql;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
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
            em.persist(team);

            Member member1 = new Member();
            member1.setUsername("관리자1");
            member1.setTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("관리자2");
            member2.setTeam(team);
            em.persist(member2);

            em.flush();
            em.clear();

            // 경로표현식 특징 - 상태필드
//            String query0 = "select m.username From Member m";
//            List<String> result0 = em.createQuery(query0, String.class)
//                    .getResultList();
//
//            for (String s : result0) {
//                System.out.println("s = " + s);
//            }


            // 경로 표현식 특징 - 단일 값 연관 경로(묵시적 내부 조인, 추가 탐색 가능)
//            String query1 = "select m.team From Member m";
//            List<Team> result1 = em.createQuery(query1, Team.class)
//                    .getResultList();
//
//            for (Team s : result1) {
//                System.out.println("s = " + s);
//            }

            // 경로 표현식 특징 - 컬렉션 값 연관 경로(묵시적 내부 조인, 추가탐색 불가능)
            String query2 = "select m.username From Team t join t.members m";
            List<Collection> result2 = em.createQuery(query2, Collection.class)
                    .getResultList();

            for (Object o : result2) {
                System.out.println("o = " + o);
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
