
package com.example.Laba6.todoitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoItemRepository
        extends JpaRepository<TodoItem, Long> {

    // Получение количества задач с указанным статусом
    int countAllByCompleted(boolean completed);

    // Получение списка задач с указанным статусом
    List<TodoItem> findAllByCompleted(boolean completed);
}
