package ru.practicum.main.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main.request.dto.ParticipationRequestDto;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EventRequestStatusUpdateResult {

    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
