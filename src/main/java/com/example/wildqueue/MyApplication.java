package com.example.wildqueue;

import com.example.wildqueue.dao.PriorityNumberDAO;
import com.example.wildqueue.dao.TellerWindowDAO;
import com.example.wildqueue.dao.TransactionDAO;
import com.example.wildqueue.dao.UserDAO;
import com.example.wildqueue.models.Student;
import com.example.wildqueue.models.Teller;
import com.example.wildqueue.models.User;
import com.example.wildqueue.utils.Serialize;
import com.example.wildqueue.utils.SessionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class MyApplication extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        generateDatabaseTables();

        primaryStage = stage;
        User user = Serialize.deserialize("user.ser");

        if (user != null) {
            SessionManager.setCurrentUser(user);
            if (user instanceof Student) {
                System.out.println("Auto-login as Student: " + user.getName());
                showStudentView();
            } else if(user instanceof Teller){
                System.out.println("Auto-login as Teller: " + user.getName());
                showTellerView();
            }
        } else {
            loadLoginView();
        }
    }

    private void generateDatabaseTables(){
        UserDAO.createUserTable();
        TransactionDAO.createTransactionTable();
        PriorityNumberDAO.createPriorityNumberTable();
        TellerWindowDAO.createTellerWindowTable();
    }

    public static void loadLoginView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("login-page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 360, 700);
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
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void showTellerView() throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(MyApplication.class.getResource("teller-homepage.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);

        primaryStage.setTitle("WildQueue Homepage");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}