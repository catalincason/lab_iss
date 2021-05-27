package networking.rpc;

import domain.Cerere;
import domain.Sarcina;
import domain.User;
import domain.UserDTO;
import networking.LoginFailedExeption;
import networking.Observer;
import networking.Services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ServicesProxy implements Services {
    private String host;
    private int port;

    private Observer client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public ServicesProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingDeque<>();
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request) {
        try {
            output.writeObject(request);
            output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response readResponse() {
        Response response=null;
        try {
            response=qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.USER_LOGGED_IN)
            client.userLoggedIn((UserDTO)response.data());
        else if (response.type() == ResponseType.USER_LOGGED_OUT)
            client.userLoggedOut((UserDTO)response.data());
        else if (response.type() == ResponseType.SARCINA_SENT)
            client.sarcinaSent((Sarcina)response.data());
        else if (response.type() == ResponseType.CERERE_SENT)
            client.cerereSent((Cerere)response.data());
        else if (response.type() == ResponseType.CERERE_UPDATED)
            client.cerereUpdated((Cerere)response.data());
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.USER_LOGGED_IN ||
                response.type() == ResponseType.USER_LOGGED_OUT ||
                response.type() == ResponseType.SARCINA_SENT ||
                response.type() == ResponseType.CERERE_SENT ||
                response.type() == ResponseType.CERERE_UPDATED;
    }

    @Override
    public void login(User user, Observer client) throws LoginFailedExeption {
        initializeConnection();
        Request request = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return;
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new LoginFailedExeption(err);
        }
    }

    @Override
    public void logout(User user, Observer client) {
        Request request = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(request);
        readResponse();
        closeConnection();
    }

    @Override
    public void sendSarcina(Sarcina sarcina) {
        Request request = new Request.Builder().type(RequestType.SEND_SARCINA).data(sarcina).build();
        sendRequest(request);
        readResponse();
    }

    @Override
    public List<Sarcina> getSarcini(User user) {
        Request request = new Request.Builder().type(RequestType.GET_SARCINI).data(user).build();
        sendRequest(request);
        Response response = readResponse();
        Sarcina[] sarcini = (Sarcina[])response.data();
        return Arrays.asList(sarcini);
    }

    @Override
    public void deleteSarcina(Sarcina sarcina) {
        Request request = new Request.Builder().type(RequestType.DELETE_SARCINA).data(sarcina).build();
        sendRequest(request);
        readResponse();
    }

    @Override
    public void sendCerere(Cerere cerere) {
        Request request = new Request.Builder().type(RequestType.SEND_CERERE).data(cerere).build();
        sendRequest(request);
        readResponse();
    }

    @Override
    public List<Cerere> getCereri() {
        Request request = new Request.Builder().type(RequestType.GET_CERERI).build();
        sendRequest(request);
        Response response = readResponse();
        Cerere[] cereri = (Cerere[])response.data();
        return Arrays.asList(cereri);
    }

    @Override
    public void updateCerere(Cerere cerere) {
        Request request = new Request.Builder().type(RequestType.UPDATE_CERERE).data(cerere).build();
        sendRequest(request);
        readResponse();
    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }
                    else{
                        try {
                            qresponses.put((Response)response);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
                catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
