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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientReflectionWorker implements Runnable, Observer {
    private Services server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public ClientReflectionWorker(Services server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request)request);
                if (response != null) {
                    sendResponse(response);
                }
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addedDonation(Donatie donatie) {
        Response response = new Response.Builder().type(ResponseType.ADDED_DONATION).data(donatie).build();
        sendResponse(response);
    }

    private Response handleRequest(Request request) {
        Response response=null;
        String handlerName="handle"+(request).type();
        try {
            Method method=this.getClass().getDeclaredMethod(handlerName, Request.class);
            response=(Response)method.invoke(this,request);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();

    private Response handleLOGIN(Request request) {
        User user = (User)request.data();
        try {
            server.login(user, this);
            return okResponse;
        }
        catch (LoginFailedExeption e) {
            connected = false;
            return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
        }
    }

    private Response handleGET_CAZURI(Request request) {
        Map<Caz, Integer> map = server.getSumaCazuri();
        List<CazDTO> cazDTOList = new ArrayList<>();
        map.forEach((caz, suma) -> cazDTOList.add(new CazDTO(caz, suma)));
        CazDTO[] cazDTOS = cazDTOList.toArray(new CazDTO[cazDTOList.size()]);
        return new Response.Builder().type(ResponseType.GET_CAZURI).data(cazDTOS).build();
    }

    private Response handleDONATE(Request request) {
        Donatie donatie = (Donatie)request.data();
        server.addDonatie(donatie);
        return okResponse;
    }

    private Response handleLOGOUT(Request request) {
        User user = (User)request.data();
        server.logout(user, this);
        connected = false;
        return okResponse;
    }

    private Response handleFIND_NUME(Request request) {
        String nume = (String)request.data();
        List<Donator> donatorList = server.findByNume(nume);
        Donator[] donatori = donatorList.toArray(new Donator[donatorList.size()]);
        return new Response.Builder().type(ResponseType.FIND_NUME).data(donatori).build();
    }

    private void sendResponse(Response response) {
        System.out.println("sending response "+response);
        try {
            output.writeObject(response);
            output.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
