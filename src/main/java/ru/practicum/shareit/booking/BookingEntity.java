package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking")
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "start_dt", nullable = false)
    private LocalDateTime startDateTime;
    @Column(name = "end_dt", nullable = false)
    private LocalDateTime endDateTime;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private ItemEntity item;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "booker_user_id", nullable = false)
    @ToString.Exclude
    private UserEntity booker;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;
}
