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

            // 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.changeTeam(team);   // *** Member는 연관관계 주인이므로 업데이트 된다.
            em.persist(member);

//            team.getMembers().add(member);  // *** 연관관계 편의 메서드로 만들어서 생략. Team의 members 는 @OneToMany(mappedBy = "team") 로 읽기 전용이다. 연관관계의 주인에 값을 입력한 것이 아니다. 따라서 업데이트 되지 않는다. 하지만, 순수 객체 상태를 고려하여 양방향 연관관계 매핑시, 항상 양쪽에 값을 설정하자.

//            em.flush();
//            em.clear();

            Team findTeam = em.find(Team.class, team.getId());  // 1차 캐시
            List<Member> members = findTeam.getMembers();

            System.out.println("==========================================");
            System.out.println("members = " + findTeam);    // 양방향 매핑 toString 무한 루프 예시
             System.out.println("==========================================");


            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
