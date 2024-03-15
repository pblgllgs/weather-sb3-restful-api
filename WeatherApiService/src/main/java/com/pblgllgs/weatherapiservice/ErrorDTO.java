package com.pblgllgs.weatherapiservice;

import lombok.Data;

import java.util.*;

@Data
public class ErrorDTO {
    private Date timestamp;
    private int status;
    private String path;
    private Map<String,String> errors = new HashMap<>();
}
