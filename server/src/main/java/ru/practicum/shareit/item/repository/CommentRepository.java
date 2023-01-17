package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findCommentByItem_IdIsOrderByCreated(Integer itemId);

    List<Comment> findCommentByItem_IdInOrderByCreated(List<Integer> itemId);
}
