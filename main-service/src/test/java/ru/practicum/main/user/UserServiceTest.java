package ru.practicum.main.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ru.practicum.main.MainApplication;
import ru.practicum.main.UtilsTests;
import ru.practicum.main.user.controller.UserController;
import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.dto.UserUpdateDto;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.service.UserService;

@SpringBootTest(classes = { MainApplication.class })
public class UserServiceTest {
    @Autowired
    private UserController userController;
    @Autowired
    private UserService userService;

    @Test
    void add() {
        UserNewDto user = new UserNewDto();
        user.setName("name");
        user.setEmail(UtilsTests.genEmail());

        UserDto userSaved = userController.add(user);
        assertTrue(userSaved.getId() > 0, "Пользователь должен добавиться");
    }

    @Test
    void addBadEmail() {
        UserNewDto user = new UserNewDto();
        user.setName("name");
        user.setEmail("test@@test.test");

        assertThrows(Exception.class, () -> userController.add(user),
                "Пользователь НЕ должен добавиться");

        user.setEmail("test_test.test");

        assertThrows(Exception.class, () -> userController.add(user),
                "Пользователь НЕ должен добавиться");
    }

    @Test
    void addBadName() {
        UserNewDto user = new UserNewDto();
        user.setName("");
        user.setEmail(UtilsTests.genEmail());

        assertThrows(Exception.class, () -> userController.add(user),
                "Пользователь НЕ должен добавиться");
    }

    @Test
    void patchName() {
        UserNewDto user = new UserNewDto();
        user.setName("name");
        user.setEmail(UtilsTests.genEmail());

        UserDto userCreated = userController.add(user);

        UserUpdateDto user2 = new UserUpdateDto();
        user2.setId(userCreated.getId());
        user2.setName("New");
        userService.patch(user2);

        UserDto userSaved = userControllerGet(userCreated.getId());
        assertTrue(userSaved.getName().equals("New"), "Имя должно измениться");
    }

    @Test
    void patchEmail() {
        UserNewDto user = new UserNewDto();
        user.setName("name");
        user.setEmail(UtilsTests.genEmail());

        UserDto userCreated = userController.add(user);

        UserUpdateDto user2 = new UserUpdateDto();
        user2.setId(userCreated.getId());
        user2.setEmail(UtilsTests.genEmail());
        userService.patch(user2);

        UserDto userSaved = userControllerGet(userCreated.getId());
        assertTrue(userSaved.getEmail().equals(user2.getEmail()), "Email должно измениться");
    }

    @Test
    void patchUnknownUser() {
        UserUpdateDto user = new UserUpdateDto();
        user.setId(999999999999L);
        user.setName("name");
        user.setEmail(UtilsTests.genEmail());

        assertThrows(Exception.class, () -> userService.patch(user),
                "Нельзя изменить несуществующего пользователя");
    }

    @Test
    void deleteUser() {
        UserNewDto user = new UserNewDto();
        user.setName("name");
        user.setEmail(UtilsTests.genEmail());

        UserDto userSaved = userController.add(user);
        assertTrue(userSaved.getId() > 0, "Пользователь должен добавиться");

        userController.delete(userSaved.getId());
        assertThrows(Exception.class, () -> userControllerGet(userSaved.getId()),
                        "Пользователь должен удалиться");
    }

    private UserDto userControllerGet(Long userId) {
        return userController.get(List.of(userId), null, null).getContent().get(0);
    }
}
