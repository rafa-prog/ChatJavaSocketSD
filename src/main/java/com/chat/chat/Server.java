package com.chat.chat;

import java.io.*;
import java.util.*;
import java.net.*;
import static java.lang.System.out;

public class  Server {
  Vector<String> users = new Vector<String>();
  Vector<HandleClient> clients = new Vector<HandleClient>();
  public void process() throws Exception  {
      ServerSocket server = new ServerSocket(3000,10); // Cria um servidor na porta 3000 com um backlog de 10
      out.println("Server Started...");
      while(true) { // Servidor executa até matar no terminal ou disparar erro
 		 Socket client = server.accept(); 
 		 HandleClient c = new HandleClient(client); // Toda vez q receber um client salva nas duas listas e usa como thread
  		 clients.add(c);
         System.out.println(clients);
         System.out.println(users);
     }  // end of while
  }
  public static void main(String ... args) throws Exception {
      new Server().process();
  } // end of main

  public void broadcastLogin(String user)  {
	// envia mensagem para todos os users que alguém conectou-se
	for ( HandleClient c : clients )
		  c.sendUserLogin(user);
	}

	public void broadcastLogout(String user)  {
		// envia mensagem para todos os users que alguém desconectou-se
		for ( HandleClient c : clients )
			  c.sendUserLogout(user);
	}

  public void broadcastMessage(String user, String message)  {
	    // envia mensagem para todos os users
	    for ( HandleClient c : clients )
	          c.sendMessage(user,message);
  }

  class  HandleClient extends Thread {
    String name = "";
	BufferedReader input;
	PrintWriter output;

	public HandleClient(Socket  client) throws Exception {
         // input and output streams
		input = new BufferedReader( new InputStreamReader( client.getInputStream())) ;
		output = new PrintWriter ( client.getOutputStream(),true);
		// pega o nome
		name  = input.readLine();
		users.add(name);
		broadcastLogin(name);
		start();
    }

	public void sendUserLogin(String uname)  {
	    	output.println( uname + " entrou no chat.");
	}

	public void sendUserLogout(String uname)  {
		output.println( uname + " saiu no chat.");
	}

    public void sendMessage(String uname,String  msg)  {
	    output.println( uname + ":" + msg);
	}
		
        public String getUserName() {  
            return name; 
        }
        public void run()  {
    	     String line;
	     try    {
                while(true)   {
		 line = input.readLine();
		 if ( line.equals("end") ) {
		   clients.remove(this);
		   users.remove(name);
		   broadcastLogout(name);
		   break;
                 }
		 broadcastMessage(name,line);
	       } // end of while
	     } // try
	     catch(Exception ex) {
	       System.out.println(ex.getMessage());
	     }
        } // end of run()
   } // end of inner class
} // end of Server