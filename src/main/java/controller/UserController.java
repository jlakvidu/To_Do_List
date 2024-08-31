package controller;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.User;

import java.sql.*;

public class UserController implements UserService{

    @Override
    public boolean addUser(User user) {
        try {
            String SQL = "INSERT INTO active_tasks VALUES(?,?,?,?)";
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setObject(1,user.getId());
            preparedStatement.setObject(2,user.getTitle());
            preparedStatement.setObject(3,user.getDescription());
            preparedStatement.setObject(4, Date.valueOf(user.getCompletionDate()));
            return preparedStatement.executeUpdate()>0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,"Error : "+e.getMessage()).show();
        }
        return false;
    }

    @Override
    public boolean deleteUser(int userId) {
        String SQL = "DELETE FROM active_tasks WHERE task_id = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {

            preparedStatement.setInt(1, userId);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
            return false;
        }
    }

    public ObservableList<User> getAll() {
        ObservableList<User> userObservableList = FXCollections.observableArrayList();
        try {
            String SQL = "SELECT * FROM completed_tasks";
            Connection connection = DBConnection.getInstance().getConnection();
            System.out.println(connection);
            PreparedStatement psTm = connection.prepareStatement(SQL);
            ResultSet resultSet = psTm.executeQuery();
            while (resultSet.next()) {
                User customer = new User(
                        resultSet.getInt("task_id"),
                        resultSet.getString("task_title"),
                        resultSet.getString("task_description"),
                        resultSet.getDate("completion_date").toLocalDate()
                );
                userObservableList.add(customer);
                System.out.println(customer);
            }
            return userObservableList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getNextId() {
        return 0;
    }
}