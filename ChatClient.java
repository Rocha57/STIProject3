
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.net.*;
import java.io.*;
import java.security.*;

import javax.crypto.*;


public class ChatClient implements Runnable
{
    private Socket socket              = null;
    private Thread thread              = null;
    private DataInputStream  console   = null;
    private ObjectOutputStream streamOut = null;
    private ChatClientThread client    = null;
    private Utils utils = null;
    private PublicKey serverPublicKey = null;
    private PublicKey clientPublicKey = null;
    private PrivateKey clientPrivateKey = null;

    public ChatClient(String serverName, int serverPort)
    {
        System.out.println("Establishing connection to server...");

        try
        {
            // Establishes connection with server (name and port)
            socket = new Socket(serverName, serverPort);
            utils = new Utils();
            System.out.println("Connected to server: " + socket);

            this.utils = new Utils();
            KeyPair kp = this.utils.kPGGen(1024);

            this.clientPrivateKey = kp.getPrivate();
            this.clientPublicKey = kp.getPublic();

            start();
        }

        catch(UnknownHostException uhe)
        {
            // Host unkwnown
            System.out.println("Error establishing connection - host unknown: " + uhe.getMessage());
        }

        catch(IOException ioexception)
        {
            // Other error establishing connection
            System.out.println("Error establishing connection - unexpected exception: " + ioexception.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   public void run()
   {
       while (thread != null)
       {
           try
           {
               String data = (String) console.readLine();
               SecretKey symmetric  = utils.generateKey();
               byte[] encrypted = utils.encryptMessage(data.getBytes(), symmetric);
               byte[] encryptedSymmetric = utils.wrapKey(symmetric,serverPublicKey);
               Message message = new Message(encrypted, encryptedSymmetric);
               /*System.out.println(message.getData());
               System.out.println(new String(message.getEncryptedData()));
               System.out.println(utils.decryptMessage(encrypted, symmetric));*/
               // Sends message from console to server
               //streamOut.writeUTF(simmetricEncryption(console.readLine()));
               streamOut.writeObject(message);
               streamOut.flush();
           }

           catch(Exception ioexception)
           {
               System.out.println("Error sending string to server: " + ioexception.getMessage());
               stop();
           }
       }
    }


    public void handle(Message message) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
        if(message.getSharekey()==0){
            byte[] encryptedData = message.getEncryptedData();
            SecretKey key = utils.unwrapKey(message.getSymmetric(), this.clientPrivateKey);
            String msg = this.utils.decryptMessage(encryptedData, key);

            // Receives message from server
            if (msg.equals(".quit"))
            {
                // Leaving, quit command
                System.out.println("Exiting...Please press RETURN to exit ...");
                stop();
            }
            else
                // else, writes message received from server to console
                System.out.println(msg);
            }
        else if(message.getSharekey()==1)
        {
            //System.out.println("Server key shared!");
            this.serverPublicKey = message.getKeyToShare();

        }
    }

    // Inits new client thread
    public void start() throws IOException
    {
        console   = new DataInputStream(System.in);
        streamOut = new ObjectOutputStream(socket.getOutputStream());

        Message keyShareMessage = new Message(this.clientPublicKey);
        streamOut.writeObject(keyShareMessage);
        //System.out.println("KEY: "+this.clientPublicKey.toString());
        if (thread == null)
        {
            client = new ChatClientThread(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    // Stops client thread
    public void stop()
    {
        if (thread != null)
        {
            thread.stop();
            thread = null;
        }
        try
        {
            if (console   != null)  console.close();
            if (streamOut != null)  streamOut.close();
            if (socket    != null)  socket.close();
        }

        catch(IOException ioe)
        {
            System.out.println("Error closing thread..."); }
            client.close();
            client.stop();
        }


    public static void main(String args[])
    {
        ChatClient client = null;
        if (args.length != 2)
            // Displays correct usage syntax on stdout
            System.out.println("Usage: java ChatClient host port");
        else
            // Calls new client
            client = new ChatClient(args[0], Integer.parseInt(args[1]));
    }

}

class ChatClientThread extends Thread
{
    private Socket           socket   = null;
    private ChatClient       client   = null;
    private ObjectInputStream  streamIn = null;

    public ChatClientThread(ChatClient _client, Socket _socket)
    {
        client   = _client;
        socket   = _socket;
        open();
        start();
    }

    public void open()
    {
        try
        {
            streamIn  = new ObjectInputStream(socket.getInputStream());
        }
        catch(IOException ioe)
        {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    public void close()
    {
        try
        {
            if (streamIn != null) streamIn.close();
        }

        catch(IOException ioe)
        {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void run()
    {
        while (true)
        {   try
            {
                client.handle((Message)streamIn.readObject());
            }
            catch(IOException ioe)
            {
                System.out.println("Listening error: " + ioe.getMessage());
                client.stop();
            } catch (Exception e) {
            e.printStackTrace();
        }
        }
    }


}
