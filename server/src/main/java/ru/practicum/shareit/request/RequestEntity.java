package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.shareit.item.ItemEntity;
import ru.practicum.shareit.user.UserEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "request")
public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "description", nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "requestor_user_id", nullable = false)
    private UserEntity author;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "request", cascade = CascadeType.ALL)
    @ToString.Exclude
    private Set<ItemEntity> items = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestEntity that = (RequestEntity) o;
        return Objects.equals(id, that.id)
                && Objects.equals(description, that.description)
                && Objects.equals(author, that.author)
                && Objects.equals(created, that.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, author, created);
    }
}
