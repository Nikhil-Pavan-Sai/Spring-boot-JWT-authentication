package com.example.springbootcustomjwtauth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
@Setter
@Getter
public abstract class DateAudit implements Serializable {

    @CreatedDate
    @NonNull
    private Instant createdAt;

    @LastModifiedDate
    @NonNull
    private Instant updatedAt;
}