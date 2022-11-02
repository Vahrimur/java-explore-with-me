package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Boolean existsByAuthorIdAndEventIdAndText(Long userId, Long eventId, String text);

    @Query(value = "select * from comments c where (c.event_id in :eventIds) " +
            "and (lower(c.text) like lower(concat('%',:text,'%'))) " +
            "and (c.created_on between :rangeStart and :rangeEnd) " +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Comment> findCommentsByParamsWithEvents(List<Long> eventIds,
                                                 String text,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Integer from,
                                                 Integer size);

    @Query(value = "select * from comments c where (lower(c.text) like lower(concat('%',:text,'%')))" +
            "and (c.created_on between :rangeStart and :rangeEnd) " +
            "order by id asc offset :from rows fetch next :size rows only", nativeQuery = true)
    List<Comment> findCommentsByParamsWithoutEvents(String text,
                                                    LocalDateTime rangeStart,
                                                    LocalDateTime rangeEnd,
                                                    Integer from,
                                                    Integer size);

}
