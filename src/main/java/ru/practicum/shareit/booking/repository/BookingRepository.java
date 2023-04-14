package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrder_ByStartDateTime_Desc(long userId);

    List<Booking> findAllByBookerId_AndEndDateTimeBeforeOrder_ByStartDateTime_Desc(long userId,
                                                                                   LocalDateTime localDate);

    @Query(value = "SELECT * "
            + "FROM bookings "
            + "WHERE start_date < ?2 "
            + "AND end_date > ?2 "
            + "AND booker_id = ?1 "
            + "ORDER BY start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookings_ByBookerId(long userId, LocalDateTime localDate);

    List<Booking> findAllByBookerId_AndStartDateTimeAfterOrder_ByStartDateTime_Desc(long userId,
                                                                                    LocalDateTime localDateTime);

    List<Booking> findAllByBookerId_AndStatusOrder_ByStartDateTime_Desc(long bookerId, BookingStatus status);

    List<Booking> findAllByItemIdInOrder_ByStartDateTime_Desc(List<Long> ids);

    @Query(value = "SELECT * "
            + "FROM bookings "
            + "WHERE start_date < ?2 "
            + "AND end_date > ?2 "
            + "AND booking_item_id IN (?1) "
            + "ORDER BY start_date DESC", nativeQuery = true)
    List<Booking> findAllCurrentBookings_ByItemsIds(List<Long> ids, LocalDateTime localDateTime);

    List<Booking> findAllByItemIdIn_AndEndDateTimeBeforeOrder_ByStartDateTime_Desc(List<Long> ids,
                                                                                   LocalDateTime localDateTime);

    List<Booking> findAllByItemIdIn_AndStartDateTimeAfterOrder_ByStartDateTime_Desc(List<Long> ids,
                                                                                    LocalDateTime localDateTime);

    List<Booking> findAllByItemIdIn_AndStatusOrder_ByStartDateTime_Desc(List<Long> ids, BookingStatus status);

    Booking findByItemId_AndEndDateTimeBeforeOrder_ByEndDateTime_Desc(long itemId, LocalDateTime localDateTime);

    Booking findByItemId_AndStartDateTimeAfterOrder_ByEndDateTime_Asc(long itemId, LocalDateTime localDateTime);

    Boolean existsByBookerId_AndItemIdAndEndDateTimeBefore(long bookerId, long itemId, LocalDateTime dateTimeTime);

}
