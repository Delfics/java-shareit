package ru.practicum.server.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExceptionControllerHandlerTest {
    @Autowired
    ExceptionControlHandler exceptionControlHandler;

    @Test
    public void testHandleValidationException() {
        String message = "Validation Exception";

        ValidationException exception = new ValidationException(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleValidationException(exception);

        assertEquals(errorResponse.getError(), message);
    }

    @Test
    public void testHandleNotFoundException() {
        String message = "NotFound Exception";

        NotFoundException exception = new NotFoundException(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleNotFoundException(exception);

        assertEquals(errorResponse.getError(), message);
    }

    @Test
    public void testHandleThrowable_InternalServerErrorException() {
        String message = "Internal Server Error Exception";

        Throwable exception = new Throwable(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleThrowable(exception);

        assertEquals(errorResponse.getError(), message);
    }

    @Test
    public void testHandleConflictException() {
        String message = "Conflict Exception";

        ConflictException exception = new ConflictException(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleConflictException(exception);

        assertEquals(errorResponse.getError(), message);
    }

    @Test
    public void testHandleForbiddenException() {
        String message = "Forbidden Exception";

        ForbiddenException exception = new ForbiddenException(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleForbiddenException(exception);

        assertEquals(errorResponse.getError(), message);
    }

    @Test
    public void testHandleBadRequestException() {
        String message = "Bad Request Exception";

        BadRequestException exception = new BadRequestException(message);

        ErrorResponse errorResponse = exceptionControlHandler.handleBadRequestException(exception);

        assertEquals(errorResponse.getError(), message);
    }
}
