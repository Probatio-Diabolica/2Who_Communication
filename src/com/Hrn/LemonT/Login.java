package com.Hrn.LemonT;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;

import javax.swing.JButton;


public class Login extends JFrame{
    private JPanel contentPane;
    
    private int tfHgt =30;
    private int tfWdt =200;

    private int lHgt=25;
    private int lWdt=100;
    //!! Labels
    private JLabel nameL;
    private JLabel IpL;
    private JLabel IpExL;
    private JLabel portL;
    private JLabel portEx;
    
    //!! textfields
    private JTextField nameTF;
    private JTextField  ipTF;
    private JTextField  portTF;

    //!! button
    private JButton submitBtn;

    //!! utils 
    private CLientGui user;


    public Login() throws IOException
    {
    
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }
        int height=34;

        File path = new File("Assets/icon.png");
        BufferedImage icon = ImageIO.read(path);
        setIconImage(icon);
        
        // ImageIcon icon = new ImageIcon("Assets/icon");
        // setIconImage(icon.getImage());


        setResizable(false);
        setTitle("Welcome to Lemon T");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,400);
        setLocationRelativeTo(null);

        //$ panel/window
        contentPane= new JPanel();
        // contentPane.setBackground(new Color(31,31,31));
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout(0,0));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        //$ NAME LABEL
        nameL=new JLabel("Name: ");
        nameL.setBounds(127,height,lWdt,lHgt);
        contentPane.add(nameL);
        height+= lHgt;

            //* Name TextField
            nameTF=new JTextField();
            nameTF.setBounds(67,height,tfWdt,tfHgt);
            nameTF.setColumns(20);
            contentPane.add(nameTF);
            height+=tfHgt;

        //$ IP Label
        IpL = new JLabel("IP: ");
        IpL.setBounds(127,height,lWdt,lHgt);
        contentPane.add(IpL);
        height+=lHgt;

            //* IP textField
            ipTF=new JTextField();
            ipTF.setBounds(67,height,tfWdt,tfHgt);
            ipTF.setColumns(20);
            contentPane.add(ipTF);
            height+=tfHgt;
            
            //* IP example Label
            IpExL = new JLabel("(eg: 127.0.0.1)");
            IpExL.setBounds(127,height,lWdt,lHgt);
            contentPane.add(IpExL);
            height+=lHgt;

        //$ IP Label
        portL = new JLabel("Port: ");
        portL.setBounds(127,height,lWdt,lHgt);
        contentPane.add(portL);
        height+=lHgt;

            //* IP textField
            portTF=new JTextField();
            portTF.setBounds(67,height,tfWdt,tfHgt);
            portTF.setColumns(20);
            contentPane.add(portTF);
            height+=tfHgt;

            //* Port label example
            portEx=new JLabel("( Eg: 2005 )") ;
            portEx.setBounds(122,height,lWdt,lHgt);
            contentPane.add(portEx);
            height+=lHgt;

        //$ subimt Button
        submitBtn = new JButton("Login");
        //$ anonymous class to save code
        submitBtn.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent event){
                String Name=nameTF.getText(),Address=ipTF.getText();
                int port=Integer.parseInt(portTF.getText());
                logSubmit(Name,Address,port);
            }

        });
        submitBtn.setBounds(167,height,75,tfHgt);
        contentPane.add(submitBtn);


    }

    //// Page login
    private void logSubmit(String name, String address,int port) {
        System.out.println("your name : " + name + " your Ip :: " + address + " your port : " + port);
        dispose();
        user= new CLientGui(name,address,port);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run(){
                try{
                    Login frame = new Login();
                    frame.setVisible(true);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    

}
