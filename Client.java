import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame{
    Socket socket;
    BufferedReader br;
    PrintWriter out;

    //Declaring Components
    private JLabel heading = new JLabel("Client Area");
    private JTextArea messagArea = new JTextArea();
    private JTextField messageField = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);
    //Constructor
    public Client(){
        try {
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection established.");
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            CreateGUI();
            handleEvents();
            startReading();
            // startWriting();
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                
            }

            @Override
            public void keyPressed(KeyEvent e) {
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // System.out.println("Key released "+ e.getKeyCode());
                if(e.getKeyCode()==10){
                    // System.out.println("You have pressed ENTER button");
                    String contentToSend = messageField.getText();
                    messagArea.append("Me: "+contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageField.setText("");
                    messageField.requestFocus();
                }
            }
            
        });
    }

    private void CreateGUI() {
        this.setTitle("Client Messager[END]");
        this.setSize(600,700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        //component coding
        heading.setFont(font);
        messagArea.setFont(font);
        messageField.setFont(font);
    
        // Load and resize the icon
        ImageIcon originalIcon = new ImageIcon("logo4.png");  // Load the image
        Image img = originalIcon.getImage();  // Get the Image object
        Image resizedImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);  // Resize to 50x50
        ImageIcon resizedIcon = new ImageIcon(resizedImg);  // Create a new ImageIcon
    
        // Set the resized icon to the heading
        heading.setIcon(resizedIcon);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messagArea.setEditable(false);
        messageField.setHorizontalAlignment(SwingConstants.CENTER);
    
        //layout for frame
        this.setLayout(new BorderLayout());
        //Adding components to frame
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messagArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageField, BorderLayout.SOUTH);
    
        this.setVisible(true);
    }
    

    //Reading method
    public void startReading() {
        Runnable r1 = ()->{
            System.out.println("Reader started...");
        try{
            while (true) {
                String msg = br.readLine();
                if (msg.equals("Exit")) {
                    System.out.println("Server has terminated the chat.");
                    JOptionPane.showMessageDialog(this, "Server has terminated the chat");
                    messageField.setEnabled(false);
                    socket.close();
                    break;
                }
                messagArea.append("Server: "+msg+"\n");
            }
        } catch (Exception e){
                System.out.println("Connection Closed!");
            }
        };
        new Thread(r1).start();
    }

    //Writing method
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
        try{
            while (!socket.isClosed()) {
                    BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                    String content = br1.readLine();
                    out.println(content);
                    out.flush();
                    if (content.equals("Exit")) {
                        socket.close();
                        break;
                    }
                }
                System.out.println("Connection Closed!");
            }catch (Exception e) {
                    e.printStackTrace();
                }
        };
        new Thread(r2).start();
    }
    public static void main(String[] args) {
        System.out.println("This is client..");
        new Client();
    }
}
