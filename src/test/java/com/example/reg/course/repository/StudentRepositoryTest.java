package com.example.reg.course.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.enrollment.course.entity.Student;
import com.example.enrollment.course.repository.StudentRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository repo;

    @Test
    @DisplayName("save and findById works")
    void saveAndFindById() {
        Student s = new Student();
        s.setFirstName("John");
        s.setLastName("Doe");
        s.setContactNumber("123");
        Student saved = repo.save(s);

        Optional<Student> found = repo.findById(saved.getStudentId());
        assertThat(found).isPresent()
                         .get()
                         .extracting(Student::getFirstName)
                         .isEqualTo("John");
    }

    @Test
    @DisplayName("findAll returns all saved students")
    void findAll() {
        Student s1 = new Student();
        s1.setFirstName("A");
        Student s2 = new Student();
        s2.setFirstName("B");
        repo.saveAll(List.of(s1, s2));

        List<Student> all = repo.findAll();
        assertThat(all).hasSize(2)
                       .extracting(Student::getFirstName)
                       .containsExactlyInAnyOrder("A","B");
    }
}
