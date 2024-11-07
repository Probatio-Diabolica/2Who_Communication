package com.Hrn.LemonT.server;

import java.net.InetAddress;

public class UserClient {
    public String name;
    public InetAddress address;
    public int port;
    // unique id for clients
    public final int ID;
    public int attempt=0;

    public UserClient(String name, InetAddress address,final int ID,int port){
        this.ID=ID;
        this.address=address;
        this.port=port;
        this.name=name;
    }

    public String getName(){
        return this.name;
    }
    public int getID()
    {
        return ID;
    }
}
