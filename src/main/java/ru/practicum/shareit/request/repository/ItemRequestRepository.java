package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findItemRequestByRequestorOrderByCreatedDesc(User user);

    List<ItemRequest> findItemRequestByRequestor_IdIsNotOrderByCreatedDesc(Integer userId);

    Page<ItemRequest> findItemRequestByRequestor_IdIsNotOrderByCreatedDesc(Integer userId, Pageable pageable);
}
