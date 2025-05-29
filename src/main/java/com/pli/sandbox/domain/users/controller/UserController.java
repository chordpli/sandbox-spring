package com.pli.sandbox.domain.users.controller;

import com.pli.sandbox.common.base.ApiResult;
import com.pli.sandbox.domain.users.dto.SignUpDto;
import com.pli.sandbox.domain.users.service.command.UserCommandService;
import com.pli.sandbox.domain.users.service.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping
    public ApiResult<String> signUp(@RequestBody SignUpDto.Request request) {
        return ApiResult.ok(userCommandService.signUp(request));
    }
}
