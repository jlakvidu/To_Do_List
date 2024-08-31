package controller;

import javafx.collections.ObservableList;
import model.User;

import java.util.List;

public interface UserService {
    boolean addUser(User user);
    boolean deleteUser(int userId);
    List<User> getAll();
    int getNextId();
}
