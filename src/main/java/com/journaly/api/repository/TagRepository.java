package com.journaly.api.repository;

import com.journaly.api.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional; // Đảm bảo có import này

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // THÊM PHƯƠNG THỨC NÀY VÀO
    Optional<Tag> findByName(String name);
}