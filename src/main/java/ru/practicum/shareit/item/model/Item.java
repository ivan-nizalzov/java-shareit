package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "is_available", nullable = false)
    private Boolean available;
    @Column(name = "owner_id", nullable = false)
    private Long ownerId;
    @Transient
    private ShortItemBookingDto lastBooking;
    @Transient
    private ShortItemBookingDto nextBooking;
    @Transient
    private List<CommentDto> comments;

}
