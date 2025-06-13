package ru.practicum.main.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.request.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventRequestStatusUpdateRequest {

    private List<Long> requestIds;
    private RequestStatus status;
}
