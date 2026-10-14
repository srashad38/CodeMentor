package com.codementor.repository;

import com.codementor.model.CodeSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeSubmissionRepository extends JpaRepository<CodeSubmission, Long> {
    List<CodeSubmission> findTop10ByOrderByCreatedAtDesc();
}
