
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.net.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;


public class ChatServer implements Runnable
{  
	private ChatServerThread clients[] = new ChatServerThread[20];
	private ServerSocket server_socket = null;
	private Thread thread = null;
	private int clientCount = 0;
	private Utils utils = null;
	private Key privateKey;
	private Key publicKey;

	public ChatServer(int port)
    	{  
		try
      		{  
            		// Binds to port and starts server
			System.out.println("Binding to port " + port);
            		server_socket = new ServerSocket(port);  
            		System.out.println("Server started: " + server_socket);
				//Creates server keypair
				this.utils = new Utils();
				KeyPair kp = this.utils.kPGGen(1024);

				this.privateKey = kp.getPrivate();
				this.publicKey = kp.getPublic();

            		start();
        	}
      		catch(IOException ioexception)
      		{  
            		// Error binding to port
            		System.out.println("Binding error (port=" + port + "): " + ioexception.getMessage());
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
                		// Adds new thread for new client
                		System.out.println("Waiting for a client ..."); 
                		addThread(server_socket.accept()); 
            		}
            		catch(IOException ioexception)
            		{
                		System.out.println("Accept error: " + ioexception); stop();
            		} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					} catch (NoSuchPaddingException e) {
						e.printStackTrace();
					}
			}
    	}
    
   	public void start()
    	{  
        	if (thread == null)
        	{  
            		// Starts new thread for client
            		thread = new Thread(this); 
            		thread.start();
        	}
    	}
    
    	public void stop()
    	{  
        	if (thread != null)
        	{
            		// Stops running thread for client
            		thread.stop(); 
            		thread = null;
        	}
    	}
   
    	private int findClient(int ID)
    	{  
        	// Returns client from id
        	for (int i = 0; i < clientCount; i++)
            		if (clients[i].getID() == ID)
                		return i;
        	return -1;
    	}
    
    	public synchronized void handle(int ID, Message message) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

			if(message.getSharekey()==0){

			byte[] encryptedData = message.getEncryptedData();
			String input = this.utils.decryptMessage(encryptedData, message.getSymmetric());

			if (input.equals(".quit"))
            	{  
                	int leaving_id = findClient(ID);
                	// Client exits
                	clients[leaving_id].send(".quit");
                	// Notify remaing users
                	for (int i = 0; i < clientCount; i++)
                    		if (i!=leaving_id)
                        		clients[i].send("Client " +ID + " exits..");
                	remove(ID);
            	}
        	else
            		// Brodcast message for every other client online
            		for (int i = 0; i < clientCount; i++)
                		clients[i].send(ID + ": " + input);   
    	}
    	else
    		{
    			int id = findClient(ID);
    			clients[id].setClientPublicKey(message.getKeyToShare());
				System.out.println("client Pub key shared "+message.getKeyToShare().toString());
			}
	}


    	public synchronized void remove(int ID)
    	{  
        	int pos = findClient(ID);
      
       	 	if (pos >= 0)
        	{  
            		// Removes thread for exiting client
            		ChatServerThread toTerminate = clients[pos];
            		System.out.println("Removing client thread " + ID + " at " + pos);
            		if (pos < clientCount-1)
                		for (int i = pos+1; i < clientCount; i++)
                    			clients[i-1] = clients[i];
            		clientCount--;
         
            		try
            		{  
                		toTerminate.close(); 
            		}
         
            		catch(IOException ioe)
            		{  
                		System.out.println("Error closing thread: " + ioe); 
            		}
         
            		toTerminate.stop(); 
        	}
    	}
    
    	private void addThread(Socket socket) throws NoSuchAlgorithmException, NoSuchPaddingException {
    	    	if (clientCount < clients.length)
        	{  
            		// Adds thread for new accepted client
            		System.out.println("Client accepted: " + socket);
            		clients[clientCount] = new ChatServerThread(this, socket);


           		try
            		{  
                		clients[clientCount].open(); 
                		clients[clientCount].start();
						//share server public key
						Message sharKeyMessage = new Message(this.publicKey);
						clients[clientCount].shareKey(sharKeyMessage);
                		clientCount++; 
            		}
            		catch(IOException ioe)
            		{  
               			System.out.println("Error opening thread: " + ioe); 
            		}
       	 	}
        	else
            		System.out.println("Client refused: maximum " + clients.length + " reached.");
    	}
    
    
	public static void main(String args[])
   	{  
        	ChatServer server = null;
        
        	if (args.length != 1)
            		// Displays correct usage for server
            		System.out.println("Usage: java ChatServer port");
        	else
            		// Calls new server
            		server = new ChatServer(Integer.parseInt(args[0]));
    	}

}

class ChatServerThread extends Thread
{  
    private ChatServer       server    = null;
    private Socket           socket    = null;
    private int              ID        = -1;
    private ObjectInputStream  streamIn  =  null;
    private ObjectOutputStream streamOut = null;

	public Key getClientPublicKey() {
		return clientPublicKey;
	}

	public void setClientPublicKey(Key clientPublicKey) {
		this.clientPublicKey = clientPublicKey;
	}

	private Utils utils = null;
	private Key clientPublicKey;

   
    public ChatServerThread(ChatServer _server, Socket _socket) throws NoSuchPaddingException, NoSuchAlgorithmException {
        super();
        server = _server;
        socket = _socket;
        ID     = socket.getPort();
		utils = new Utils();

    }
    
    // Sends message to client
    public void send(String msg)
    {   
        try
        {
			Key symmetric  = utils.generateKey();
			byte[] encrypted = utils.encryptMessage(msg.getBytes(), symmetric);
			Message message = new Message(encrypted, symmetric);
			message.setEncryptedData(encrypted);
			streamOut.writeObject(message);
            streamOut.flush();
        }
       
        catch(IOException ioexception)
        {  
            System.out.println(ID + " ERROR sending message: " + ioexception.getMessage());
            server.remove(ID);
            stop();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	public void shareKey(Message msg)
	{
		try {
			streamOut.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			streamOut.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    // Gets id for client
    public int getID()
    {  
        return ID;
    }
   
    // Runs thread
    public void run()
    {  
        System.out.println("Server Thread " + ID + " running.");
      
        while (true)
        {  
            try
            {
				server.handle(ID,(Message) streamIn.readObject());
            }
         
            catch(IOException ioe)
            {  
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            } catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
    
    
    // Opens thread
    public void open() throws IOException
    {  
        streamIn = new ObjectInputStream(new
                        BufferedInputStream(socket.getInputStream()));
        streamOut = new ObjectOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
		streamOut.flush();
    }
    
    // Closes thread
    public void close() throws IOException
    {  
        if (socket != null)    socket.close();
        if (streamIn != null)  streamIn.close();
        if (streamOut != null) streamOut.close();
    }
    
}

