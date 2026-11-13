
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

        addAttributes(model, todoItems, allItems, filter);

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

            addAttributes(model, allItems, allItems, "all");

            return "index";
        }

        todoItem.setCompleted(false);
        todoItemRepository.save(todoItem);

        return "redirect:/";
    }

    // Изменение статуса одной задачи
    @PutMapping("/toggle/{id}")
    public String toggleTodo(@PathVariable Long id) {

        TodoItem todoItem = todoItemRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Задача не найдена"));

        todoItem.setCompleted(!todoItem.isCompleted());
        todoItemRepository.save(todoItem);

        return "redirect:/";
    }

    // Удаление одной задачи
    @DeleteMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {

        todoItemRepository.deleteById(id);

        return "redirect:/";
    }

    // Подготовка данных для отображения страницы
    private void addAttributes(
            Model model,
            List<TodoItem> todoItems,
            List<TodoItem> allItems,
            String filter) {

        long activeCount = todoItemRepository.countAllByCompleted(false);
        int completedCount = todoItemRepository.countAllByCompleted(true);

        model.addAttribute("todoItems", todoItems);
        model.addAttribute("activeCount", activeCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("newTodo", new TodoItem());
        model.addAttribute("filter", filter);
    }

    // Удаление всех выполненных задач
    @DeleteMapping("/completed")
    public String deleteCompletedItems() {

        List<TodoItem> completedItems =
                todoItemRepository.findAllByCompleted(true);

        for (TodoItem item : completedItems) {
            todoItemRepository.deleteById(item.getId());
        }

        return "redirect:/";
    }

    // Изменение статуса всех задач
    @PutMapping("/toggle-all")
    public String toggleAll() {

        List<TodoItem> todoItems = todoItemRepository.findAll();

        for (TodoItem todoItem : todoItems) {
            todoItem.setCompleted(!todoItem.isCompleted());
            todoItemRepository.save(todoItem);
        }

        return "redirect:/";
    }
}
