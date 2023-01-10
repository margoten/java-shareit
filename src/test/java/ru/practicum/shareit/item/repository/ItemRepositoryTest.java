package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

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

    @BeforeEach
    void beforeEach() {
        entityManager.persist(user);
        item1 = new Item(null, "Item1", "Description1", true, user, null);
        item2 = new Item(null, "Item2", "Description2", true, user, null);
        entityManager.persist(item1);
        entityManager.persist(item2);
    }


    @Test
    void searchAllItemsInResult() {
        List<Item> items = itemRepository.search("item");
        assertThat(items, containsInAnyOrder(item1, item2));
        assertThat(items, hasSize(2));
    }

    @Test
    void searchEmptyItemsInResult() {
        List<Item> items = itemRepository.search("hello");
        assertThat(items, empty());
    }

    @Test
    void searchOneOfItemsInResult() {

        List<Item> items = itemRepository.search("description1");
        assertThat(items, hasSize(1));
        assertThat(items, contains(item1));
    }
}