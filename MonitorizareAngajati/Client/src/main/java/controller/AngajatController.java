package controller;

import domain.*;
import javafx.application.Platform;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import networking.Observer;
import networking.Services;

import java.time.ZoneId;
import java.util.Date;

public class AngajatController implements Observer {
    private ObservableList<Sarcina> model = FXCollections.observableArrayList();

    private Services server;

    @FXML
    private TableView<Sarcina> tableViewSarcini;

    @FXML
    private TableColumn<Sarcina, String> tableColumnSarcina;

    @FXML
    private TableColumn<Sarcina, Date> tableColumnDeadline;

    @FXML
    private DatePicker datePickerCerere;

    private User currentUser;

    private Scene loginScene;

    public void setLoginScene(Scene loginScene) {
        this.loginScene = loginScene;
    }

    @FXML
    private void initialize() {
        tableColumnSarcina.setCellValueFactory(param -> new ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return param.getValue().getDescriere();
            }
        });
        tableColumnDeadline.setCellValueFactory(param -> new ObservableValueBase<Date>() {
            @Override
            public Date getValue() {
                return param.getValue().getDeadline();
            }
        });
        tableViewSarcini.setItems(model);
    }

    public void setServer(Services server) {
        this.server = server;
    }

    @Override
    public void userLoggedIn(UserDTO user) {

    }

    @Override
    public void userLoggedOut(UserDTO user) {

    }

    @Override
    public void sarcinaSent(Sarcina sarcina) {
        if (sarcina.getAngajat().equals(currentUser))
            Platform.runLater(() -> model.add(sarcina));
    }

    @Override
    public void cerereSent(Cerere cerere) {

    }

    @Override
    public void cerereUpdated(Cerere cerere) {
        if (cerere.getSarcina().getAngajat().equals(currentUser))
            Platform.runLater(() -> model.replaceAll(sarcina -> {
                if (sarcina.getId() == cerere.getSarcina().getId() &&
                        cerere.getStatus() == Status.ACCEPTAT)
                    return new Sarcina(sarcina.getAngajat(),
                            sarcina.getDescriere(), cerere.getDeadline());
                else
                    return sarcina;
            }));
    }

    public void getSarcini() {
        model.setAll(server.getSarcini(currentUser));
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void handleLogout(ActionEvent actionEvent) {
        server.logout(currentUser, this);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.show();
        Stage stage1 = (Stage)tableViewSarcini.getScene().getWindow();
        stage1.close();
    }

    public void terminaSarcina(ActionEvent actionEvent) {
        Sarcina sarcinaSelectata = tableViewSarcini.getSelectionModel().getSelectedItem();
        server.deleteSarcina(sarcinaSelectata);
        model.remove(sarcinaSelectata);
    }

    public void handleTrimiteCerere(ActionEvent actionEvent) {
        Date date = Date.from(datePickerCerere.getValue()
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Sarcina sarcinaSelectata = tableViewSarcini.getSelectionModel().getSelectedItem();
        Cerere cerere = new Cerere(sarcinaSelectata, date);
        server.sendCerere(cerere);
    }
}
