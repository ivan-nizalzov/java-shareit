package ru.practicum.shareit.booking.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  List<Booking> findAllByBookerIdOrderByStartDateTimeDesc(long userId);

  List<Booking> findAllByBookerIdAndEndDateTimeBeforeOrderByStartDateTimeDesc(long userId,
      LocalDateTime localDate);

  @Query(value = "SELECT * "
      + "FROM bookings "
      + "WHERE start_date < ?2 "
      + "AND end_date > ?2 "
      + "AND booker_id = ?1 "
      + "ORDER BY start_date DESC", nativeQuery = true)
  List<Booking> findAllCurrentBookingsByBookerId(long userId, LocalDateTime localDate);

  List<Booking> findAllByBookerIdAndStartDateTimeAfterOrderByStartDateTimeDesc(long userId, LocalDateTime localDate);

  List<Booking> findAllByBookerIdAndStatusOrderByStartDateTimeDesc(long bookerId, BookingStatus status);

  List<Booking> findAllByItemIdInOrderByStartDateTimeDesc(List<Long> ids);

  @Query(value = "SELECT * "
      + "FROM bookings "
      + "WHERE start_date < ?2 "
      + "AND end_date > ?2 "
      + "AND item_id IN (?1) "
      + "ORDER BY start_date DESC", nativeQuery = true)
  List<Booking> findAllCurrentBookingsByItemsIds(List<Long> ids, LocalDateTime localDate);

  List<Booking> findAllByItemIdInAndEndDateTimeBeforeOrderByStartDateTimeDesc(List<Long> ids,
      LocalDateTime localDate);

  List<Booking> findAllByItemIdInAndStartDateTimeAfterOrderByStartDateTimeDesc(List<Long> ids, LocalDateTime localDate);

  List<Booking> findAllByItemIdInAndStatusOrderByStartDateTimeDesc(List<Long> ids, BookingStatus status);

  Booking findByItemIdAndEndDateTimeBeforeOrderByEndDateTimeDesc(long itemId, LocalDateTime localDate);

  Booking findByItemIdAndStartDateTimeAfterOrderByEndDateTimeAsc(long itemId, LocalDateTime localDate);

  boolean existsByBookerIdAndItemIdAndEndDateTimeBefore(long bookerId, long itemId, LocalDateTime dateTime);
}
