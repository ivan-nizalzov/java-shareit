package ru.practicum.shareit.booking.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "start_date")
  private LocalDateTime startDateTime;
  @Column(name = "end_date")
  private LocalDateTime endDateTime;
  @ManyToOne
  @JoinColumn(name = "item_id", referencedColumnName = "id")
  private Item item;
  @ManyToOne
  @JoinColumn(name = "booker_id", referencedColumnName = "id")
  private User booker;
  @Enumerated(EnumType.STRING)
  private BookingStatus status;
}
