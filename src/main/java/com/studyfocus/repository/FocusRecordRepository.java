package com.studyfocus.repository;

import com.studyfocus.entity.FocusRecord;
import com.studyfocus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FocusRecordRepository extends JpaRepository<FocusRecord, Long> {
    List<FocusRecord> findByUserOrderByTimestampAsc(User user);
}
