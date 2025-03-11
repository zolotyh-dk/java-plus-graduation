package ru.practicum.ewm.user;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class UserTestUtil {
    public static final Pageable PAGEABLE = PageRequest.of(0, 10);
    public static final long USER_ID_1 = 1L;
    public static final long USER_ID_2 = 2L;
    public static final long USER_ID_3 = 3L;
    public static final String USER_NAME_1 = "First User";
    public static final String USER_NAME_2 = "Second User";
    public static final String USER_NAME_3 = "Third User";
    public static final String EMAIL_1 = "first@test.com";
    public static final String EMAIL_2 = "second@test.com";
    public static final String EMAIL_3 = "third@test.com";

    public static final UserShortDto USER_SHORT_DTO_1 = UserShortDto.builder()
            .id(USER_ID_1)
            .name(USER_NAME_1)
            .build();
}
