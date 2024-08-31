package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewTaskFormController implements Initializable {

    @FXML
    private TableColumn<User, Integer> colId;

    @FXML
    private TableColumn<User, String> colTitle;

    @FXML
    private TableColumn<User, String> colDescription;

    @FXML
    private TableColumn<User, String> colDate;

    @FXML
    private TableView<User> tblToDoList;

    private final UserService userService = new UserController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("completionDate"));
        loadTable();
    }

    private void loadTable() {
        ObservableList<User> userObservableList = (ObservableList<User>) userService.getAll();
        if (!userObservableList.isEmpty()) {
            tblToDoList.setItems(userObservableList);
        } else {
            System.out.println("No data found for completed tasks.");
        }
    }
}
