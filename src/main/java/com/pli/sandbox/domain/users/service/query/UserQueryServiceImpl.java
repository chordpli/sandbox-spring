package com.pli.sandbox.domain.users.service.query;

import com.pli.sandbox.domain.users.repository.query.UserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryServiceImpl implements UserQueryService {
    private final UserQueryRepository userQueryRepository;
}
