import java.net.Socket;

public class UserHandler implements Runnable{

    private final Socket user;

    public UserHandler(Socket user) {
        this.user = user;
    }

    @Override
    public void run() {

    }
}
