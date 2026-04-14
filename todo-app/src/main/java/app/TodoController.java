package app;

import app.dao.TodoDAO;
import app.models.Todo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TodoController {

    @FXML
    private TextField inputField;

    @FXML
    private Button addButton;

    @FXML
    private ListView<HBox> listView;

    private TodoDAO dao;

    @FXML
    private void initialize() {
        initDatabase();
        addButton.setOnAction(e -> addItem());
        inputField.setOnAction(e -> addItem());
    }

    private void initDatabase() {
        String url = "jdbc:postgresql://db:5432/todo_db";
        String todoUser = "todo_user";
        String todoPass = "todo_pass123";
        try {
            Connection conn = DriverManager.getConnection(url, todoUser, todoPass);
            dao = new TodoDAO(conn);
            dao.init();

            // Load existing todos
            for (Todo t : dao.getAll()) {
                addTodoToListView(t);
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addTodoToListView(Todo todo) {
        CheckBox checkBox = new CheckBox();
        Label label = new Label(todo.getText());
        Button deleteBtn = new Button("X");

        deleteBtn.setVisible(false);

        checkBox.setSelected(todo.isCompleted());
        label.setStyle(todo.isCompleted() ? "-fx-strikethrough: true;" : "");

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            label.setStyle(newVal ? "-fx-strikethrough: true;" : "");
            todo.setCompleted(newVal);
            try { dao.update(todo); } catch (SQLException e) { e.printStackTrace(); }
        });

        HBox container = new HBox(10, checkBox, label, deleteBtn);
        container.setOnMouseEntered(e -> deleteBtn.setVisible(true));
        container.setOnMouseExited(e -> deleteBtn.setVisible(false));

        deleteBtn.setOnAction(e -> {
            listView.getItems().remove(container);
            try { dao.delete(todo.getId()); } catch (SQLException e1) { e1.printStackTrace(); }
        });

        listView.getItems().add(container);
    }

    private void addItem() {
        String text = inputField.getText();
        if (text != null && !text.trim().isEmpty()) {
            try {
                Todo todo = dao.add(text.trim());
                addTodoToListView(todo);
                inputField.clear();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}