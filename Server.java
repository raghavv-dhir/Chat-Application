import java.awt.BorderLayout;
import java.awt.Font;
import java.io.*;
import java.net.*;
import javax.swing.*;

class Server extends JFrame {
    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    // GUI Components
    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageField = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    // Constructor for Server class
    public Server() {
        try {
            // Set up the server socket on port 7777
            server = new ServerSocket(7777);
            System.out.println("Server is ready to accept connections");
            System.out.println("Waiting...");

            // Accept incoming client connection
            socket = server.accept();
            System.out.println("Client connected.");

            // Set up input and output streams
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            // Initialize the GUI
            createGUI();
            handleEvents();

            // Start reading and writing threads
            startReading();
            startWriting();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to create the server GUI
    private void createGUI() {
        this.setTitle("Server Messenger[END]");
        this.setSize(600, 700);
        this.setLocationRelativeTo(null);  // Center the window on the screen
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set fonts for the components
        heading.setFont(font);
        messageArea.setFont(font);
        messageField.setFont(font);

        // Add an icon to the heading (logo.png) and set its alignment
        heading.setIcon(new ImageIcon("logo.png"));  // Add the icon for the server side
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageField.setHorizontalAlignment(SwingConstants.CENTER);

        // Layout for the frame
        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);  // Add heading to the top
        JScrollPane jScrollPane = new JScrollPane(messageArea);  // Make the message area scrollable
        this.add(jScrollPane, BorderLayout.CENTER);  // Add message area to the center
        this.add(messageField, BorderLayout.SOUTH);  // Add text field to the bottom

        this.setVisible(true);  // Show the window
    }

    // Method to handle user input events
    private void handleEvents() {
        messageField.addActionListener((e) -> {
            String contentToSend = messageField.getText();
            messageArea.append("Me: " + contentToSend + "\n");
            out.println(contentToSend);
            out.flush();
            messageField.setText("");  // Clear the input field
            messageField.requestFocus();
        });
    }

    // Reading method (runs in a separate thread)
    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started...");
            try {
                while (true) {
                    // Read messages from the client
                    String msg = br.readLine();
                    if (msg.equals("Exit")) {
                        System.out.println("Client has terminated the chat.");
                        JOptionPane.showMessageDialog(this, "Client has terminated the chat");
                        messageField.setEnabled(false);  // Disable input when chat ends
                        socket.close();
                        break;
                    }
                    messageArea.append("Client: " + msg + "\n");  // Show the client's message in the text area
                }
            } catch (Exception e) {
                System.out.println("Connection closed!");
            }
        };
        new Thread(r1).start();  // Start the reading thread
    }

    // Writing method (runs in a separate thread)
    public void startWriting() {
        Runnable r2 = () -> {
            System.out.println("Writer started...");
            try {
                while (!socket.isClosed()) {
                    // Do nothing, the message will be sent using handleEvents
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Thread(r2).start();  // Start the writing thread
    }

    public static void main(String[] args) {
        System.out.println("This is server...");
        new Server();  // Start the server
    }
}
