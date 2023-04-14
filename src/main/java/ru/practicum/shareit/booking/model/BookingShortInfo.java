package ru.practicum.shareit.booking.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class BookingShortInfo {
    private Long id;
    private Long bookerId;
    @JsonProperty("start")
    private LocalDateTime startDateTime;
    @JsonProperty("end")
    protected LocalDateTime endDateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingShortInfo that = (BookingShortInfo) o;
        return Objects.equals(id, that.id) && Objects.equals(bookerId, that.bookerId)
                && Objects.equals(startDateTime, that.startDateTime)
                && Objects.equals(endDateTime, that.endDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, bookerId, startDateTime, endDateTime);
    }
}
