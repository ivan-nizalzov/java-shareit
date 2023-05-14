package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Item item;
    private Booking booking;
    private User user;

    @BeforeEach
    public void setUp() {
        item = Item.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        user = User.builder()
                .email("test@test.com")
                .name("testName")
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.now().plusNanos(1))
                .end(LocalDateTime.now().plusNanos(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyBootstrappingByPersistingAnItem() {
        Assertions.assertNull(booking.getId());
        em.persist(user);
        em.persist(item);
        em.persist(booking);
        Assertions.assertNotNull(booking.getId());
    }

    @Test
    void getAllBookingsByIdTest() {
        Pageable pageable = PageRequest.of(0, 2);
        User userFromDB = userRepository.save(user);
        itemRepository.save(item);
        bookingRepository.save(booking);

        List<Booking> pageBookings = bookingRepository.getAllBookingsById(userFromDB.getId(), pageable);
        Booking bookingFromDB = pageBookings.get(0);

        Assertions.assertNotNull(pageBookings);
        checkBookingAreTheSame(booking, bookingFromDB);
    }

    @Test
    void findBookingsByItemTest() {
        User userFromDB = userRepository.save(user);
        Item itemFromDB = itemRepository.save(item);
        Booking bookingSaved = bookingRepository.save(booking);

        List<Booking> listBookings = bookingRepository.findBookingsByItem(
                itemFromDB, bookingSaved.getStatus(), userFromDB.getId(), LocalDateTime.now().plusNanos(3));
        Booking bookingFromDB = listBookings.get(0);

        Assertions.assertNotNull(listBookings);
        Assertions.assertEquals(1, listBookings.size());
        checkBookingAreTheSame(booking, bookingFromDB);
    }

    private void checkBookingAreTheSame(Booking booking, Booking secondBooking) {
        Assertions.assertEquals(booking.getStart(), secondBooking.getStart());
        Assertions.assertEquals(booking.getEnd(), secondBooking.getEnd());
        Assertions.assertEquals(booking.getItem(), secondBooking.getItem());
        Assertions.assertEquals(booking.getBooker(), secondBooking.getBooker());
        Assertions.assertEquals(booking.getStatus(), secondBooking.getStatus());
    }

}
