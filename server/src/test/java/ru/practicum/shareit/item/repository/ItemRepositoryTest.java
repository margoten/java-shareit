package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.utils.PaginationUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    ItemRepository itemRepository;

    User user = new User(null,
            "Harry",
            "mail@mail.ru");


    Item item1;
    Item item2;
    Pageable pageable;

    @BeforeEach
    void beforeEach() {
        entityManager.persist(user);
        item1 = new Item(null, "Item1", "Description1", true, user, null);
        item2 = new Item(null, "Item2", "Description2", true, user, null);
        entityManager.persist(item1);
        entityManager.persist(item2);
        pageable = PaginationUtils.createPageRequest(0, 100, Sort.by("id").ascending());
    }


    @Test
    void searchAllItemsInResult() {
        Page<Item> items = itemRepository.search("item", pageable);
        assertThat(items, containsInAnyOrder(item1, item2));
        assertThat(items.toList(), hasSize(2));
    }

    @Test
    void searchEmptyItemsInResult() {
        Page<Item> items = itemRepository.search("hello", pageable);
        assertThat(items.toList(), empty());
    }

    @Test
    void searchOneOfItemsInResult() {

        Page<Item> items = itemRepository.search("description1", pageable);
        assertThat(items.toList(), hasSize(1));
        assertThat(items.toList(), contains(item1));
    }
}