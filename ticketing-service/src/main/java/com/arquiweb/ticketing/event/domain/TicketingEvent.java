package com.arquiweb.ticketing.event.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ticketing_event")
@AllArgsConstructor
@NoArgsConstructor
public class TicketingEvent {

    @Id
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "sales_enabled", nullable = false)
    private Boolean salesEnabled;

}