package com.example.wildqueue;

import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.Serialize;
import com.example.wildqueue.utils.SessionManager;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MyApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        primaryStage = stage;
        User user = Serialize.deserialize("user.ser");

        if (user != null) {
            if (user instanceof Student) {
                System.out.println("Auto-login as Student: " + user.getName());
                SessionManager.setCurrentUser(user);
                showStudentView();
            }
        } else {
            loadLoginView();
        }
    }

    public static void loadLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 700);
        UserDAO.createUserTable();
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showStudentView() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("student-main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 700);

        primaryStage.setTitle("WildQueue Homepage");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}