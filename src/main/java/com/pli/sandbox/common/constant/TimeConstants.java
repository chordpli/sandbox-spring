package com.pli.sandbox.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TimeConstants {

    public static final long ONE_MINUTE_IN_SECONDS = 60;
    public static final long ONE_HOUR_IN_SECONDS = 60 * ONE_MINUTE_IN_SECONDS;
    public static final long ONE_DAY_IN_SECONDS = 24 * ONE_HOUR_IN_SECONDS;
    public static final long SEVEN_DAYS_IN_SECONDS = 7 * ONE_DAY_IN_SECONDS;
}
