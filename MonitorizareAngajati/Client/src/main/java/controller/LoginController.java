package controller;

import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import networking.LoginFailedExeption;
import networking.Services;

public class LoginController {
    private Services server;

    @FXML
    private TextField textFieldNume;

    @FXML
    private PasswordField passwordField;

    private Scene sefScene;
    private Scene angajatScene;
    private SefController sefController;
    private AngajatController angajatController;

    public void setContext(Scene sefScene, Scene angajatScene, SefController sefController, AngajatController angajatController) {
        this.sefScene = sefScene;
        this.angajatScene = angajatScene;
        this.sefController = sefController;
        this.angajatController = angajatController;
    }

    public void setServer(Services server) {
        this.server = server;
    }

    public void handleLogin(ActionEvent actionEvent) {
        String nume = textFieldNume.getText();
        String parola = passwordField.getText();
        if (!nume.isEmpty() && !parola.isEmpty()) {
            try {
                User user = new User(nume, parola);
                if (nume.equals("sef")) {
                    server.login(user, sefController);

                    Stage stage = new Stage();
                    stage.setScene(sefScene);
                    sefController.getCereri();
                    sefController.setCurrentUser(user);
                    stage.show();
                    Stage stage1 = (Stage)textFieldNume.getScene().getWindow();
                    stage1.close();
                }
                else {
                    server.login(user, angajatController);

                    Stage stage = new Stage();
                    stage.setScene(angajatScene);
                    angajatController.setCurrentUser(user);
                    angajatController.getSarcini();
                    stage.show();
                    Stage stage1 = (Stage)textFieldNume.getScene().getWindow();
                    stage1.close();
                }
            }
            catch (LoginFailedExeption e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
