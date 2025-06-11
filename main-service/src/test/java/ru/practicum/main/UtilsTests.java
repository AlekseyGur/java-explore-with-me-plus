package ru.practicum.main;

import java.util.Random;

import ru.practicum.main.user.dto.UserDto;
import ru.practicum.main.user.dto.UserNewDto;
import ru.practicum.main.user.service.UserService;

public class UtilsTests {

    public static String genEmail() {
        return UtilsTests.genString(15) + "@test.test";
    }

    public static String genString(int length) {
        String randomChars = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomChars += (char) (random.nextInt(26) + 'a');
        }
        return randomChars;
    }

    public static UserDto genUserDto(UserService userService) {
        UserNewDto userDto = new UserNewDto();
        userDto.setEmail(UtilsTests.genEmail());
        userDto.setName("username " + UtilsTests.genString(5));
        return userService.save(userDto);
    }

}
