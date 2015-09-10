import java.io.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// This client is very abusable right now
// Very open to DDoS
// make the up arrow thing

public class Client implements Runnable
{
	private Socket socket = null;
	private Thread thread = null;
	private Scanner stdin = null;
	private PrintWriter out = null;
	private ClientThread client = null;
    //private BufferedReader in = null;
	String username = null;
	private int i = 0;

	public static void main(String[] args)
	{
		//System.setProperty("file.encoding", "UTF-16");

		//System.out.println("defaultCharacterEncoding by charSet: " + Charset.defaultCharset());

        Client clientStart = null;
        clientStart = new Client(args);
	}

	public Client(String[] args)
	{
		//String hostname = "192.168.1.8";
		//int port = 2014;
		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		username = args[2];
		makeConnection(hostname, port);
	}

	//public void recieve()
	{
	//	System.out.println();
	}

	public void run()
	{
        while(thread != null)
        {
            //try
            {
                out.println(stdin.nextLine());
                //out.flush();
            }
            //catch(IOException e)
            {
               // System.out.println("error at you know where");
            }
        }
	}

	public void makeConnection(String hostname, int port)
	{
		try
		{
			socket = new Socket(hostname, port);

			//out = new PrintWriter(socket.getOutputStream(), true);
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_16), true);
			out.println(username);
			//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			stdin = new Scanner(System.in);

			if(thread == null)
			{
				client = new ClientThread(this, socket);
				thread = new Thread(this);
				//System.out.println(client + "\n");
				thread.start();
			}
		}
		catch(IOException e)
		{
			System.out.println("Server not responding, try again later");
			System.exit(1);
		}
	}
}