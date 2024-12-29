package com.Hrn.LemonT.server;

public class Parser {
    ////Commands///////////////////////////////////////////////////////////////////////
    private final String COMMAND_ARGS   =   "sudo";
    private final String ARGS_FETCHER   =   "get";
    private final String PUSH           =   "push";
    private final String HELP           =   "help";
    private final String ACTIVE_CLIENTS =   "clients";
    private final String RAW_DATA       =   "raw";
    // private String 

    private boolean raw=false;
    //////////////////////////////////////////////////////////////////////////////////
    Parser()
    {

    }
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
        }else if(Command.equals(HELP)) {
            System.out.println("Here's the list of commands\n\n");
            System.out.println("* 'help' : returns all the commands");
            System.out.println("* 'sudo get clients' : returns the active clients");
            System.out.println("");
            System.out.println();
        }else{
            System.out.println(Command+"<-[Err:#1] Invalid Command");
        }
        return Command;
    }

}
