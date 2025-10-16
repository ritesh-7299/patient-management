package com.pm.authservice.filter;

import com.pm.commonutils.filter.GlobalResponseFilter;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalResponseWrapper extends GlobalResponseFilter {
}
