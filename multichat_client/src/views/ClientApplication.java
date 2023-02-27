package views;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;

import dto.request.RequestDto;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientApplication extends JFrame {

	private static final long serialVersionUID = -4753767777928836759L;
	
	private Gson gson;
	private Socket socket;
	
	
	private JPanel mainPanel;
	private CardLayout mainCard;
	
	private JTextField usernameField;
	
	private JTextField sendMessageField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApplication frame = new ClientApplication();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public ClientApplication() {
		
		/*===============<< init >>===================*/
		gson = new Gson();
		try {
			socket = new Socket("127.0.0.1", 9090);
			ClientRecive clientRecive = new ClientRecive(socket);
			clientRecive.start();
		} catch (UnknownHostException e1) { 
			e1.printStackTrace();
		} catch (ConnectException e1) { // 호스트 못찾음
			JOptionPane.showMessageDialog(this, "서버에 접속할 수 없습니다.", "접속오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		/*===============<< frame set >>===================*/
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(600, 150, 480, 800);
		
		/*===============<< login panel >>===================*/
		
		mainPanel = new JPanel();
		JPanel loginPanel = new JPanel();
		JPanel roomListPanel = new JPanel();
		JPanel roomPanel = new JPanel();
		
		/*===============<< layout >>===================*/
		
		mainCard = new CardLayout();
		
		mainPanel.setLayout(mainCard);
		loginPanel.setLayout(null);
		roomListPanel.setLayout(null);
		roomPanel.setLayout(null);
		
		/*===============<< panel set >>===================*/
		
		setContentPane(mainPanel);
		mainPanel.add(loginPanel, "loginPanel");
		mainPanel.add(roomListPanel, "roomListPanel");
		mainPanel.add(roomPanel, "roomPanel");
		
		
		/*===============<< login panel >>===================*/
		JButton enterButton = new JButton("접속하기");
		
		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()== KeyEvent.VK_ENTER) {
					enterButton.doClick();
				}
			}
		});
		
		usernameField.setBounds(51, 562, 338, 37);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);
		
		enterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RequestDto<String> usernameCheckReqDto = 
						new RequestDto<String>("usernameCheck", usernameField.getText());
				sendRequest(usernameCheckReqDto);
			}
		});
		enterButton.setBounds(51, 609, 338, 37);
		loginPanel.add(enterButton);
		
		/*===============<< roomList panel >>===================*/
		
		JScrollPane roomListScroll = new JScrollPane();
		roomListScroll.setBounds(123, 0, 331, 751);
		roomListPanel.add(roomListScroll);
		
		JList roomList = new JList();
		roomListScroll.setViewportView(roomList);
		
		JButton createRoomButton = new JButton("방생성");
		createRoomButton.setBounds(12, 67, 97, 87);
		roomListPanel.add(createRoomButton);
		
		/*===============<< room panel >>===================*/
		
		JScrollPane joinUserListScroll = new JScrollPane();
		joinUserListScroll.setBounds(0, 0, 356, 98);
		roomPanel.add(joinUserListScroll);
		
		JList joinUserList = new JList();
		joinUserListScroll.setViewportView(joinUserList);
		
		JButton roomExitbutton = new JButton("나가기");
		roomExitbutton.setBounds(357, 0, 97, 98);
		roomPanel.add(roomExitbutton);
		
		JScrollPane chattingContentScroll = new JScrollPane();
		chattingContentScroll.setBounds(0, 100, 454, 612);
		roomPanel.add(chattingContentScroll);
		
		JTextArea chattingContent = new JTextArea();
		chattingContentScroll.setViewportView(chattingContent);
		
		sendMessageField = new JTextField();
		sendMessageField.setBounds(0, 713, 386, 38);
		roomPanel.add(sendMessageField);
		sendMessageField.setColumns(10);
		
		JButton sendButton = new JButton("전송");
		sendButton.setBounds(385, 713, 69, 38);
		roomPanel.add(sendButton);
		
	
	}
	
	private void sendRequest(RequestDto<?> requestDto) {
		String reqJson = gson.toJson(requestDto);
		OutputStream outputStream = null;
		PrintWriter printWriter = null;
		try {
			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(outputStream, true);
			printWriter.println(reqJson);
			System.out.println("클라이언트 -> 서버: " + reqJson);
		} catch (IOException e) {
			e.printStackTrace();

		}
		
	}
}
