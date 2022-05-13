package org.example.repository;

import lombok.RequiredArgsConstructor;
import org.example.entity.ResultEntity;
import org.example.entity.TaskEntity;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TaskRepository {
    private final Jdbi jdbi;

    public TaskEntity save(TaskEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                // language=PostgreSQL
                                """
                                        INSERT INTO tasks(user_id,text, done) VALUES (:userId, :text, :done)
                                        RETURNING id, user_id, text, done
                                        """
                        )
                        .bind("userId", entity.getUserId())
                        .bind("text", entity.getText())
                        .bind("done", entity.isDone()) // TODO: <-
                        .mapToBean(TaskEntity.class)
                        .one()
        );
    }

    public ResultEntity saveResult(ResultEntity entity) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                //language=PostgreSQL
                                """
                                        INSERT INTO results(task_id,name_file,number_line) VALUES (:taskId,:fileName,:numberLine)
                                        RETURNING id,task_id,name_file, number_line
                                        """
                        )
                        .bind("taskId", entity.getTaskId())
                        .bind("fileName", entity.getNameFile())
                        .bind ("numberLine", entity.getNumberLine())
                        .mapToBean(ResultEntity.class)
                        .one()
        );
    }

    public void updateStatus(TaskEntity entity) {
        jdbi.withHandle(handle -> handle.createUpdate(
                                //language=PostgreSQL
                                """
                                        UPDATE tasks SET done= :done
                                        WHERE id= :id
                                        """
                        )
                        .bind("id", entity.getId())
                        .bind("done", entity.isDone())
                        .execute()
        );
    }

    public Optional<TaskEntity> getById(long id) {
        return jdbi.withHandle(handle -> handle.createQuery(
                                //language=PostgreSQL
                                """
                                        SELECT id, user_id, done FROM tasks WHERE id = :id
                                        """
                        )
                        .bind("id", id)
                        .mapToBean(TaskEntity.class)
                        .findOne()
        );
    }

    public List<ResultEntity> getResultsByTaskId(long taskId) {
        return jdbi.withHandle(handle -> handle.createQuery(
                //language=PostgreSQL
                """
SELECT id, name_file, number_line FROM results WHERE task_id = :task_id
"""
        )
                        .bind("task_id",taskId)
                        .mapToBean(ResultEntity.class)
                        .list()
        );
    }

    //по id задачи найти результат

//    public Optional<TaskEntity> findByLogin(long id) {
//        return jdbi.withHandle(handle -> handle.createQuery(
//                                // language=PostgreSQL
//                                "SELECT task_id, name_file, number_line FROM results WHERE task_id = :task_id"
//                        )
//                        .bind("task_id", taskId)
//                        .mapToBean(UserEntity.class)
//                        .findOne()
//        );
//    }
}
