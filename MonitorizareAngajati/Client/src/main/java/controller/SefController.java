package controller;

import domain.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import networking.Observer;
import networking.Services;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;

public class SefController implements Observer {
    private ObservableList<UserDTO> modelUsers = FXCollections.observableArrayList();
    private ObservableList<Cerere> modelCereri = FXCollections.observableArrayList();

    private Services server;

    @FXML
    private TableView<UserDTO> tableViewAngajati;

    @FXML
    private TableColumn<UserDTO, String> tableColumnAngajat;

    @FXML
    private TableColumn<UserDTO, LocalTime> tableColumnTime;

    @FXML
    private TableView<Cerere> tableViewCereri;

    @FXML
    private TableColumn<Cerere, String> tableColumnSarcina;

    @FXML
    private TableColumn<Cerere, Date> tableColumnNewDeadline;

    private User currentUser;

    private Scene loginScene;

    public void setLoginScene(Scene loginScene) {
        this.loginScene = loginScene;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    @FXML
    private void initialize() {
        tableColumnAngajat.setCellValueFactory(param -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return param.getValue().getUser().getNume();
            }
        });
        tableColumnTime.setCellValueFactory(param -> new ObservableValueBase<LocalTime>() {
            @Override
            public LocalTime getValue() {
                return param.getValue().getTime();
            }
        });
        tableViewAngajati.setItems(modelUsers);

        tableColumnSarcina.setCellValueFactory(param -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return param.getValue().getSarcina().getDescriere();
            }
        });
        tableColumnNewDeadline.setCellValueFactory(param -> new ObservableValueBase<Date>() {
            @Override
            public Date getValue() {
                return param.getValue().getDeadline();
            }
        });
        tableViewCereri.setItems(modelCereri);
    }

    public void setServer(Services server) {
        this.server = server;
    }

    @Override
    public void userLoggedIn(UserDTO user) {
        Platform.runLater(() -> modelUsers.add(user));
    }

    @Override
    public void userLoggedOut(UserDTO user) {
        Platform.runLater(() -> modelUsers.remove(user));
    }

    @Override
    public void sarcinaSent(Sarcina sarcina) {

    }

    @Override
    public void cerereSent(Cerere cerere) {
        Platform.runLater(() -> modelCereri.add(cerere));
    }

    @Override
    public void cerereUpdated(Cerere cerere) {
        Platform.runLater(() -> modelCereri.replaceAll(entry -> {
            if (entry.getId() == cerere.getId())
                return cerere;
            else
                return entry;
        }));
    }

    public void getCereri() {
        modelCereri.setAll(server.getCereri());
    }

    public void handleLogout(ActionEvent actionEvent) {
        server.logout(currentUser, this);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.show();
        Stage stage1 = (Stage)tableViewCereri.getScene().getWindow();
        stage1.close();
    }

    public void handleTrimite(ActionEvent actionEvent) {
        User angajat = tableViewAngajati.getSelectionModel().getSelectedItem().getUser();
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/newSarcinaView.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            NewSarcinaController controller = loader.getController();
            controller.setServer(server);
            controller.setAngajat(angajat);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            controller.setStage(stage);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAccept(ActionEvent actionEvent) {
        Cerere selectedCerere = tableViewCereri.getSelectionModel().getSelectedItem();
        if (selectedCerere.getStatus() == null) {
            selectedCerere.setStatus(Status.ACCEPTAT);
            server.updateCerere(selectedCerere);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Cerere Accepted", "");
        }

    }

    public void handleReject(ActionEvent actionEvent) {
        Cerere selectedCerere = tableViewCereri.getSelectionModel().getSelectedItem();
        if (selectedCerere.getStatus() == null) {
            selectedCerere.setStatus(Status.RESPINS);
            server.updateCerere(selectedCerere);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION, "Cerere Rejected", "");
        }

    }
}
