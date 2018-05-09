package sample;

import com.jfoenix.controls.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import static sample.Database.connect;


public class LoginController implements Initializable {

    @FXML
    private JFXButton loginButton;

    @FXML
    private JFXButton createACCButton;

    @FXML
    private JFXTextField userTextField;

    @FXML
    private JFXPasswordField passwordTextField;

    @FXML
    private ImageView loginImage;

    @FXML
    private JFXProgressBar loginProgress;

    @FXML
    private JFXButton submitButton;

    @FXML
    private JFXButton cancelCreateUser;

    @FXML
    private JFXPasswordField confirmPasswordTextField;

    @FXML
    private ImageView createUserImage;

    @FXML
    private Label loginLabel;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Connecting to the database
        Database db = new Database();
        Connection conn = connect();

        //Hide the create user button

        submitButton.setVisible(false);
        confirmPasswordTextField.setVisible(false);
        createUserImage.setVisible(false);
        cancelCreateUser.setVisible(false);

        createACCButton.setOnAction((e -> {
            loginLabel.setText("CREATE ACCOUNT");
            submitButton.setVisible(true);
            confirmPasswordTextField.setVisible(true);
            createUserImage.setVisible(true);
            cancelCreateUser.setVisible(true);
        }));

        submitButton.setOnAction((e -> {
            if (!doesLoginExist(conn)) {
                if (passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
                    loginProgress.setVisible(true);
                    PauseTransition pt = new PauseTransition();
                    pt.setDuration(Duration.seconds(1));
                    pt.setOnFinished(ev -> {
                        db.insertNewUser(conn, userTextField, passwordTextField);
                        loginLabel.setText("LOGIN");
                        submitButton.setVisible(false);
                        confirmPasswordTextField.setVisible(false);
                        createUserImage.setVisible(false);
                        loginProgress.setVisible(false);
                    });
                    pt.play();

                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Passwords do not match.");
                    alert.setOnHidden(evt -> {});

                    alert.show();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText("User already exists.");
                alert.setOnHidden(evt -> {});

                alert.show();
            }


        }));



        cancelCreateUser.setOnAction((e -> {
            loginLabel.setText("LOGIN");
            submitButton.setVisible(false);
            confirmPasswordTextField.setVisible(false);
            createUserImage.setVisible(false);
            loginProgress.setVisible(false);
            cancelCreateUser.setVisible(false);
        }));

        loginProgress.setVisible(false);

        passwordTextField.setOnAction(e -> {
            loginButtonPressed(db, conn);
        });

        loginButton.setOnAction((e ->{
            loginButtonPressed(db, conn);

        }));
    }



    public void loginButtonPressed(Database db, Connection conn) {
        loginProgress.setVisible(true);
        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(1));
        pt.setOnFinished(ev -> {


        if (isLogin(conn, userTextField.getText(), passwordTextField.getText())) {
            int userid = db.getUserID(conn, userTextField.getText());
            db.updateCurrentUser(conn,userid);
            loadMainComponent();
            try {
                conn.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Wrong username/password");
            alert.setOnHidden(evt -> {});

            alert.show();

        }

        loginProgress.setVisible(false);
        });
        pt.play();
    }

    private void loadMainComponent() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sample.fxml"));
            Parent root = (Parent) fxmlLoader.load();
            Stage loginStage = new Stage();
            Scene afterLoginScene = new Scene(root, 1600, 800);
            loginStage.setScene(afterLoginScene);
            afterLoginScene.getStylesheets().add("styles.css");
            loginStage.show();
            Main.mainstg.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public boolean isLogin(Connection conn, String username, String password) {
        String query = "SELECT * FROM users where username = ? and password = ?";
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            return false;
        }
    }


    public boolean doesLoginExist(Connection conn) {
        String username = userTextField.getText();
        String query = "SELECT * FROM users where username = ?";
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
        catch (SQLException e)
        {
            return false;
        }
    }
}
