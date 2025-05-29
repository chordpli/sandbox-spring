package com.pli.sandbox.domain.coupon.event;

import com.pli.sandbox.domain.coupon.service.command.CouponCommandService;
import com.pli.sandbox.domain.users.event.UserSignUpEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSignUpEventListener {

    private final CouponCommandService couponCommandService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUserSignUpEvent(UserSignUpEvent event) {
        log.info("회원가입 이벤트 수신: userId={}, email={}", event.getUserId(), event.getEmail());
        try {
            couponCommandService.issueWelcomeCoupon(event.getUserId());
            log.info("웰컴 쿠폰 발행 완료: userId={}", event.getUserId());
        } catch (Exception e) {
            log.error("웰컴 쿠폰 발행 실패: userId={}, 오류={}", event.getUserId(), e.getMessage(), e);
        }
    }
}
