import networking.Services;
import networking.server.AbstractServer;
import networking.server.ConcurentServer;
import networking.server.ServerException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import repository.CerereRepository;
import repository.SarcinaRepository;
import repository.UserRepository;
import repository.hibernate.CerereHBRepository;
import repository.hibernate.SarcinaHBRepository;
import repository.hibernate.UserHBRepository;

import java.io.IOException;
import java.util.Properties;

public class StartServer {

    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        Properties props = new Properties();
        try {
            props.load(StartServer.class.getResourceAsStream("/server.properties"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        initialize();

        UserRepository userRepository = new UserHBRepository(sessionFactory);
        SarcinaRepository sarcinaRepository = new SarcinaHBRepository(sessionFactory);
        CerereRepository cerereRepository = new CerereHBRepository(sessionFactory);

        Services service = new Service(userRepository, sarcinaRepository, cerereRepository);

        int port = 1234;
        try {
            port = Integer.parseInt(props.getProperty("server.port"));
        }
        catch (NumberFormatException e) {
            System.err.println("Wrong  Port Number" + e.getMessage());
            System.err.println("Using default port " + 1234);
        }
        System.out.println("Starting server on port: "+port);
        AbstractServer server = new ConcurentServer(port, service);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(ServerException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }

    private static void initialize() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            System.err.println("Exception "+e);
            StandardServiceRegistryBuilder.destroy( registry );
        }
    }
}
