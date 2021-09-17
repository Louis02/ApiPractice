import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RandomDogPic {
	static JFrame frame = new JFrame();
	static JFrame dogFrame = new JFrame();
	static JButton button = new JButton();
	static JLabel pic = new JLabel();
	public static void main(String[] args) throws IOException {
		
	
		
		button.setText("New Random Dog");
		frame.add(button);
		frame.setVisible(true);
		frame.pack();
		frame.setLocation(700, 700);
		button.addActionListener((e)->{
			try {
				whenPressed();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	public static void whenPressed() throws IOException {

		URL site = new URL("https://dog.ceo/api/breeds/image/random");
		HttpURLConnection connection = (HttpURLConnection)site.openConnection();
		InputStream input = connection.getInputStream();
		JsonReader reader = Json.createReader(input);
		JsonObject obj = reader.readObject();
		
		input.close();
		String dogPic = obj.getString("message");
		System.out.println(dogPic);
		
		
		
		
		pic.setIcon(new ImageIcon(new URL(dogPic)));
		dogFrame.add(pic);
		
		dogFrame.setVisible(true);
		dogFrame.pack();
	}
}
