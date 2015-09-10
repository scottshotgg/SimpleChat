import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class ServerThread extends Thread
{
    private int threadID = -1;
    private Socket clientSocket = null;
    private Server server = null;
    public PrintWriter out = null;
    public PrintStream systemOut = null;
    private BufferedReader in = null;
    String username = null;
    int ID = -1;
    int messagesSent = 0;

    public ServerThread(int IDinput, Server serverInput, Socket socketInput)
    {
        //System.setProperty("file.encoding", "UTF-16");

        server = serverInput;
        clientSocket = socketInput;
        threadID = clientSocket.getPort();
        ID = IDinput;
        System.out.println(new Date() + " |-| serverthread: " + ID);
        try
        {
            systemOut = new PrintStream(System.out, true, "UTF-8");             // use this shit to print
        }
        catch(UnsupportedEncodingException e)
        {
            System.exit(1);
        }

        try
        {
            out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_16), true);                        // test russian and other shit with this
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_16));
        }
        catch(IOException e)
        {
            System.out.println("you know where this is");
        }
    }

    public void run()
    {
        String inputLine = null;
        try
        {
            username = in.readLine();   // may have to put this in the server

            if(server.validateUsername(username, ID, clientSocket))
            {
                inputLine = "";
                server.globalSend(threadID, new Date() + " |-| Welcome to the server, " + username + ", enjoy your stay and please follow the rules!", "SERVER");
                sendMessage("Type /r to view the rules.\n");
            }
            else
            {
                sendMessage("You have been denied from the server; username already in use.\nContact the admins if you think that this is a mistake (computer comparators don't make mistakes).");
            }
        }
        catch(IOException e)
        {
            System.out.println("username error");       //make ban list for certain usernames
        }

        //while(!inputLine.equalsIgnoreCase(".bye"))
        while(inputLine != null && !out.checkError())
        {
            if(messagesSent <= 10)
            {
                try
                {
                    inputLine = in.readLine();
                    if(inputLine.length() >= 1 && inputLine.charAt(0) == '/')
                    {
                        server.executeCommand(username, ID, inputLine);
                    }
                    else
                    {
                        systemOut.println(new Date() + " |-| " + clientSocket + ",ID [" + threadID + ", " + ID + "] " + username + ": " + inputLine);
                        //System.out.println(clientSocket + ",ID " + threadID + " " + username + ": " + inputLine);
                        server.globalSend(threadID, inputLine, username);
                    }
                    messagesSent++;
                }
                catch(IOException e)
                {
                    System.out.println("you know where this is1");
                }
                catch(NullPointerException e)
                {

                }
            }
            else
            {
                // make 60 a dynamic command line number
                sendMessage("\n------------------------------------------");
                sendMessage("SERVER: You have been timed out for a minute; too many messages, 60 per minute.\nSERVER: This is more than enough. Keep doing this and harsher consequences will follow; you have been warned.");
                sendMessage("SERVER: Validating your account will raise this limit and allow you to send more. Your amount of trust on the server can also raise or lower this value.");
                sendMessage("SERVER: If you send any messages during this time, they will send when the time is up. They will count towards the next 60 messages per minute.");
                sendMessage("------------------------------------------\n");

                messagesSent = 0;   // here is where you can make it dynamically increasing
                systemOut.println(new Date() + " |-| " + clientSocket + ",ID [" + threadID + ", " + ID + "] " + "SERVER" + ": " + username + " has been timed out for 60 seconds.");

                try
                {
                    //in.wait(10);
                    Thread.sleep(10000);
                }
                catch(InterruptedException e)
                {
                    System.out.println("Could not put thread to sleep " + ID + " " + threadID + " " + clientSocket);
                }
                catch(IllegalMonitorStateException e)
                {
                    systemOut.println(new Date() + " |-| " + clientSocket + ",ID [" + threadID + ", " + ID + "] " + "SERVER" + ": " + username + " has disconnected before the timeout.");
                }

                systemOut.println(new Date() + " |-| " + clientSocket + ",ID [" + threadID + ", " + ID + "] " + "SERVER" + ": " + username + " is no longer timed out.");
                sendMessage("Your timeout is up, you may now resume sending messages. ");
            }
        }
        try
        {
            clientSocket.close();
            server.removeClient(ID);
        }
        catch(IOException e)
        {
            System.out.println("Could not close the socket!\nPlease report this to a developer!");
        }

        server.globalSend(threadID, new Date() + " |-| -- " + username + " has logged off --", "SERVER");
        systemOut.println(new Date() + " |-|  -- " + username + " has logged off --");
    }

    public void sendMessage(String message)
    {
        out.println(message);
    }

    public void dialogMessage(String message)
    {
        systemOut.println(new Date() + " |-| " + clientSocket + ",ID [" + threadID + ", " + ID + "] " + "SERVER" + ": " + username + " has been timed out for 60 seconds.");   // alter this
    }

    public void sleepThread(int time) throws InterruptedException
    {
        System.out.println(username);
        Thread.sleep(time);
    }

    public boolean isAdmin()
    {

        return true;
    }
}