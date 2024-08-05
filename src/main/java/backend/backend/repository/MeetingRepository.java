package backend.backend.repository;

import backend.backend.domain.Meeting;
import backend.backend.domain.enums.MeetingCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Page<Meeting> findAllByCategory(PageRequest id, MeetingCategory category);
}
