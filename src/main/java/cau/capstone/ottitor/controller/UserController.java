package cau.capstone.ottitor.controller;

import cau.capstone.ottitor.dto.DataResponseDto;
import cau.capstone.ottitor.dto.UserDto;
import cau.capstone.ottitor.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping(path = "/me")
    public DataResponseDto<Object> getMe() {
        return DataResponseDto.of(userService.getMe());
    }

    @PatchMapping(path = "/me")
    public DataResponseDto<Object> updateMe(
        @RequestBody UserDto userDto
    ) {
        return DataResponseDto.of(userService.updateMe(userDto));
    }

    @DeleteMapping(path = "/me")
    public DataResponseDto<Object> deleteMe(
    ) {
        return DataResponseDto.of(userService.deleteMe());
    }
}