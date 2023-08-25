package com.pblgllgs.weatherapiservice;

import jakarta.servlet.http.HttpServletRequest;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger((GlobalExceptionHandler.class));

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handlerGenericException(HttpServletRequest request, Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setTimestamp(new Date());
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDTO.addError("Error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        errorDTO.setPath(request.getServletPath());

        LOGGER.info(ex.getMessage(), ex);

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
}
