// package com.pli.sandbox.domain.coupon.service.command;
//
// import static org.mockito.Mockito.times;
// import static org.mockito.Mockito.verify;
//
// import com.pli.sandbox.domain.coupon.enums.CouponType;
// import com.pli.sandbox.domain.coupon.model.Coupon;
// import com.pli.sandbox.domain.coupon.repository.CouponJpaRepository;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;
//
// @ExtendWith(MockitoExtension.class)
// class CouponCommandServiceTest {
//
//    @Mock
//    private CouponJpaRepository couponJpaRepository;
//
//    @InjectMocks
//    private CouponCommandServiceImpl couponCommandService;
//
//    @Test
//    @DisplayName("신규 회원에게 웰컴 쿠폰을 발행해야 한다")
//    void shouldIssueWelcomeCouponToNewUser() {
//        // given
//        Long userId = 1L;
//
//        // when
//        couponCommandService.issueWelcomeCoupon(userId);
//
//        // then
//        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
//        verify(couponJpaRepository, times(1)).save(couponCaptor.capture());
//
//        Coupon capturedCoupon = couponCaptor.getValue();
//        assert capturedCoupon.getUserId().equals(userId);
//        assert capturedCoupon.getType() == CouponType.WELCOME;
//        assert capturedCoupon.getName().contains("웰컴");
//        assert capturedCoupon.getDiscountRate() == 10; // 10% 할인
//        assert !capturedCoupon.getIsUsed(); // 미사용 상태
//    }
//
//    @Test
//    @DisplayName("쿠폰 코드는 고유해야 한다")
//    void couponCodeShouldBeUnique() {
//        // given
//        Long userId1 = 1L;
//        Long userId2 = 2L;
//
//        // when
//        couponCommandService.issueWelcomeCoupon(userId1);
//        couponCommandService.issueWelcomeCoupon(userId2);
//
//        // then
//        ArgumentCaptor<Coupon> couponCaptor = ArgumentCaptor.forClass(Coupon.class);
//        verify(couponJpaRepository, times(2)).save(couponCaptor.capture());
//
//        // 캡처된 쿠폰 목록 가져오기
//        var capturedCoupons = couponCaptor.getAllValues();
//        String code1 = capturedCoupons.get(0).getCode();
//        String code2 = capturedCoupons.get(1).getCode();
//
//        assert !code1.equals(code2); // 쿠폰 코드가 서로 달라야 함
//        assert code1.startsWith("WELCOME-"); // 쿠폰 코드 형식 확인
//        assert code2.startsWith("WELCOME-"); // 쿠폰 코드 형식 확인
//    }
// }
