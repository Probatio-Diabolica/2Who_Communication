package com.Hrn.LemonT.server;

// !! I might do this server shenanigans in c++ for better performance

//this is supposed to launch the new instance of server and it is going to have the main methods
// this class is supposed to be running the server. It is the top layer of the server.


//note: I have the plans of accessing multiple server using this way, because Our server class is responsible for the instantation of the "Server".
////hence I can connoct to multi servers essentially more people.
//!! Here I made an entry point because I want or server to be a complete different entity  


public class ServerRunner {
    
    private int port;
    private Server server ;

    public ServerRunner(int port){
        this.port=port; 
        server = new Server(port);
    }
    public static void main(String[] args) {
        int port;
        if(args.length != 1)
        {
            System.out.println("You are missing a port number");
            return ;
        }
        port = Integer.parseInt(args[0]);
        new ServerRunner(port);
        System.out.println("your port is "+ port);
    }

}
