package com.example.demo;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RateLimiter implements Filter{

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

                private Bucket createNewBucket(){
                    return Bucket.builder()
                        .addLimit(Bandwidth.builder()
                            .capacity(10)
                            .refillGreedy(10, Duration.ofMinutes(1))
                            .build()
                    ).build();
                }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
                HttpServletRequest httpServletRequest = (HttpServletRequest)request;
                HttpServletResponse httpServletResponse = (HttpServletResponse)response;

                String ip = httpServletRequest.getRemoteAddr();

                Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());

                if(bucket.tryConsume(1)){
                    chain.doFilter(request, response);
                }else{
                    httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    httpServletResponse.getWriter().write("Previse requestova");
                }

    }
    
}
