package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("select i from Item i " +
            "where i.available = TRUE and (upper(i.name) like upper(concat('%', ?1, '%')) " +
            "or upper(i.description) like upper(concat('%', ?1, '%')))")
    Page<Item> search(String text, Pageable pageable);

    Page<Item> findAllByOwner_IdIs(Integer ownerId, Pageable pageable);

    List<Item> findAllByRequest_IdIs(Integer requestId);

    List<Item> findAllByRequest_IdIn(List<Integer> requests);
}
