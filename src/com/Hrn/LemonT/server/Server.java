package com.Hrn.LemonT.server;

import java.io.CharConversionException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/* 
!! List of CommandLine errors
$    [Err:#1] :: if command is incorrect 
!! List of CommandLine Exceptions
$    [Warn:#1] :: Incomplete commmand 
$    [Warn#2]  :: Not a command
*/

// this class will be responsible to send and recieve the data
public class Server implements Runnable{
    public String serialVersion="1.0.0";
    private List<UserClient> clients= new ArrayList<UserClient>();
    private List<Integer> responseList=new ArrayList<Integer>();
    private DatagramSocket socket;
    private int port;
    private boolean running=false;
    private Thread run,manage,send,recieve;
    
    ////Commands///////////////////////////////////////////////////////////////////////
    private final String COMMAND_ARGS   =   "sudo";
    private final String ARGS_FETCHER   =   "get";
    private final String PUSH           =   "push";
    private final String HELP           =   "help";
    private final String ACTIVE_CLIENTS =   "clients";
    private final String RAW_DATA       =   "raw";
    private final String REMOVE         =   "rm";
    
    /////Flags//////////////////////////////////////////////////////////////////////// 
    private final String fUSER           =   "-usr";

    // private String 

    private boolean raw=false;
    //////////////////////////////////////////////////////////////////////////////////

    private final int MAX_ATTEMPTS=5;
    
