package nerworking.rpc;

import DTO.CazDTO;
import domain.Caz;
import domain.Donatie;
import domain.Donator;
import domain.User;
import networking.LoginFailedExeption;
import networking.Observer;
import networking.Services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void addDonatie(Donatie donatie) {
        Request request = new Request.Builder().type(RequestType.DONATE).data(donatie).build();
        sendRequest(request);
        readResponse();
    }

    @Override
    public Map<Caz, Integer> getSumaCazuri() {
        Request request = new Request.Builder().type(RequestType.GET_CAZURI).build();
        sendRequest(request);
        Response response = readResponse();
        CazDTO[] cazDTOS = (CazDTO[])response.data();
        Map<Caz, Integer> map = new HashMap<>();
        for (CazDTO cazDTO : cazDTOS)
            map.put(cazDTO.getCaz(), cazDTO.getSuma());
        return map;
    }

    @Override
    public List<Donator> findByNume(String nume) {
        Request request = new Request.Builder().type(RequestType.FIND_NUME).data(nume).build();
        sendRequest(request);
        Response response = readResponse();
        Donator[] donatori = (Donator[])response.data();
        return Arrays.asList(donatori.clone());
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
        if (response.type() == ResponseType.ADDED_DONATION) {
            Donatie donatie = (Donatie)response.data();
            client.addedDonation(donatie);
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.ADDED_DONATION;
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
