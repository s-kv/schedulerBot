package com.skv.schedulerBot.domain;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"fullName"}, name = "fullName_constraint")
})
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Size(min = 3, max = 50)
    private String fullName;
    @Column(nullable = true)
    private long chatId;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "worker", cascade = CascadeType.ALL)
    @OrderBy("date ASC")
    private Set<Schedule> schedule;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Set<Schedule> getSchedule() {
        if (schedule == null)
            schedule = new HashSet<>();
        return schedule;
    }

    public void setSchedule(Set<Schedule> schedule) {
        this.schedule = schedule;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
