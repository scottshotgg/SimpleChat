import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

// try making a "freedom server"
// make a tab thing
// make a whitelist and blacklist feature
// make log file - working on this
// make custom rooms
// look up person

public class Server implements Runnable
{
    private ServerThread[] client = null;
    private Thread thread = null;
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null;
    private int i = 0;
    public PrintStream systemOut = null;
    private BufferedReader in = null;
    File log = null;
    SimpleDateFormat date = new SimpleDateFormat("EEE - dd.MM.yy - HH.mm.ss");  // Check this
    BufferedWriter logWriter = null;

    public static void main(String[] args)
    {
        if(args.length > 2)
        {
            System.err.println("Error: Incorrect usage; java Server [port_number] [max_clients]");
            System.exit(1);
        }
        else
        {
            Server server = new Server(args);
        }
    }

    public Server(String args[])
    {
        makeLogFile(); // Make sure this is based on a command line argument
        // Make sure you can pass in a directory, -r, -d/-a flags for relevant or w/e


        try
        {
            int portNumber = Integer.parseInt(args[0]);
            client = new ServerThread[Integer.parseInt(args[1])];
            systemOut = new PrintStream(System.out, true, "UTF-8");             // use this shit to print

            System.out.println(new Date() + " |-| Binding to port " + portNumber);
            writeLog(new Date() + " |-| Binding to port " + portNumber);    // Check this for the date format

            serverSocket = new ServerSocket(portNumber);
            thread = new Thread(this);
            thread.start();
        }
        catch(IOException e)
        {
            writeLog("IOException error: " + e);
            System.out.println("IOException error: " + e);
            // NEED TO ADD CORRECT FILE CLOSING
            System.exit(1);
        }
        catch(ArrayIndexOutOfBoundsException e)
        {
            switch(e.toString().charAt(e.toString().length() - 1))
            {
                case '0':
                {
                    writeLog("Port number not specified\nUsage: java Server [port_number] [max_clients]");
                    System.out.println("Port number not specified\nUsage: java Server [port_number] [max_clients]");
                }
                break;

                case '1':
                {
                    writeLog("Max number of clients not specified\nUsage: java Server [port_number] [max_clients]");
                    System.out.println("Max number of clients not specified\nUsage: java Server [port_number] [max_clients]");
                }
                break;

                default:
                {
                    writeLog("\n\nPlease give the following message to an admin for further assistance: \n");
                    System.out.println("\n\nPlease give the following message to an admin for further assistance: \n");

                    writeLog(e + "\n" + e.getStackTrace() + " " + e.getLocalizedMessage() + "\n\n");
                    System.out.println(e + "\n" + e.getStackTrace() + " " + e.getLocalizedMessage() + "\n\n");
                }
            }
        }
    }

    @Override
    public void run()
    {
        while(thread != null)
            try
            {
                // Base these comments on if the log is active or not

                System.out.println(new Date() + " |-| Waiting for client....");
                writeLog(new Date() + " |-| Waiting for client....");
                //if(validateUsername())
                //{
                clientSocket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_16));
                //String username = in.readLine();
                //System.out.println(validateUsername(username));

                //}
                System.out.println(new Date() + " |-| Client accepted on IP " + clientSocket.getRemoteSocketAddress().toString());

                int ID = findIDForClient();        // make this search through the array and return the first null value
                client[ID] = new ServerThread(ID, this, clientSocket);
                client[ID].start();
                if(ID == i)
                    i++;

                //while((inputLine = in.readLine()) != null)

