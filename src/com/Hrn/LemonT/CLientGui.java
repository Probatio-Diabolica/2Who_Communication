package com.Hrn.LemonT;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class CLientGui extends JFrame implements Runnable{

    private JPanel contentPane;
    private JTextArea txtChat;
    private JTextField txtMsg;
    private JButton btnSend;
    private DefaultCaret caret;

    private Client client; 

    private Thread dataFetcher,run;

    private boolean running = false;

    public CLientGui(String name, String address, int port){
        setTitle("2who_" + name);
        ImageIcon icon = new ImageIcon("Assets/icon.png");
        setIconImage(icon.getImage());
        client=new Client(name,address,port);
        boolean connect=client.openConnection(address);
        if(!connect){
            System.err.println("connection err");
            console("Connection error");
        }

        setUpClient();

        if(connect){
            console(name+" Made the connection");
            
        }else{
            console("Cannot make a connection");
        }
        String Connection = "/c/"+name+"/e/" ; 
        client.send(Connection.getBytes());
        this.running=true;
        run = new Thread(this,"m__Running"); 
        run.start();
        
    }


    @Override
    public void run(){
        fetchPackets();
    }

    public void fetchPackets(){
        dataFetcher=new Thread("m__DataFetcher"){
            @Override
            public void run(){
                while(running){
                    String message = client.recieve();
                    // console(message);
                    
                    //$ Do fetch the client'd ID if it's just a connection packet string
                    //$ Else, it's just a message, give it to the user 
                    
                    if(message.startsWith("/c/")){
                        client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
                        console(client.getName()+" successfully connected to server with ID "+client.getID());
                    }else if(message.startsWith("/m/")){
                        String msg = message.substring(3);
                        msg=msg.split("/e/")[0]; // it is zero because string might be empty
                        console(msg);
                    }
                    else if(message.startsWith("/i/")){
                        String ping = "/i/"+client.getID()+"/e/";
                        send(ping,false);
                    }
                }
                    
            }     
        };
        dataFetcher.start();
    }

    public void send(String Message,boolean toClient){
        if(Message.equals("")) return;
        
        if(toClient) {
            Message = "[ "+client.getName()+" ]"+" :: "+Message ;
            Message="/m/"+Message+"/e/";
            txtMsg.setText("");
        }
        client.send(Message.getBytes());
    }
    private void setUpClient()
    {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e){
            e.printStackTrace();
        }


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024,920);
        setMinimumSize(new Dimension(1024, 920));
        setLocationRelativeTo(null);
        contentPane=new JPanel();
        
        
        //* experimental
        // contentPane.setBackground(new Color(31,31,31));
        
        contentPane.setBorder(new EmptyBorder(5,5,5,5));
        contentPane.setLayout(new BorderLayout(0,0));
        setContentPane(contentPane);
        

        //* grid Bag Layout for dynamic sizing
        GridBagLayout gb_ContentPane=new GridBagLayout();
        gb_ContentPane.columnWidths=new int[]{50,920,30,24}; //!! sum must be equal to width
        gb_ContentPane.columnWeights=new double[]{1.0,Double.MIN_VALUE};
        gb_ContentPane.rowHeights=new int[]{60,800,60}; //!! sum must be equal to height
        gb_ContentPane.rowWeights=new double[]{1.0,Double.MIN_VALUE};
        contentPane.setLayout(gb_ContentPane);

        //* Chat Area
        txtChat = new JTextArea();
        txtChat.setEditable(false);
        ////experimental
        // txtChat.setBackground(new Color(33,33,33));
        // txtChat.setForeground(new Color(255,255,255));
        ///////////////////////////////////////////
        caret=(DefaultCaret)txtChat.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane Chat= new JScrollPane(txtChat);
        GridBagConstraints gdc_chat=new GridBagConstraints();
        gdc_chat.insets=new Insets(0,20,0,5);
        gdc_chat.fill=GridBagConstraints.BOTH;
        gdc_chat.gridx=0;
        gdc_chat.gridy=0;
        gdc_chat.gridwidth=3;
        gdc_chat.gridheight=2;
        contentPane.add(Chat,gdc_chat);
        // !! will come back to it. I wanna add a friend panel too
        // gdc_chat.gridx=1; //* column index
        // gdc_chat.gridy=1; //* row index
        // gdc_chat.gridwidth=2;
        

        //* Message area
        txtMsg=new JTextField();
        txtMsg.addKeyListener(new KeyAdapter(){
            @Override
            public void keyPressed(KeyEvent key){
                if(key.getKeyCode()==KeyEvent.VK_ENTER){
                        send(txtMsg.getText(),true);
                }
            }
        });
        GridBagConstraints gdc_txtMsg = new GridBagConstraints();
        gdc_txtMsg.insets = new Insets(0,20,0,5);
        gdc_txtMsg.fill = GridBagConstraints.HORIZONTAL;
        gdc_txtMsg.gridx = 1;
        gdc_txtMsg.gridy = 2;
        contentPane.add(txtMsg,gdc_txtMsg);
        txtMsg.setColumns(10);

        //* Send button
        btnSend= new JButton("Send");
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                send(txtMsg.getText(),true);
            }
        });
        GridBagConstraints gdc_btnSend = new GridBagConstraints();
        gdc_btnSend.insets=new Insets(0, 0, 0, 5);
        gdc_btnSend.gridx=2;
        gdc_btnSend.gridy=2;
        contentPane.add(btnSend, gdc_btnSend);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e){
                String disconnect="/d/"+client.getID()+"/e/";
                System.out.println(disconnect);
                send(disconnect,false);
                client.close();
                running=false;
            }
        });

        setVisible(true);
        txtMsg.requestFocusInWindow();
    }
    public void console(String Message)
    {
        txtChat.append(Message+"\n");
    }

    

}
