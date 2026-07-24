package com.erensia.gamescheduling.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 모든 엔티티가 상속하는 공통 베이스.
 *
 * 01-requirements.md 7절 결정: PK는 Long(auto increment), createdAt은 전 엔티티에
 * 일괄 적용(JPA Auditing으로 자동 관리, 수정 불가). updatedAt은 MVP 범위에서 도입하지 않는다
 * (같은 문서 참고 - 동시 편집 정책이 확정되면 그때 필요 여부를 재검토한다).
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

}
