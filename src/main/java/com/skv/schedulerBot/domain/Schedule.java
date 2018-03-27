package com.skv.schedulerBot.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"date", "worker_id"}, name = "worker_date_constraint")
})
public class Schedule {
    public static final String DD_MM_YYYY = "dd.MM.yyyy";
    public static final String HH_MM = "HH:mm";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    @ManyToOne
    @JoinColumn(name = "worker_id")
    private Worker worker;

    public Schedule() {}

    public Schedule(Worker worker, LocalDate date) {
        this.worker = worker;
        this.date = date;
    }

    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Schedule.DD_MM_YYYY);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(Schedule.HH_MM);

        if (startTime != null && endTime != null)
            return date.format(dateFormatter)
                    + " : "
                    + startTime.format(timeFormatter)
                    + " - "
                    + endTime.format(timeFormatter);
        else
            return date.format(dateFormatter) + " : -";
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
