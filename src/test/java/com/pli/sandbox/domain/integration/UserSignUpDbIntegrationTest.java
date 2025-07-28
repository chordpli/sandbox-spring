// package com.pli.sandbox.domain.integration;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
// import com.pli.sandbox.domain.coupon.enums.CouponType;
// import com.pli.sandbox.domain.coupon.model.Coupon;
// import com.pli.sandbox.domain.coupon.repository.CouponJpaRepository;
// import com.pli.sandbox.domain.users.dto.SignUpDto;
// import com.pli.sandbox.domain.users.model.User;
// import com.pli.sandbox.domain.users.repository.command.UserCommandRepository;
// import com.pli.sandbox.domain.users.service.command.UserCommandService;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.test.context.ActiveProfiles;
//
// @SpringBootTest
// @ActiveProfiles("test")
// class UserSignUpDbIntegrationTest {
//
//    @Autowired
//    private UserCommandService userCommandService;
//
//    @Autowired
//    private UserCommandRepository userCommandRepository;
//
//    @Autowired
//    private CouponJpaRepository couponJpaRepository;
//
//    @Test
//    @DisplayName("회원가입 시 DB에 회원 정보와 쿠폰이 저장되어야 한다")
//    void shouldPersistUserAndCouponOnSignUp() throws InterruptedException {
//        // given
//        String email = "test" + System.currentTimeMillis() + "@example.com"; // 고유한 이메일 사용
//        String password = "password123";
//        String nickname = "tester" + System.currentTimeMillis();
//
//        SignUpDto.Request request = SignUpDto.Request.of(email, password, nickname);
//
//        // when
//        String savedEmail = userCommandService.signUp(request);
//
//        // 비동기 이벤트 처리를 위해 잠시 대기
//        TimeUnit.SECONDS.sleep(1);
//
//        // then
//        // 1. 사용자 저장 확인
//        User savedUser = userCommandRepository.findByEmail(email).orElse(null);
//
//        assertNotNull(savedUser, "저장된 사용자가 존재해야 합니다");
//        assertEquals(email, savedUser.getEmail());
//        assertEquals(nickname, savedUser.getNickname());
//
//        // 2. 쿠폰 발행 확인
//        List<Coupon> issuedCoupons = couponJpaRepository.findByUserId(savedUser.getId());
//
//        assertNotNull(issuedCoupons, "발행된 쿠폰이 존재해야 합니다");
//        assertTrue(issuedCoupons.size() > 0, "적어도 하나의 쿠폰이 발행되어야 합니다");
//
//        Coupon welcomeCoupon = issuedCoupons.stream()
//                .filter(coupon -> coupon.getType() == CouponType.WELCOME)
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(welcomeCoupon, "웰컴 쿠폰이 발행되어야 합니다");
//        assertEquals(savedUser.getId(), welcomeCoupon.getUserId());
//        assertTrue(welcomeCoupon.getCode().startsWith("WELCOME-"), "쿠폰 코드는 WELCOME- 접두사를 가져야 합니다");
//        assertEquals(10, welcomeCoupon.getDiscountRate(), "웰컴 쿠폰은 10% 할인이어야 합니다");
//        assertEquals(false, welcomeCoupon.getIsUsed(), "새로 발행된 쿠폰은 미사용 상태여야 합니다");
//    }
//
//    @Test
//    @DisplayName("다수의 회원가입 시 각 사용자마다 별도의 쿠폰이 발행되어야 한다")
//    void shouldIssueUniqueWelcomeCouponForEachUser() throws InterruptedException {
//        // given
//        String email1 = "test1" + System.currentTimeMillis() + "@example.com";
//        String email2 = "test2" + System.currentTimeMillis() + "@example.com";
//
//        SignUpDto.Request request1 = SignUpDto.Request.of(email1, "password123", "tester1");
//        SignUpDto.Request request2 = SignUpDto.Request.of(email2, "password123", "tester2");
//
//        // when
//        userCommandService.signUp(request1);
//        userCommandService.signUp(request2);
//
//        // 비동기 이벤트 처리를 위해 잠시 대기
//        TimeUnit.SECONDS.sleep(1);
//
//        // then
//        User user1 = userCommandRepository.findByEmail(email1).orElse(null);
//        User user2 = userCommandRepository.findByEmail(email2).orElse(null);
//
//        assertNotNull(user1, "첫 번째 사용자가 저장되어야 합니다");
//        assertNotNull(user2, "두 번째 사용자가 저장되어야 합니다");
//
//        List<Coupon> coupons1 = couponJpaRepository.findByUserId(user1.getId());
//        List<Coupon> coupons2 = couponJpaRepository.findByUserId(user2.getId());
//
//        assertTrue(coupons1.size() > 0, "첫 번째 사용자의 쿠폰이 발행되어야 합니다");
//        assertTrue(coupons2.size() > 0, "두 번째 사용자의 쿠폰이 발행되어야 합니다");
//
//        Coupon welcomeCoupon1 = coupons1.stream()
//                .filter(coupon -> coupon.getType() == CouponType.WELCOME)
//                .findFirst()
//                .orElse(null);
//
//        Coupon welcomeCoupon2 = coupons2.stream()
//                .filter(coupon -> coupon.getType() == CouponType.WELCOME)
//                .findFirst()
//                .orElse(null);
//
//        assertNotNull(welcomeCoupon1, "첫 번째 사용자의 웰컴 쿠폰이 발행되어야 합니다");
//        assertNotNull(welcomeCoupon2, "두 번째 사용자의 웰컴 쿠폰이 발행되어야 합니다");
//
//        // 쿠폰 코드가 서로 달라야 함
//        assertNotNull(welcomeCoupon1.getCode(), "쿠폰1 코드가 존재해야 합니다");
//        assertNotNull(welcomeCoupon2.getCode(), "쿠폰2 코드가 존재해야 합니다");
//        assertTrue(!welcomeCoupon1.getCode().equals(welcomeCoupon2.getCode()), "두 쿠폰의 코드는 서로 달라야 합니다");
//    }
// }
