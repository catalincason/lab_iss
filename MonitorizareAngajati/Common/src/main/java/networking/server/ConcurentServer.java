package server;

import networking.rpc.ClientReflectionWorker;
import networking.Services;

import java.net.Socket;

public class ConcurentServer extends AbstractConcurentServer {
    private Services server;
    public ConcurentServer(int port, Services server) {
        super(port);
        this.server = server;
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientReflectionWorker worker = new ClientReflectionWorker(server, client);
        Thread thread = new Thread(worker);
        return thread;
    }
}
