package com.example.reg.course.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.enrollment.course.entity.WaitlistEntry;
import com.example.enrollment.course.repository.WaitlistEntryRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WaitlistEntryRepositoryTest {

    @Autowired
    private WaitlistEntryRepository repo;

    @Test
    @DisplayName("countByCourseId and findFirstByCourseIdOrderByTimestampAsc")
    void countAndFindFirst() {
        // given: three entries for course 50, timestamps in mixed order
        WaitlistEntry w1 = new WaitlistEntry(null, 50L, 1L, Instant.parse("2025-01-01T00:00:00Z"));
        WaitlistEntry w2 = new WaitlistEntry(null, 50L, 2L, Instant.parse("2025-01-02T00:00:00Z"));
        WaitlistEntry w3 = new WaitlistEntry(null, 50L, 3L, Instant.parse("2025-01-03T00:00:00Z"));
        repo.saveAll(List.of(w2, w3, w1));

        // count
        assertThat(repo.countByCourseId(50L)).isEqualTo(3L);

        // first by timestamp
        Optional<WaitlistEntry> first = repo.findFirstByCourseIdOrderByTimestampAsc(50L);
        assertThat(first).isPresent()
                         .get().extracting(WaitlistEntry::getEnrollmentId)
                         .isEqualTo(1L);
    }

    @Test
    @DisplayName("findByCourseIdOrderByTimestampAsc returns sorted list and deleteByEnrollmentId works")
    void findAllAndDeleteByEnrollmentId() {
        WaitlistEntry w1 = new WaitlistEntry(null, 60L, 10L, Instant.parse("2025-02-01T00:00:00Z"));
        WaitlistEntry w2 = new WaitlistEntry(null, 60L, 20L, Instant.parse("2025-02-02T00:00:00Z"));
        repo.saveAll(List.of(w1, w2));

        List<WaitlistEntry> list = repo.findByCourseIdOrderByTimestampAsc(60L);
        assertThat(list).extracting(WaitlistEntry::getEnrollmentId).containsExactly(10L, 20L);

        // delete by enrollmentId
        repo.deleteByEnrollmentId(10L);
        assertThat(repo.findByCourseIdOrderByTimestampAsc(60L))
            .extracting(WaitlistEntry::getEnrollmentId)
            .containsExactly(20L);
    }
}
