package org.mvnikitin.accountdemo.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table
public record Account(
        @Id
        Long id,
        Integer personId,
        String number,
        BigDecimal amount,
        @CreatedDate
        LocalDateTime created,
        LocalDateTime closed
) {
}
