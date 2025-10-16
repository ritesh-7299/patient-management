package com.pm.authservice.filter;

import com.pm.commonutils.filter.GlobalExceptionFilter;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler extends GlobalExceptionFilter {
}
