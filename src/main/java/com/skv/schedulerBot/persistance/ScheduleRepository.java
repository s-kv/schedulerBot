package com.skv.schedulerBot.persistance;

import com.skv.schedulerBot.domain.Schedule;
import com.skv.schedulerBot.domain.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findByWorker(Worker worker);
}
