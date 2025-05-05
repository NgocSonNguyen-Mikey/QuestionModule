package org.example.questionmodule.utils.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AppUtil {
    public static LocalDateTime getDateNow() {
        return LocalDateTime.now();
    }
}
