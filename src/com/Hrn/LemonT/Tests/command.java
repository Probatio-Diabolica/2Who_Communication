package com.Hrn.LemonT.Tests;
import java.util.Scanner;

public class command {
    private static String commandFilter(String command){
        for(int i =0 ;i<command.length();i++)
        {
            if(command.charAt(i)==' ')
            {
                // here we will remove the space
                command=command.substring(0, i) + command.substring(i+1, command.length());
                --i;
            }
        }
        return command.toLowerCase();
    }
    public static String commandSplitter(String Command)
    {
        if(Command.startsWith("sudo--get")) 
        {
            try
            {
                Command=Command.split("sudo--get")[1];
            }
            catch(ArrayIndexOutOfBoundsException e)
            {
                Command=" ";
                System.out.println("[SERVER WARNING] Incomplete command !!");
            }
        }
        return Command;
    }
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while(true)
        {
            String commaString= scan.nextLine();
            System.out.println("Before Parsing :: "+commaString);
            commaString =commandFilter(commaString);
            System.out.println("After Parsing :: "+commaString);
            commaString=commandSplitter(commaString);
            System.out.println(commaString);
        }
    }
}
