package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class ResultEntity {
    private long id;
    private long taskId;
    private String nameFile;
    private int numberLine;
}
