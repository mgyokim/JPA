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

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            em.flush();
            em.clear();
// ----------- 엔티티 프로젝션
             List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();
            Member findMember = result.get(0);
            findMember.setAge(20);

            // 묵시적 조인
            List<Team> result2 = em.createQuery("select m.team from Member m", Team.class) // 이렇게 쓰지말고
                    .getResultList();
            // 명시적 조인
            // 아래처럼 실제 sql과 최대한 비슷하게 작성해야함 왜냐하면, join의 경우 성능에 영향을 줄 수 있는 요소와 튜닝할 수 있는 요소가 많기 때문에 한눈에 보여야 한다.
            List<Team> result3 = em.createQuery("select t from Member m join m.team t", Team.class)
                    .getResultList();

// ----------- 임베디드 타입 프로젝션 -> 임베디드 타입만으로는 안되고, 속한 엔티티로부터 시작 ex) o.address
            em.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

// ----------- 스칼라 타입 프로젝션
            em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

// 여러 값 프로젝션 조회 - 1. Query 타입으로 조회
            List resultList = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            Object o = resultList.get(0);
            Object[] result4 = (Object[]) o;
            System.out.println("username = " + result4[0]);
            System.out.println("age = " + result4[1]);


            // 2. 제네릭에 Obejct 배열 지정
            List<Object[]> resultList2 = em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            Object[] result5 = resultList2.get(0);
            System.out.println("username = " + result5[0]);
            System.out.println("age = " + result5[1]);

            // 3. new 명령어로 조회 -> 가장 깔끔함
            List<MemberDTO> result6 = em.createQuery("select distinct new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();

            MemberDTO memberDTO = result6.get(0);
            System.out.println("memberDTO.getUsername() = " + memberDTO.getUsername());
            System.out.println("memberDTO.getAge() = " + memberDTO.getAge());


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
