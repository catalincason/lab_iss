package controller;

import domain.Sarcina;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import networking.Services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class NewSarcinaController {
    @FXML
    private TextField textFieldSarcina;

    @FXML
    private DatePicker datePickerDeadline;

    private User angajat;

    private Services server;

    private Stage stage;

    public void setAngajat(User angajat) {
        this.angajat = angajat;
    }

    public void setServer(Services server) {
        this.server = server;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void handleTrimite(ActionEvent actionEvent) {
        if (!textFieldSarcina.getText().isEmpty() && !datePickerDeadline.getEditor().getText().isEmpty()) {
            String descriere = textFieldSarcina.getText();
            Date date = Date.from(datePickerDeadline.getValue()
                .atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Sarcina sarcina = new Sarcina(angajat, descriere, date);
            server.sendSarcina(sarcina);
            MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION, "Confirmation", "Sarcina trimisa");
            stage.close();
        }
        else
            MessageAlert.showErrorMessage(stage, "Complete the fields");
    }
}