                //clientSocket.close();
            }
            catch(IOException e)
            {
                System.out.println("IOException error: " + e);
                System.exit(1);
            }
    }

    public void globalSend(int ID, String message, String username)
    {
        for(int j = 0; j < i; j++)
        {
            if(client[j] != null)
            {
                client[j].sendMessage(new Date() + " |-| " + username + ": " + message);
            }
        }
    }

    public void executeCommand(String username, int ID, String inputLine)        // check this for permissions
    {
        //String command[] = new String[3];

        String command[] = inputLine.split(" ", 3);             // how can admin enter weird name without copy pasta from mouse
        //command[1] = inputLine.split("-", 0);
        //command[2] = inputLine.split("-", 0);

        /*if(command[3] == null)aaaaaaaaa
        {
            if(command[2] == null)
            {
                systemOut.println(new Date() + " |-| COMMAND: " + username + " executed " + command[0]);
            }
            else
            {
                systemOut.println(new Date() + " |-| COMMAND: " + username + " executed " + command[0] + " " command[1]);
            }
        }*/

        systemOut.println(new Date() + " |-| COMMAND: " + username + " executed " + inputLine);       // handle an arrayindexoutofbounds here to make sure that clients do not get stuck

        switch(command[0])       // at some point in the future, we should try and make a file format for this and it can pull from there
        {
            case "/la":
            {
                client[ID].sendMessage("\nLIST ALL:\n--------");
                for(int j = 0; j < i; j++)
                {
                    //if(client[j] != null)         // either we handle big clients with another file or suck it up and use a giant fucking array with no client replacing.... or we remove this feature :(
                    {
                        client[ID].sendMessage(client[j].username);     //make this have something indicating added on friends
                    }
                }
                //client[ID].sendMessage("--------\n");
                client[ID].sendMessage("");
            }
            break;

            case "/lp":
            {
                client[ID].sendMessage("\nLIST PEOPLE:\n--------");
                for(int j = 0; j < i; j++)
                {
                    if(!client[j].out.checkError())
                    {
                        client[ID].sendMessage(client[j].username);     //make this have something indicating added on friends
                    }
                }
                //client[ID].sendMessage("--------\n");
                client[ID].sendMessage("");
            }
            break;

            case "/r":
            {
                client[ID].sendMessage("\nRULES:\n--------\n1. Be nice, lol\n2. Don't fuck with the admins\n3. Be super cereal when its time to.\n:^)");
                client[ID].sendMessage("");
            }

            case "/pm":     // private message
            {
                int clientID = -1;          // I dont nknow if i need this or not
                clientID = resolveClientID(command[1]);
                client[clientID].sendMessage(username + ": " + command[2]);     // This needs to go to a specific person. I THINK I HAS FIXED
            }
            break;

            case "/c":          // definitely check privileges here
            {
                client[ID].sendMessage("\nCOMMANDS:\n--------\n/lp - List connected people\n/la - List all people\n/r - List rules\n/c - List all (privileged) commands");
                client[ID].sendMessage("");
            }
            break;

            case "/timeout":        // we need to get the client username and validate privileges and send them a message whether found or not
            {
                int time = Integer.parseInt(command[2]);         // make this look nicer -- this might be * 1000
                int clientID = -1;
                try
                {
                    if(time > 0 && time < 900)      // Max timeout 15 min?
                    {
                        clientID = resolveClientID(command[1]);
                        timeout(time * 1000, command[1], clientID);
                        client[clientID].sleepThread(time * 1000);           // what happens when stupid shit with integer and check negative here      // maybe add .sleep back
                        systemOut.println(new Date() + " [-] " + command[1] + " has been timed out for " + time + " seconds by " + username);
                        client[clientID].sendMessage(new Date() + " |-| SERVER: You have now been resumed.");
                    }
                    else if(time < 0)
                    {
                        client[ID].sendMessage("You can time someone out for a negative amount of time....");
                    }
                    else
                    {
                        client[ID].sendMessage("You can only time people out for 15 minutes (900 seconds); if you need more than this, use a temp ban.");
                    }
                }
                catch(ArrayIndexOutOfBoundsException e)
                {
                    switch(e.toString().charAt(e.toString().length() - 1))
                    {
                        case '1':
                        {
                            client[ID].sendMessage("User not found.");
                        }
                        break;

                        case '2':
                        {
                            client[ID].sendMessage("You do not have privileges to timeout this user.");
                        }
                        break;

                        default:
                        {
                            // ???
                        }
                    }
                    // make shit for -1 is user not found, -2 is user cannot be timedout by you, etc
                }
                catch(InterruptedException e)
                {
                    // put some shit here later
                }
            }
            break;

            case "/find":
            {
                int find = resolveClientID(command[1]);

                if(find != -1)
                {
                    client[ID].sendMessage("\"" + command[1] + "\" found, ID: " + find);      // Make a corrolary
                }
            }
            break;

            default:
            {
                client[ID].sendMessage("Command not recognized:\nUse \"/c\" to see all commands.");
            }
        }
    }

    public void removeClient(int ID)
    {
        //client[ID].username = null;     // do i need this?
        client[ID] = null;  // dedicated threads. LOL
    }

    public int findIDForClient()    // This is for finding an assignable ID, not for resolving an ID from a username
    {
        for(int j = 0; j < i; j++)      // try this, maybe with a while loop is better
        {
            if(client[j] == null)           // maybe make an updater that tracks where open spots are, array
            {
                //System.out.println("ID: " + id);  // Do I need this?
                return j;
            }
        }
        return i;
    }

    public boolean validateUsername(String username, int ID, Socket clientSocket)
    {
        for(int j = 0; j < i; j++)
        {
            if(client[j] != null && client[j].ID != ID && username.equals(client[j].username))   //might need this is we remove client[ID].username = null
            {
                systemOut.println(new Date() + " |-| ERROR: " + clientSocket + " tried to log onto the server with the same name as someone else; " + username);
                return false;
            }
        }

        systemOut.println(username + " has been validated.");
        return true;
    }

    public int resolveClientID(String usernameFromCommand)
    {
        for(int j = 0; j < i; j++)
        {
            if(client[j] != null && usernameFromCommand.equals(client[j].username))       // this is gonna need to be checked first
            {
                /*if(!client[j].isAdmin())
                {

                }
                else
                {
                    return j;
                }*/
                return j;
            }
        }
        return -1;
    }

    public void timeout(int time, String usernameFromCommand, int ID)
    {
        client[ID].sendMessage(new Date() + " |-| SERVER: You have been timed out by an admin for " + (time / 1000) + " second/s");       // maybe print the rules here or tell them the command for that
        systemOut.println(new Date() + " |-| Found user: " + usernameFromCommand + ", ID: " + ID); // for sleep?
    }

    public void makeLogFile()
    {
        log = new File(date.format(new Date()) + ".log");

        try
        {
            logWriter = new BufferedWriter(new PrintWriter(log));
            // Make sure this closes properly or it wont print
        }
        catch(FileNotFoundException e)
        {

        }
    }

    public void writeLog(String message)
    {
        try
        {
            // Fix this formatting shit that is fucked up in the file
            logWriter.write(message);
            logWriter.flush();
        }
        catch(IOException e)
        {
        }
    }
}


