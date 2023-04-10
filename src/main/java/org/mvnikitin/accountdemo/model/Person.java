package org.mvnikitin.accountdemo.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table
public record Person(
        @Id
        Integer id,
        String firstName,
        String middleName,
        String lastName,
        LocalDate dateOfBirth,
        String passport,
        String contactPhone,
        @CreatedDate
        LocalDateTime created,
        @LastModifiedDate
        LocalDateTime changed
) {
}
