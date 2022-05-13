package org.example.service;

import org.example.dto.SearchResultDTO;
import org.example.dto.TaskGetByIdResponseDTO;
import org.example.entity.ResultEntity;
import org.example.entity.TaskEntity;
import org.example.repository.TaskRepository;
import org.example.security.Authentication;
import org.example.security.ForbiddenException;
import org.example.security.NotFoundException;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

@Service
public class SearchService {
    private final TaskRepository repository;
    private Jdbi jdbi;
    private final Path rfc = Paths.get("RFC");
    final ThreadPoolExecutor executor = new ThreadPoolExecutor(64, 256, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    public SearchService(TaskRepository repository) {
        this.repository = repository;
    }

    public SearchResultDTO search(Authentication auth, String search) throws IOException {

        //сохранить в базу
        final TaskEntity saved = repository.save(new TaskEntity(
                0L,
                auth.getId(),
                search,
                false
        ));

        executor.execute(() -> {
            try {
                final List<Path> files = Files.list(rfc).toList();
                for (Path file : files) {
                    try {
                        final List<String> lines = Files.readAllLines(file);

                        final String filename = file.getFileName().toString();
                        int lineNumber = 0;
                        for (String line : lines) {
                            lineNumber++;
                            final int index = line.indexOf(search);
                            if (index != -1) {
                                repository.saveResult(new ResultEntity(0L, saved.getId(), filename, lineNumber));
                            }
                            // SQL -> result
                        }

                        repository.updateStatus(new TaskEntity(saved.getId(), saved.getUserId(), saved.getText(), true));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return new SearchResultDTO(saved.getId(), saved.getUserId(), saved.isDone());
    }

    public TaskGetByIdResponseDTO getById(Authentication auth, long id) {
        if (auth.isAnonymous()) {
            throw new ForbiddenException();
        }

        final TaskEntity task = repository.getById(id).orElseThrow(NotFoundException::new);
        if (task.getUserId() != auth.getId()) {
            throw new ForbiddenException();
        }
        if (!task.isDone()) {
            return new TaskGetByIdResponseDTO(
                    task.getId(),
                    task.isDone(),
                    Collections.emptyList()
            );
        }

        final List<TaskGetByIdResponseDTO.Result> results = repository.getResultsByTaskId(task.getId()).stream()
                .map(o -> new TaskGetByIdResponseDTO.Result(
                        o.getId(),
                        o.getNameFile(),
                        o.getNumberLine()
                ))
                .collect(Collectors.toList());

        return new TaskGetByIdResponseDTO(
                task.getId(),
                task.isDone(),
                results
        );
    }
}

//сохранили задачу в базе, нужно её теперь сделать