    public Server(int port)
    {
        this.port= port;
        try {
            socket=new DatagramSocket(port); 
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        run=new Thread(this,"m__Server");
        run.start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //!! this will parse our command
    private String commandParser(String command){
        for(int i =0 ;i<command.length();i++)
        {
            if(command.charAt(i)==' ')
            {
                // here we will remove the space
                command=command.substring(0, i) + command.substring(i+1, command.length());
                --i;
            }
        }
        return commandSplitter(command.toLowerCase());
    }

    private String commandSplitter(String Command)
    {
        if(Command.startsWith(COMMAND_ARGS+ARGS_FETCHER)) 
        {
            try
            {
                Command=Command.split(COMMAND_ARGS+ARGS_FETCHER)[1];
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                Command="";
                System.out.println("[Warn:#1] Incomplete command !!");
            }
        }
        // else if(Command.startsWith(REMOVE+fUSER))
        // {
        //     try
        //     {
        //         Command=Command.split(REMOVE+fUSER)[1];
        //     }
        //     catch(ArrayIndexOutOfBoundsException e)
        //     {
        //         Command="";
        //         System.out.println("[Warn:#1] Incomplete command !!");
        //     }
        // }
        return Command;
    }

    private void Actions(String Command)
    {
        if(Command.equals(ACTIVE_CLIENTS))
        {
            if(clients.size()==0) System.out.println("No active users");
            else{
                for(int i=0;i<clients.size();i++) System.out.println("Client ID : "+clients.get(i).getID() + "Client name : "+clients.get(i).getName());
            }

        }else if(Command.equals(HELP)) {
            System.out.println("Here's the list of commands\n\n");
            System.out.println("* 'help' : returns all the commands");
            System.out.println("* 'sudo get clients' : returns the active clients");
            System.out.println("");
            System.out.println();
        }
        else if(Command.equals(RAW_DATA)){
            raw=!raw;
        }
        else if(Command.startsWith(REMOVE+fUSER))
        {
            boolean removed=false;
            try{
                int id=Integer.parseInt(Command.split(REMOVE+fUSER)[1]);
                System.out.println(id);
                removed=disconnect(id, true);
            }catch(NumberFormatException e){
                System.out.println("Id is not a valid number type");
                return;
            }
            catch(ArrayIndexOutOfBoundsException e){
                System.out.println("Missing Id");
            }
            if(removed==false)
            {
                System.out.println("Not correct ID");
            }
        }
        else{
            System.out.println(Command+"<-[Err:#1] Invalid Command");
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void run()
    {
        running = true;
        System.out.println("Server started on port : "+port);
        manageClients();
        receive();
        
        //commands
        Scanner scanner = new Scanner(System.in);
        while(running){
            String command= scanner.nextLine() ;
            if(command.equals("STOP")) running=false;
            command = commandParser(command);
            Actions(command);
        }
    }

    private void receive() {
        //note: it receives the anytype of data it gets.
        //it gets complicated because we will be getting data from multiple users. 
        recieve = new Thread("m__Receiver"){
            public void run(){
                while(running){
                    byte[] data= new byte[1024];
                    DatagramPacket packet= new DatagramPacket(data,data.length);
                    try {

                        socket.receive(packet);
                        // packet.getAddress();
                        // packet.getPort();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    process(packet);
                    // System.out.println(clients.get(0).getName()+ " :: "+clients.get(0).address.toString()+" : "+clients.get(0).port);
                    
                }
            }
        };
        recieve.start();
    }

    private void sendToAll(String message)
    {
        if(message.startsWith("/m/"))
        {
            String msg = message.substring(3);
            msg=msg.split("/e/")[0];
            // if(raw) System.out.println(msg);
        }
        // if(raw){

        //     // System.out.println(message);
        
        // }    
        for (int i=0;i<clients.size();i++){
            UserClient client=clients.get(i);
            send(message.getBytes(),client.address,client.port);
        }
    }

    private void send(final byte[] data, final InetAddress address, final int port){
        send=new Thread("m__Send"){
            public void run(){
                DatagramPacket packet= new DatagramPacket(data,data.length,address,port);
                try{
                    socket.send(packet);
                }catch(IOException e){
                    e.printStackTrace();
                } 
            }
        };
        send.start();
    }

    private void send(String message,final InetAddress address,int port){
        message+="/e/";
        send(message.getBytes(),address,port);
    }

    private void process(DatagramPacket packet){
        String  string= new String(packet.getData());
        
        //task: Flush whatever is happening in the server
        if(raw) System.out.println(string);

        //task: Connection packet
        if(string.startsWith("/c/")){
            int id=UniqueID.getIdentifier();
            String name=string.split("/c/|/e/")[1];
            System.out.println("ID:: "+" ("+id+") connected!");
            clients.add(new UserClient(name, packet.getAddress(), id, packet.getPort()));
            // System.out.println("Something :: " +string.substring(3,string.length()));
            
            String ID="/c/"+id;

            send(ID,packet.getAddress(),packet.getPort());    
        }//task :: message packet
        else if(string.startsWith("/m/")){
            sendToAll(string);

        //task: disconnected packet
        }else if(string.startsWith("/d/")){
            System.out.print(string);
            String id= string.split("/d/|/e/")[1];
            disconnect(Integer.parseInt(id), true);
        }
        else if(string.startsWith("/i/")){
            // System.out.println(string);
            responseList.add(Integer.parseInt(string.split("/i/|/e/")[1]));

        }
        else{
            System.out.println(string);
        }
    }

    private boolean disconnect(int id,boolean isExitClean){
        UserClient tempClient= null;
        String message="";
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getID()==id){
                tempClient=clients.get(i);
                clients.remove(i);
                if(isExitClean){
                    message="User : "+tempClient.getName()+" ( "+tempClient.getID()+" ) @ " + tempClient.address.toString() + " : " + tempClient.port + " has disconnected.";
                }else{
                    message="User "+ tempClient.getName() +" id:: "+ tempClient.getID() + " Timed out";
                }
                System.out.println(message);
                return true;
            }
        }
        return false;
    }

    private void manageClients() {
        ////manages the clients
        ////if a client doesn't responds to the packets we send,we kick em bc that just means they are unavailable.
        manage=new Thread("m__Manage"){
            public void run(){
                while(running){
                    sendToAll("/i/Ping");

                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace(); 
                    }
                    for(int i=0;i<clients.size();i++){

                        UserClient uc = clients.get(i);
                        if(!responseList.contains(clients.get(i).getID())){
                            if(uc.attempt>MAX_ATTEMPTS) disconnect(uc.getID(), false);
                            else ++uc.attempt;
                        }
                        else{
                            responseList.remove(Integer.valueOf(uc.getID()));
                            uc.attempt=0;
                            
                        }
                    }
                }
            }
        };
        manage.start();
    }
}

