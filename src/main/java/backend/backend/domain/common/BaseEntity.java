package backend.backend.domain.common;

import backend.backend.domain.enums.Week;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Week.ActiveStatus activeStatus = Week.ActiveStatus.ACTIVE;

    //softdelete 된 후 다시 활성 상태로 업데이트
    public void updateActiveStatus() {
        if (this.activeStatus == Week.ActiveStatus.DELETED) {
            this.activeStatus = Week.ActiveStatus.ACTIVE;
        }
    }
}
