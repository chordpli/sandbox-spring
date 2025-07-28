package com.pli.sandbox.domain.users.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pli.sandbox.domain.users.dto.SignUpDto;
import com.pli.sandbox.domain.users.event.UserSignUpEvent;
import com.pli.sandbox.domain.users.model.User;
import com.pli.sandbox.domain.users.repository.command.UserCommandRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserCommandRepository userCommandRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    @DisplayName("회원가입 시 이벤트가 발행되어야 한다")
    void signUpShouldPublishEvent() {
        // given
        SignUpDto.Request request = SignUpDto.Request.of("test@example.com", "password123", "tester");

        User savedUser = User.createOf(request.getEmail(), request.getPassword(), request.getNickname());
        // ID 설정을 위한 리플렉션 사용 (실제 코드에서는 JPA가 자동으로 ID를 설정)
        try {
            java.lang.reflect.Field idField = User.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        when(userCommandRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        userCommandService.signUp(request);

        // then
        // 이벤트 발행 확인
        ArgumentCaptor<UserSignUpEvent> eventCaptor = ArgumentCaptor.forClass(UserSignUpEvent.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());

        UserSignUpEvent capturedEvent = eventCaptor.getValue();
        assert capturedEvent.getUserId().equals(1L);
        assert capturedEvent.getEmail().equals("test@example.com");
    }
}
