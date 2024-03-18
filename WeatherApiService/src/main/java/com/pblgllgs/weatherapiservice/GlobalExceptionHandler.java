package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String ERROR = "Error";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handlerGenericException(HttpServletRequest request, Exception ex) {
        Map<String, String> errors = Map.of(ERROR, ex.getMessage());
        log.info("aca");
        return getErrorDTO(request, HttpStatus.INTERNAL_SERVER_ERROR, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerConstraintViolationException(HttpServletRequest request, ConstraintViolationException ex) {
        Map<String, String> errors = Map.of(ERROR, ex.getMessage());

        return getErrorDTO(request, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(GeolocationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerGeolocationException(HttpServletRequest request, GeolocationException ex) {
        Map<String, String> errors = Map.of(ERROR, ex.getMessage());

        return getErrorDTO(request, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerGenericBadRequestException(HttpServletRequest request, BadRequestException ex) {
        Map<String, String> errors = Map.of(ERROR, ex.getMessage());

        return getErrorDTO(request, HttpStatus.BAD_REQUEST, errors);
    }

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handlerLocationNotFoundException(HttpServletRequest request, LocationNotFoundException ex) {
        Map<String, String> errors = Map.of(ERROR, ex.getMessage());

        return getErrorDTO(request, HttpStatus.NOT_FOUND, errors);
    }

    private static ErrorDTO getErrorDTO(HttpServletRequest request, HttpStatus status, Map<String, String> errors) {

        log.info(errors.toString());
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(status.value());
        errorDTO.setErrors(errors);
        errorDTO.setPath(request.getServletPath());
        return errorDTO;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setTimestamp(new Date());
        errorDTO.setPath(((ServletWebRequest) request).getRequest().getServletPath());
        errorDTO.setErrors(extractErrors(ex.getBindingResult().getFieldErrors()));
        log.info(extractErrors(ex.getBindingResult().getFieldErrors()).toString());
        return new ResponseEntity<>(errorDTO, headers, status);
    }

    private Map<String, String> extractErrors(List<FieldError> listErrors) {
        Map<String, String> errorsMap = new HashMap<>();
        listErrors.forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        return errorsMap;
    }

}
