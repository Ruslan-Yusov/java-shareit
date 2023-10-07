package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Integer> {

    List<ItemEntity> findByOwnerId(@Param("id") Integer id);

    List<ItemEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description);
}
