package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.user.UserEntity;

import javax.persistence.*;

/**
 * Item Entity.
 */
@Entity
@Data
@Table(name = "item")
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "available", nullable = false)
    private Boolean available;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserEntity owner;
}
