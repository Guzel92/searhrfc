package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class TaskEntity {
    private long id;
    private long userId;
    private String text;
    private boolean done;
}
