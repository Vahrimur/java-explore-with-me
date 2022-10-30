package ru.practicum.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * from users u where u.id in :ids order by id asc " +
            "offset :from rows fetch next :size rows only", nativeQuery = true)
    List<User> findAllByIdsAndParams(List<Long> ids, int from, int size);

    @Query(value = "select * from users order by id asc offset ?1 rows fetch next ?2 rows only",
            nativeQuery = true)
    List<User> findAllByParams(int from, int size);
}
