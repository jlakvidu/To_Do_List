package controller;

import com.jfoenix.controls.JFXTextField;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import model.User;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AddNewTaskFormController implements Initializable {

    @FXML
    private DatePicker completionDate;

    @FXML
    private JFXTextField txtTitle;

    @FXML
    private JFXTextField txtDescription;

    @FXML
    private JFXTextField txtId;

    private UserService userService = new UserController(); // Assuming UserService interface is implemented by UserController

    private AddTaskFormController addTaskFormController;

    public void setAddTaskFormController(AddTaskFormController controller) {
        this.addTaskFormController = controller;
    }

    @FXML
    public void btnAddOnAction(ActionEvent actionEvent) {
        String taskDescription = txtDescription.getText();
        String taskTitle = txtTitle.getText();

        // Validate input
        if (taskDescription.trim().isEmpty() || taskTitle.trim().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Task title or description cannot be empty.").show();
            return;
        }

        // Generate next ID for the task
        int newTaskId = generateNextID();
        txtId.setText(String.valueOf(newTaskId));

        CheckBox taskCheckBox = new CheckBox(taskDescription);
        taskCheckBox.setStyle("-fx-text-fill: #26205A;");
        taskCheckBox.setOnAction(e -> handleCheckBoxAction(taskCheckBox));

        // Add task to ListView in AddTaskFormController
        if (addTaskFormController != null) {
            addTaskFormController.getTaskListView().getItems().add(taskCheckBox);
        } else {
            System.err.println("AddTaskFormController is not set.");
        }

        // Create a User object for the task
        User user = new User(
                newTaskId,
                taskTitle,
                taskDescription,
                completionDate.getValue()
        );

        // Add user/task to the database
        if (userService.addUser(user)) {
            new Alert(Alert.AlertType.INFORMATION, "Task added successfully!").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Task could not be added.").show();
        }

        // Clear input fields
        txtDescription.clear();
        txtTitle.clear();
    }

    private void handleCheckBoxAction(CheckBox taskCheckBox) {
        if (taskCheckBox.isSelected()) {
            // Remove the task from the ListView
            if (addTaskFormController != null) {
                addTaskFormController.getTaskListView().getItems().remove(taskCheckBox);
            }

            // Create a User object for the completed task
            User completedUser = new User(
                    Integer.parseInt(txtId.getText()), // Ensure this ID is valid
                    txtTitle.getText(),
                    taskCheckBox.getText(),
                    completionDate.getValue()
            );

            // Optionally, add the completed task to a table or another list in another window
            if (addTaskFormController != null) {
                addTaskFormController.addCompletedTask(completedUser);
            }
        }
    }

    public static int generateNextID() {
        int nextID = 1; // Default starting ID

        try {
            // Get database connection
            Connection connection = DBConnection.getInstance().getConnection();

            // Create SQL query to get the maximum ID
            String query = "SELECT MAX(task_id) AS max_id FROM completed_tasks"; // Corrected to use the correct table name
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                nextID = resultSet.getInt("max_id") + 1; // Increment max ID by 1
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nextID;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        txtId.setText(String.valueOf(generateNextID())); // Initialize ID field with the next available ID
    }
}
