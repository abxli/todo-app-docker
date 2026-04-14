package app.dao;

import app.models.Todo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoDAO {
    private final Connection conn;

    public TodoDAO(Connection conn) {
        this.conn = conn;
    }

    // Create table if not exists
    public void init() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS todos (" +
                     "id SERIAL PRIMARY KEY," +
                     "text VARCHAR(255) NOT NULL," +
                     "completed BOOLEAN DEFAULT FALSE)";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    public List<Todo> getAll() throws SQLException {
        List<Todo> todos = new ArrayList<>();
        String sql = "SELECT id, text, completed FROM todos ORDER BY id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                todos.add(new Todo(rs.getInt("id"), rs.getString("text"), rs.getBoolean("completed")));
            }
        }
        return todos;
    }

    public Todo add(String text) throws SQLException {
        String sql = "INSERT INTO todos(text) VALUES(?) RETURNING id, text, completed";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, text);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Todo(rs.getInt("id"), rs.getString("text"), rs.getBoolean("completed"));
            }
        }
        return null;
    }

    public void update(Todo todo) throws SQLException {
        String sql = "UPDATE todos SET text = ?, completed = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, todo.getText());
            ps.setBoolean(2, todo.isCompleted());
            ps.setInt(3, todo.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM todos WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}