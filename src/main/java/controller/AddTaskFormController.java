package controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.User;
import db.DBConnection;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ResourceBundle;

public class AddTaskFormController implements Initializable {

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private ListView<CheckBox> tastListView;

    private Connection connection;

    public AddTaskFormController() {
        try {
            this.connection = DBConnection.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void btnAddNewTaskOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/add_new_task_form.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(loader.load());
            AddNewTaskFormController newTaskController = loader.getController();
            newTaskController.setAddTaskFormController(this); // Pass the reference
            stage.setScene(scene);
            stage.setTitle("Add New Task");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadTasks(); // Ensure tasks are loaded after adding a new task
    }

    private void loadTasks() {
        String query = "SELECT * FROM active_tasks";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            tastListView.getItems().clear(); // Clear existing items
            while (rs.next()) {
                int id = rs.getInt("task_id");
                String description = rs.getString("task_description");
                CheckBox taskCheckBox = new CheckBox(description);
                taskCheckBox.setStyle("-fx-text-fill: #26205A;");

                // Handle the action event to transfer data
                taskCheckBox.setOnAction(e -> handleCheckBoxAction(taskCheckBox, id));

                tastListView.getItems().add(taskCheckBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void handleCheckBoxAction(CheckBox taskCheckBox, int taskId) {
        if (taskCheckBox.isSelected()) {
            // Fetch task details before transferring
            String selectQuery = "SELECT * FROM active_tasks WHERE task_id = ?";
            try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                selectStmt.setInt(1, taskId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    String title = rs.getString("task_title");
                    String description = rs.getString("task_description");
                    Date completionDate = rs.getDate("completion_date");

                    // Transfer the task to the completed_tasks table
                    insertTaskIntoCompletedTasks(taskId, title, description, completionDate);

                    // Remove the task from the active_tasks table
                    deleteTaskFromActiveTasks(taskId);

                    // Remove the task from the ListView
                    tastListView.getItems().remove(taskCheckBox);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertTaskIntoCompletedTasks(int taskId, String title, String description, Date completionDate) {
        String insertQuery = "INSERT INTO completed_tasks (task_id, task_title, task_description, completion_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, taskId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            if (completionDate != null) {
                stmt.setDate(4, completionDate); // Set the completion date
            } else {
                stmt.setNull(4, java.sql.Types.DATE); // Handle potential null dates
            }

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("You did it sucessfully....");
                alert.show();
            } else {
                System.err.println("Failed to insert task into completed_tasks.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteTaskFromActiveTasks(int taskId) {
        String deleteQuery = "DELETE FROM active_tasks WHERE task_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
            stmt.setInt(1, taskId);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Task deleted from active_tasks successfully.");
            } else {
                System.err.println("Failed to delete task from active_tasks.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadTasks(); // Load tasks when the controller is initialized
    }

    public ListView<CheckBox> getTaskListView() {
        return tastListView;
    }

    public void addCompletedTask(User completedUser) {
        // Implement if you need to handle completed tasks
    }

    @FXML
    public void btnAddOnAction(ActionEvent actionEvent) {
        // Implement task addition functionality if needed
    }
}
