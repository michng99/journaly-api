package com.journaly.api.repository;

import com.journaly.api.entity.JournalEntry;
import com.journaly.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, UUID> {
    long countByUser(User user);
}