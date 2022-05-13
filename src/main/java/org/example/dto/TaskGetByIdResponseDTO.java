package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class TaskGetByIdResponseDTO {
    private long id;
    private boolean done;
    private List<Result> results;

    @RequiredArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Result {
        private long id;
        private String nameFile;
        private int numberLine;
    }
}
