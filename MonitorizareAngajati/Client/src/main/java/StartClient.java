import controller.AngajatController;
import controller.LoginController;
import controller.SefController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import networking.Services;
import networking.rpc.ServicesProxy;

import java.io.IOException;
import java.util.Properties;

public class StartClient extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Properties props = new Properties();
        try {
            props.load(StartClient.class.getResourceAsStream("/server.properties"));
        }
        catch (IOException e) {
            System.err.println(e);
        }

        String ip = props.getProperty("server.host", "localhost");

        int port = 1234;
        try {
            port = Integer.parseInt(props.getProperty("server.port"));
        }
        catch (NumberFormatException e) {
            System.err.println("Wrong port number " + e.getMessage());
            System.out.println("Using default port: " + 1234);
        }

        Services server = new ServicesProxy(ip, port);

        try {
            FXMLLoader loader1 = new FXMLLoader();
            loader1.setLocation(getClass().getResource("/views/sefView.fxml"));
            Parent croot = loader1.load();
            Scene sefScene = new Scene(croot);
            SefController sefController = loader1.getController();
            sefController.setServer(server);

            FXMLLoader loader2 = new FXMLLoader();
            loader2.setLocation(getClass().getResource("/views/angajatView.fxml"));
            Parent croot2 = loader2.load();
            Scene angajatScene = new Scene(croot2);
            AngajatController angajatController = loader2.getController();
            angajatController.setServer(server);

            FXMLLoader loader3 = new FXMLLoader();
            loader3.setLocation(getClass().getResource("/views/loginView.fxml"));
            Parent root = loader3.load();

            LoginController loginController = loader3.getController();
            loginController.setContext(sefScene, angajatScene, sefController, angajatController);
            loginController.setServer(server);
            Scene loginScene = new Scene(root);
            sefController.setLoginScene(loginScene);
            angajatController.setLoginScene(loginScene);
            primaryStage.setScene(loginScene);
            primaryStage.show();
        }
        catch (IOException e) {
            System.err.println(e);
        }
    }
}
