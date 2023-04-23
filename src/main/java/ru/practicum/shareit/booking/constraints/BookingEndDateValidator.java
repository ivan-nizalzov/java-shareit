package ru.practicum.shareit.booking.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import ru.practicum.shareit.booking.dto.BookingCreateRequestDto;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class BookingEndDateValidator implements ConstraintValidator<EndDateValidation, BookingCreateRequestDto> {

  @Override
  public boolean isValid(BookingCreateRequestDto createRequestDto,
      ConstraintValidatorContext constraintValidatorContext) {
    return createRequestDto.getEndDateTime() != null && createRequestDto.getStartDateTime() != null
        && createRequestDto.getEndDateTime().isAfter(createRequestDto.getStartDateTime());
  }
}
