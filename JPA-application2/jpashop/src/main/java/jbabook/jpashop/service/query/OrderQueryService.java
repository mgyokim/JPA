package jbabook.jpashop.service.query;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class OrderQueryService {

    // (OSIV를 끄면, 모든 지연로딩을 트랜젹션 안에서 돌려야한다., Dto도 같은 패지키로 가져와서.)
}
