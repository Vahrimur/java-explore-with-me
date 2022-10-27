package ru.practicum.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.id in :ids or :ids is null")
    List<User> findAllByIds(List<Long> ids, Pageable pageable);

    @Query(value = "select * from users order by id asc offset ?1 rows fetch next ?2 rows only",
            nativeQuery = true)
    List<User> findAllByParams(int from, int size);
}
