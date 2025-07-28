// package com.pli.sandbox.domain.coupon.event;
//
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
// import static org.mockito.Mockito.verifyNoMoreInteractions;
//
// import com.pli.sandbox.domain.coupon.service.command.CouponCommandService;
// import com.pli.sandbox.domain.users.event.UserSignUpEvent;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class UserSignUpEventListenerTest {
//
//    @Mock
//    private CouponCommandService couponCommandService;
//
//    @InjectMocks
//    private UserSignUpEventListener eventListener;
//
//    @Test
//    @DisplayName("회원가입 이벤트 수신 시 웰컴 쿠폰을 발행해야 한다")
//    void shouldIssueWelcomeCouponOnUserSignUp() {
//        // given
//        Long userId = 1L;
//        String email = "test@example.com";
//        UserSignUpEvent event = new UserSignUpEvent(userId, email);
//
//        // when
//        eventListener.handleUserSignUpEvent(event);
//
//        // then
//        verify(couponCommandService, times(1)).issueWelcomeCoupon(userId);
//        verifyNoMoreInteractions(couponCommandService);
//    }
//
//    @Test
//    @DisplayName("쿠폰 발행 중 예외가 발생해도 이벤트 처리가 중단되지 않아야 한다")
//    void shouldHandleExceptionDuringCouponIssue() {
//        // given
//        Long userId = 1L;
//        String email = "test@example.com";
//        UserSignUpEvent event = new UserSignUpEvent(userId, email);
//
//        // 예외 발생 시뮬레이션
//        org.mockito.Mockito.doThrow(new RuntimeException("쿠폰 발행 실패"))
//                .when(couponCommandService)
//                .issueWelcomeCoupon(userId);
//
//        // when & then - 예외가 전파되지 않고 처리되어야 함
//        eventListener.handleUserSignUpEvent(event);
//        verify(couponCommandService, times(1)).issueWelcomeCoupon(userId);
//    }
// }
