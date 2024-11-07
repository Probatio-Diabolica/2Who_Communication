package com.Hrn.LemonT.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


// this class will be responsible to send and recieve the data
public class Server implements Runnable{
    
    private List<UserClient> clients= new ArrayList<UserClient>();
    private List<Integer> responseList=new ArrayList<Integer>();
    private DatagramSocket socket;
    private int port;
    private boolean running=false;
    private Thread run,manage,send,recieve;
    
    private String ARGS="//c//";
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

    @Override
    public void run()
    {
        running = true;
        System.out.println("Server started on port : "+port);
        manageClients();
        receive();
        Scanner scanner = new Scanner(System.in);
        while(running){
            String command= scanner.nextLine();
            if(!command.startsWith(ARGS)) {
                sendToAll("/m/Server: "+command+"/e/");
                continue;
            }
            command= command.substring(ARGS.length());
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
                        packet.getAddress();
                        packet.getPort();
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
            System.out.println(msg);
        }
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
        
        //task: Connection packet

        if(string.startsWith("/c/")){
            int id=UniqueID.getIdentifier();
            System.out.println("ID:: "+id);
            clients.add(new UserClient(string.substring(3,string.length()), packet.getAddress(), id, packet.getPort()));
            System.out.println("Something :: " +string.substring(3,string.length()));
            
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
            System.out.println(string);
            responseList.add(Integer.parseInt(string.split("/i/|/e/")[1]));

        }
        else{
            System.out.println(string);
        }
    }

    private void disconnect(int id,boolean status){
        System.out.println("entered");
        UserClient tempClient= null;;
        for(int i=0;i<clients.size();i++){
            if(clients.get(i).getID()==id){
                tempClient=clients.get(i);
                clients.remove(i);
                break;
            }
        }
        String message="";
        if(status){
            message="User : "+tempClient.getName()+" ( "+tempClient.getID()+" ) @ " + tempClient.address.toString() + " : " + tempClient.port + " has disconnected.";
        }else{
            message="User "+ tempClient.getName() +" id:: "+ tempClient.getID() + " Timed out";
        }
        System.out.println(message);
    }

    private void manageClients() {
        ////manages the clients
        ////if a client doesn't responds to the packets we send,we kick em bc that just means they are unavailable.
        manage=new Thread("m__Manage"){
            public void run(){
                while(running){
                    sendToAll("/i/Ping");
                    // System.out.println(clients.size());
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        e.printStackTrace(); 
                    }
                    for(int i=0;i<clients.size();i++)
                    {
                        UserClient uc = clients.get(i);
                        if(responseList.contains(clients.get(i).getID())){
                            if(uc.attempt>=MAX_ATTEMPTS) disconnect(uc.getID(), false);
                            else ++uc.attempt;
                        }
                        else{
                            int a=new Integer(uc.getID());
                            responseList.remove(new Integer(uc.getID()));
                            uc.attempt=0;
                            System.out.println(a+"OK");
                        }
                    }
                }
            }
        };
        manage.start();
    }
}
