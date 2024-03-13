package com.pblgllgs.weatherapiservice;

import com.pblgllgs.weatherapiservice.location.LocationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER_LOG = LoggerFactory.getLogger((GlobalExceptionHandler.class));

    private static final String ERROR = "Error";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handlerGenericException(HttpServletRequest request, Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.addError(ERROR, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        LOGGER_LOG.info(ex.getMessage(), ex);

        return errorDTO;
    }

    @ExceptionHandler(GeolocationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerGeolocationException(HttpServletRequest request, GeolocationException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.addError(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        LOGGER_LOG.info(ex.getMessage(), ex);

        return errorDTO;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerGenericBadRequestException(HttpServletRequest request, BadRequestException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.addError(ERROR, HttpStatus.BAD_REQUEST.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        LOGGER_LOG.info(ex.getMessage(), ex);

        return errorDTO;
    }

    @ExceptionHandler(LocationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handlerLocationNotFoundException(HttpServletRequest request, LocationNotFoundException ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.NOT_FOUND.value());
        errorDTO.addError(ERROR, ex.getMessage());
        errorDTO.setPath(request.getServletPath());

        LOGGER_LOG.info(ex.getMessage(), ex);

        return errorDTO;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handlerGenericBadRequestException(HttpServletRequest request, ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDTO.setErrors(extractErrorsConstraint(constraintViolations));
        errorDTO.setPath(request.getServletPath());

        LOGGER_LOG.info(ex.getMessage(), ex);

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
        return new ResponseEntity<>(errorDTO, headers, status);
    }

    private Map<String, String> extractErrors(List<FieldError> listErrors) {
        Map<String, String> errorsMap = new HashMap<>();
        listErrors.forEach(error -> errorsMap.put(error.getField(), error.getDefaultMessage()));
        return errorsMap;
    }

    private Map<String, String> extractErrorsConstraint(Set<ConstraintViolation<?>> constraintViolations) {
        Map<String, String> errorsMap = new HashMap<>();
        constraintViolations.forEach(
                constraintViolation -> errorsMap.put(
                        constraintViolation.getPropertyPath().toString(),
                        constraintViolation.getMessage()
                )
        );
        return errorsMap;
    }
}
