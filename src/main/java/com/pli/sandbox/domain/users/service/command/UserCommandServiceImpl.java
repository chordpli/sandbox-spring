package com.pli.sandbox.domain.users.service.command;

import com.pli.sandbox.domain.users.dto.SignUpDto.Request;
import com.pli.sandbox.domain.users.model.User;
import com.pli.sandbox.domain.users.repository.command.UserCommandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserCommandServiceImpl implements UserCommandService {
    private final UserCommandRepository userCommandRepository;

    @Override
    public String signUp(Request request) {
        User user = User.createOf(request.getEmail(), request.getPassword(), request.getNickname());
        User savedUser = userCommandRepository.save(user);

        return savedUser.getEmail();
    }
}
