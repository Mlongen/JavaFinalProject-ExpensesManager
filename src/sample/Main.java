package sample;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

public class Main extends Application {

    private TableView<Database> table;
    private ObservableList<Database> data;

    static Stage mainstg;
    @Override
    public void start(Stage primaryStage) throws Exception{
        this.mainstg = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("logincontroller.fxml"));
        primaryStage.setTitle("Expenses manager");
        Scene mainScene = new Scene(root, 800, 600);
        mainScene.getStylesheets().add("loginstyles.css");
        primaryStage.setScene(mainScene);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
