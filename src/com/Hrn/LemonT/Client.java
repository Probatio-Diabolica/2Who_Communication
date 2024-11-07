package com.Hrn.LemonT;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;



/*
!! Two choices
-> I have two choices of using networking protocols: TCP or UDP 

$ TCP makes sures that our packets get sent and recieved by others. And other thing it gaurentees that or packet get sent in a sequence {Can't say the same for delivery} 
$ while UDP {User datagram protocol} doesn't.

note: TCP requires to have a well established connection before it sends the data {3 way handshake}. UDP does not, It will just throw the data and will hope that it gets recieved by the other party as if its not its job. {Reminds me of a certain someone}.

!! I can choose TCP it looks good, I hope setting it up won't be that complex.

note: If this were to be a game I would've chosen UDP, because I don't want a multiplayer game to exit just because it temorarily lost its connection from the server

!! resolved: I'm dumb I only have one choice that it TCP Why did I wasted my time implementing UDP. I should've made a detailed plan T-T. But anyways
*/


public class Client {
////--------------Concurrency-------------------------------
    private Thread send;
////--------------------------------------------------------

////--------------User data essentials ---------------------
    private String name, address;
    private int port;
    private int ID=-1;
////--------------------------------------------------------

////--------------Connection--------------------------------
    private DatagramSocket socket;
    private InetAddress IP;
////--------------------------------------------------------

    Client(String name, String address, int port)
    {
        this.name=name;
        this.address=address;
        this.port=port;
    }

    public boolean openConnection(String Address)
    {
        try{
            //!! we can't allocate two sockets in a single port but we can send multiple packets ina a single socket
            socket= new DatagramSocket();
            IP=InetAddress.getByName(Address);
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
            return false;
        }
        catch(SocketException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void send(final byte[] data)
    {
        send= new Thread("m__Send"){
            public void run(){
                DatagramPacket pack = new DatagramPacket(data,data.length,IP,port);
                try {
                    socket.send(pack);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        send.start();
    }

    public void close(){
        new Thread(){
            public void run(){
                synchronized (socket){
                    socket.close();
                }
            }
            
        }.start();

    }

    public String recieve() {
        byte[] data = new byte[1024];
        DatagramPacket packet = new DatagramPacket(data,data.length);
        
        try{
            socket.receive(packet);
        }catch(IOException e){
            e.printStackTrace();
        }

        String message=new String(packet.getData());
        return message;
    }

    public final String getName(){
        return this.name;
    }
    public final String getAddress(){
        return this.address;
    }
    public final int getPort(){
        return this.port;
    }
    public final int getID(){
        return this.ID;
    }

    public void setID(int newID) {
        this.ID=newID;
    }

}
