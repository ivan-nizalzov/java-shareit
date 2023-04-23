package ru.practicum.shareit.booking.model;


import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FilterValueConverter implements Converter<String, BookingFilter> {

  @Override
  public BookingFilter convert(String inputFilterValue) {
    try {
      return BookingFilter.valueOf(inputFilterValue.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Unknown state: " + inputFilterValue);
    }
  }
}
