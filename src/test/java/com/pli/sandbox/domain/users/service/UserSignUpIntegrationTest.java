package com.pli.sandbox.domain.users.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.pli.sandbox.domain.coupon.service.command.CouponCommandService;
import com.pli.sandbox.domain.users.dto.SignUpDto;
import com.pli.sandbox.domain.users.model.User;
import com.pli.sandbox.domain.users.repository.command.UserCommandRepository;
import com.pli.sandbox.domain.users.service.command.UserCommandService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class UserSignUpIntegrationTest {

    @Autowired
    private UserCommandService userCommandService;

    // @MockitoSpyBean
    private CouponCommandService couponCommandService;

    // @MockitoBean
    private UserCommandRepository userCommandRepository;

    @Test
    @DisplayName("회원가입 시 쿠폰이 발행되어야 한다")
    void shouldIssueCouponOnSignUp() throws InterruptedException {
        // given
        SignUpDto.Request request = SignUpDto.Request.of("test@example.com", "password123", "tester");

        User savedUser = User.createOf(request.getEmail(), request.getPassword(), request.getNickname());
        try {
            java.lang.reflect.Field idField = User.class.getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedUser, 1L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        org.mockito.Mockito.when(userCommandRepository.save(any(User.class))).thenReturn(savedUser);

        // when
        userCommandService.signUp(request);

        // 비동기 이벤트 처리를 위해 잠시 대기
        TimeUnit.MILLISECONDS.sleep(500);

        // then
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(couponCommandService, times(1)).issueWelcomeCoupon(userIdCaptor.capture());
        assert userIdCaptor.getValue().equals(1L);
    }
}
