package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.IncorrectFieldException;
import ru.practicum.exception.IncorrectObjectException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.NewUserRequestMapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserDtoMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) throws IncorrectFieldException {
        checkNewUserRequestData(newUserRequest);
        checkCorrectEmail(newUserRequest);

        User user = NewUserRequestMapper.mapToUserEntity(newUserRequest);
        return UserDtoMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        checkPageableParams(from, size);

        Pageable sorted = PageRequest.of((from / size), size);
        List<User> users;
        if (ids == null) {
            users = userRepository.findAllByParams(from, size);
        } else {
            users = userRepository.findAllByIds(ids, sorted);
        }
        return UserDtoMapper.mapToUserDto(users);
    }

    @Override
    public void deleteUser(Long userId) throws IncorrectObjectException {
        checkUserExists(userId);

        userRepository.deleteById(userId);
    }

    private void checkNewUserRequestData(NewUserRequest newUserRequest) throws IncorrectFieldException {
        if (newUserRequest.getName().isBlank()) {
            throw new IncorrectFieldException("Name field cannot be blank");
        }
        if (newUserRequest.getEmail().isBlank()) {
            throw new IncorrectFieldException("Email field cannot be blank");
        }
    }

    private void checkCorrectEmail(NewUserRequest newUserRequest) throws IncorrectFieldException {
        String email = newUserRequest.getEmail();
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            throw new IncorrectFieldException("Incorrect format of Email");
        }
    }

    private void checkPageableParams(Integer from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("From parameter cannot be less zero");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Size parameter cannot be less or equal zero");
        }
    }

    @Override
    public void checkUserExists(Long userId) throws IncorrectObjectException {
        if (!userRepository.findAll().isEmpty()) {
            List<Long> ids = userRepository.findAll()
                    .stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            if (!ids.contains(userId)) {
                throw new IncorrectObjectException("There is no user with id = " + userId);
            }
        } else {
            throw new IncorrectObjectException("There is no user with id = " + userId);
        }
    }
}
