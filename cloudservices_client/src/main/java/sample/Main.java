package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        //FXMLLoader loader = FXMLLoader.load(getClass().getResource("sample.fxml"));
        /*
        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 900, 700));
        primaryStage.show();
        */


        System.out.println(getClass().getResource("/fxml/bootstrap2overview.fxml"));
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/bootstrap2overview.fxml"));
        Parent root = (Parent) loader.load();


        Image applicationIcon = new Image(getClass().getResourceAsStream("/fxml/server.png"));
        primaryStage.getIcons().add(applicationIcon);
        primaryStage.setTitle("File Storage Service");
        primaryStage.setScene(new Scene(root, 950, 900));

        primaryStage.show();

    }


    public static void main(String[] args)
    {

        launch(args);

    }
}
