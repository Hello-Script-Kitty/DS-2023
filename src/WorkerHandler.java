import java.net.Socket;

public class WorkerHandler implements Runnable{

    private Socket worker;

    public WorkerHandler(Socket worker) {
        this.worker = worker;
    }
    @Override
    public void run() {

    }
}
