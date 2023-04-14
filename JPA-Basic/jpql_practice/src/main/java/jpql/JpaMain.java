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

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);


            Member member1 = new Member();
            member1.setUsername("회원1");
            member1.setTeam(teamA);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("회원2");
            member2.setTeam(teamA);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("회원3");
            member3.setTeam(teamB);
            em.persist(member3);

            em.flush();
            em.clear();

            String query = "select m From Member m";

            List<Member> result = em.createQuery(query, Member.class)
                    .getResultList();

            for (Member member : result) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());}
                // 회원1, 팀A(SQL)
                // 회원2, 팀A(1차 캐시)
                // 회원3, 팀B(SQL)

                // 최악의 경우 회원 100명 -> N + 1 이때 1은 첫번째에 회원을 가져오기 위해 날린 쿼리

            // 위의 문제를 푸는 방법 -> fetch join
            //language=JPAQL
            String query2 = "select m From Member m join fetch m.team";
            // join fetch로 인해 처음에 team까지 select쿼리를 날려서 영속성 컨텍스트에 엔티티 저장

            List<Member> result2 = em.createQuery(query2, Member.class)
                    .getResultList();

            for (Member member : result2) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getName());}


            // --------------- 컬렉션 페치 조인
            String query3 = "select t From Team t join fetch t.members";

            List<Team> result3 = em.createQuery(query3, Team.class)
                    .getResultList();

            for (Team team : result3) {
                System.out.println("team.getName = " + team.getName() +"|members = " +  team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }
            // 원하는 데로 데이터는 가져왔는데 문제가 있다. 일대다 조인은 데이터가 뻥튀기 될 수 있다.
            // TeamA의 입장에서는 하나지만, member가 2명 이었기 때문에 join을 하면, row가 2개로 만들어지는데,
            // JPA는 이시점에 row가 중복으로 만들어졌는지 알수 없다.
            // 그래서 DB에서 가져온대로 쓴다.
            // 이것이 객체와 RDB의 차이다. 객체의 입장에서는 미리 알 수 있는 것이 아니다.
            // 같은 영속성 컨텍스트에 올라간 하나가 중복되서 사용되는 것이다. (페치 조인 1 - 기본 17분 부분 참고)
            // 사용자에게 결정을 맡기는 것. JPA는 DB에서 나온 결과 수 만큼 돌려주는 것임.
            // 중복 제거는 DISTINCT로 제거하면 된다.

            // --------------- 컬렉션 페치 조인(중복 제거)
            String query4 = "select DISTINCT t From Team t join fetch t.members";

            List<Team> result4 = em.createQuery(query4, Team.class)
                    .getResultList();

            for (Team team : result4) {
                System.out.println("team.getName = " + team.getName() +"|members = " +  team.getMembers().size());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
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
