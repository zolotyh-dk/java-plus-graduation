package ru.practicum.ewm.event;

import java.time.LocalDateTime;
import java.time.Month;

import static ru.practicum.ewm.category.CategoryTestUtil.CATEGORY_DTO_1;
import static ru.practicum.ewm.user.UserTestUtil.USER_SHORT_DTO_1;

public class EventTestUtil {
    public static final long EVENT_ID_1 = 1L;
    public static final long EVENT_ID_2 = 2L;
    public static final String EVENT_TITLE_1 = "Event title 1";

    public static final String EVENT_ANNOTATION_1 = "Event annotation 1";
    public static final LocalDateTime EVENT_DATE_1 = LocalDateTime
            .of(2999, Month.DECEMBER, 1, 12, 0, 0);

    public static final EventShortDto EVENT_SHORT_DTO_1 = EventShortDto.builder()
            .id(EVENT_ID_1)
            .initiator(USER_SHORT_DTO_1)
            .title(EVENT_TITLE_1)
            .category(CATEGORY_DTO_1)
            .eventDate(EVENT_DATE_1)
            .annotation(EVENT_ANNOTATION_1)
            .paid(false)
            .confirmedRequests(1L)
            .views(1L)
            .build();
}
