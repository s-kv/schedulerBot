package com.skv.schedulerBot.domain;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    @OrderBy("startDate ASC")
    private List<Schedule> schedule;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Collection<Schedule> getSchedule() {
        if (schedule == null)
            schedule = new ArrayList<>();
        return schedule;
    }

    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }
}
