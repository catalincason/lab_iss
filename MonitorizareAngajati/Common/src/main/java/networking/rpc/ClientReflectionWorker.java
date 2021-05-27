package networking.rpc;

import domain.Cerere;
import domain.Sarcina;
import domain.User;
import domain.UserDTO;
import networking.LoginFailedExeption;
import networking.Services;
import networking.Observer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.List;

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

    private Response handleLOGOUT(Request request) {
        User user = (User)request.data();
        server.logout(user, this);
        connected = false;
        return okResponse;
    }

    private Response handleSEND_SARCINA(Request request) {
        Sarcina sarcina = (Sarcina)request.data();
        server.sendSarcina(sarcina);
        return okResponse;
    }

    private Response handleGET_SARCINI(Request request) {
        List<Sarcina> sarcini = server.getSarcini((User)request.data());
        Sarcina[] sarcinas = sarcini.toArray(new Sarcina[0]);
        return new Response.Builder().type(ResponseType.GET_SARCINI).data(sarcinas).build();
    }

    private Response handleDELETE_SARCINA(Request request) {
        server.deleteSarcina((Sarcina)request.data());
        return okResponse;
    }

    private Response handleSEND_CERERE(Request request) {
        server.sendCerere((Cerere)request.data());
        return okResponse;
    }

    private Response handleGET_CERERI(Request request) {
        List<Cerere> cerereList = server.getCereri();
        Cerere[] cereri = cerereList.toArray(new Cerere[0]);
        return new Response.Builder().type(ResponseType.GET_CERERI).data(cereri).build();
    }

    private Response handleUPDATE_CERERE(Request request) {
        server.updateCerere((Cerere)request.data());
        return okResponse;
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

    @Override
    public void userLoggedIn(UserDTO user) {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_IN).data(user).build();
        sendResponse(response);
    }

    @Override
    public void userLoggedOut(UserDTO user) {
        Response response = new Response.Builder().type(ResponseType.USER_LOGGED_OUT).data(user).build();
        sendResponse(response);
    }

    @Override
    public void sarcinaSent(Sarcina sarcina) {
        Response response = new Response.Builder().type(ResponseType.SARCINA_SENT).data(sarcina).build();
        sendResponse(response);
    }

    @Override
    public void cerereSent(Cerere cerere) {
        Response response = new Response.Builder().type(ResponseType.CERERE_SENT).data(cerere).build();
        sendResponse(response);
    }

    @Override
    public void cerereUpdated(Cerere cerere) {
        Response response = new Response.Builder().type(ResponseType.CERERE_UPDATED).data(cerere).build();
        sendResponse(response);
    }
}
