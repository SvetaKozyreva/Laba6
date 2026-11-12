
package com.example.Laba6.todoitem.web;

import com.example.Laba6.todoitem.TodoItem;
import com.example.Laba6.todoitem.TodoItemRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/")
public class TodoItemController {

    private final TodoItemRepository todoItemRepository;

    public TodoItemController(TodoItemRepository todoItemRepository) {
        this.todoItemRepository = todoItemRepository;
    }

    // Отображение главной страницы
    @GetMapping
    public String index(
            @RequestParam(defaultValue = "all") String filter,
            Model model) {

        List<TodoItem> allItems = todoItemRepository.findAll();
        List<TodoItem> todoItems;

        // Выбираем задачи в зависимости от фильтра
        switch (filter) {
            case "active":
                todoItems = allItems.stream()
                        .filter(item -> !item.isCompleted())
                        .toList();
                break;

            case "completed":
                todoItems = allItems.stream()
                        .filter(TodoItem::isCompleted)
                        .toList();
                break;

            default:
                todoItems = allItems;
                filter = "all";
        }

        // Считаем количество невыполненных задач
        long activeCount = allItems.stream()
                .filter(item -> !item.isCompleted())
                .count();

        model.addAttribute("todoItems", todoItems);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("newTodo", new TodoItem());
        model.addAttribute("filter", filter);

        return "index";
    }

    // Добавление новой задачи
    @PostMapping
    public String addTodo(
            @Valid @ModelAttribute("newTodo") TodoItem todoItem,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            List<TodoItem> allItems = todoItemRepository.findAll();

            long activeCount = allItems.stream()
                    .filter(item -> !item.isCompleted())
                    .count();

            model.addAttribute("todoItems", allItems);
            model.addAttribute("activeCount", activeCount);
            model.addAttribute("filter", "all");

            return "index";
        }

        todoItem.setCompleted(false);
        todoItemRepository.save(todoItem);

        return "redirect:/";
    }

    // Изменение статуса задачи
    @PutMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable Long id) {

        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Задача не найдена"));

        todoItem.setCompleted(!todoItem.isCompleted());
        todoItemRepository.save(todoItem);

        return "redirect:/";
    }

    // Удаление задачи
    @DeleteMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {

        todoItemRepository.deleteById(id);

        return "redirect:/";
    }
}
