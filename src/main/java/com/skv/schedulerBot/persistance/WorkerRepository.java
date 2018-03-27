package com.skv.schedulerBot.persistance;

import com.skv.schedulerBot.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, Long> {
    Worker findByFullName(String fullName);
    Worker findByChatId(Long chatId);
}
