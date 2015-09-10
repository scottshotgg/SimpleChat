import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ClientThread extends Thread
{
    private Socket socket = null;
    private Client client = null;
    BufferedReader in = null;

    public ClientThread(Client clientInput, Socket socketInput)
    {
        //System.setProperty("file.encoding", "UTF-16");
        //System.out.println("defaultCharacterEncoding by charSet: " + Charset.defaultCharset());

        client = clientInput;
        socket = socketInput;

        try
        {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_16));
        }
        catch(IOException e)
        {
            System.out.println(socket);
        }

        start();
    }

    public void run()
    {
        String line = "";
        while(line != null)
        {
            try
            {
                line = in.readLine();
                System.out.println(line);
            }
            catch(IOException e)
            {
                System.out.println("Didn't get anything!");
            }
        }
        System.out.println("Server not responding...");
        System.exit(1);
    }
}
