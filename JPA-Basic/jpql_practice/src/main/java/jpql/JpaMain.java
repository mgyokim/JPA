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

            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class); // 타입이하나일 때는 명시하면 타입쿼리로 반환
            Query query1 = em.createQuery("select m.username, m.age from Member m");// m.username은 String인데, m.age는 int 즉 타입을 명기할 수 없다. 이때는 타입쿼리로 반환 불가능
            TypedQuery<String> query2 = em.createQuery("select m.username from Member m", String.class);// m.username은 String 이므로 명시해주면 타입쿼리로 반환 가능

            TypedQuery<Member> query3 = em.createQuery("select m from Member m", Member.class); //
            List<Member> resultList = query3.getResultList(); // getResultList()는 리스트 컬렉션이 반환된다. NullPointException 걱정 안해도된다.

            // 만약에 쿼리의 반환값이 하나라면, where ~#~#~#
            Member singleResult = query3.getSingleResult(); // 정확히 하나만! 반환하며, 결과가 없으면 NoResultException, 결과가 둘 이상이면 NonUniqueException 터짐 ->
            // 만약에 Spring Data JPA를 쓰면, 요즘에는 null로 반환하거나 Optional로 반환한다.(내부적으로 try-catch)

            // 파라미터 바인딩
//            TypedQuery<Member> query4 = em.createQuery("select m from Member m where m.username= :username", Member.class);
//            query4.setParameter("username", "member1");
//            Member singleResult1 = query.getSingleResult();
//            for (Member member1 : resultList) {
//                System.out.println("singleResult1 = " + singleResult1.getUsername());
//            };
            // 보통은 이렇게 체인으로 엮어서 사용한다.
            Member result = em.createQuery("select m from Member m where m.username= :username", Member.class) // 타입이하나일 때는 명시하면 타입쿼리로 반환
                    .setParameter("username", "member1")
                    .getSingleResult();

            System.out.println("result = " + result.getUsername());



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
