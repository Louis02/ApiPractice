import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFileChooser;
import javax.swing.Timer;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

public class discordBot extends WebSocketAdapter implements ActionListener {
	String channelID;
	String token;
	String name;
	WebSocket socket;
	int heartbeatInterval;
	Timer hbTimer;
	WebSocket webS;
	private String sessionID;
	private Integer sequenceCode;
	private boolean reconnect;

	public discordBot(String c, String t, String n) {
		this.channelID = c;
		this.token = t;
		this.name = n;
		makeConnectionMethod();

	}

	private void makeConnectionMethod() {

		try {
			WebSocketFactory wsf = new WebSocketFactory();
			this.socket = wsf.createSocket(getGateway());
			socket.addListener(this);

			socket.connect();
		} catch (IOException e) {

			e.printStackTrace();
		}

		catch (WebSocketException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {

		BufferedReader br = new BufferedReader(new FileReader("/Users/league/Desktop/Token.txt"));
		String name = br.readLine();
		String channelID = br.readLine();
		String token = br.readLine();
		br.close();
		new discordBot(channelID, token, name);
	}

	public void botMessage(String input) {
		String message = "{\"content\":\"whatsup\"}";
		try {
			System.out.println(message);
			URL url = new URL("https://discord.com/api/channels/" + channelID + "/messages");
			HttpsURLConnection hc = (HttpsURLConnection) url.openConnection();
			hc.setDoOutput(true);
			hc.setRequestMethod("POST");
			hc.setRequestProperty("Authorization", "Bot " + token);
			hc.setRequestProperty("Content-Type", "application/json");
			hc.setRequestProperty("User-Agent", "");
			OutputStream out = hc.getOutputStream();
			out.write(message.getBytes());
			out.flush();
			out.close();
			hc.getInputStream().close();
			hc.disconnect();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getGateway() {
		String ans = "";
		try {
			URL url = new URL("https://discord.com/api/gateway/bot");
			HttpsURLConnection hc = (HttpsURLConnection) url.openConnection();
			hc.setRequestMethod("GET");
			hc.setRequestProperty("Authorization", "Bot " + token);
			hc.setRequestProperty("Content-Type", "application/json");
			hc.setRequestProperty("User-Agent", "");
			InputStream in = hc.getInputStream();
			String data = "";
			int v = in.read();
			while (v != -1) {
				data += (char) v;
				v = in.read();
			}
			System.out.println(data);
			in.close();
			hc.disconnect();
			JsonObject o = getJsonObjectFromString(data);
			ans = o.getString("url");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ans;

	}

	public JsonObject getJsonObjectFromString(String input) {
		JsonReader reader = Json.createReader(new StringReader(input));
		JsonObject obj = reader.readObject();

		return obj;

	}

	@Override
	public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
		System.out.println("connected");
	}

	@Override
	public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
			boolean closedByServer) {
		if (closedByServer) {
			reconnect = true;
			makeConnectionMethod();
		}
		System.out.println(clientCloseFrame);
		System.out.println(serverCloseFrame);
		System.out.println("disconnected");
	}

	@Override
	public void onError(WebSocket websocket, WebSocketException cause) {
		System.out.println(cause);
	}

	@Override
	public void onFrame(WebSocket websocket, WebSocketFrame frame) {
		// System.out.println(frame);
		String payload = frame.getPayloadText();
		JsonObject obj = getJsonObjectFromString(payload);
		try {
			sequenceCode = obj.getInt("s");
		} catch (Exception e) {
			sequenceCode = null;
		}
		int op = obj.getInt("op");
		if (op == 10) {
			JsonObject d = obj.getJsonObject("d");
			heartbeatInterval = d.getInt("heartbeat_interval");
			System.out.println(heartbeatInterval);
			String auth = "";
			if (reconnect) {
				auth = "{\n" + 
						"  \"op\": 6,\n" + 
						"  \"d\": {\n" + 
						"    \"token\": \""+token+"\",\n" + 
						"    \"session_id\": \""+sessionID+"\",\n" + 
						"    \"seq\": "+sequenceCode+"\n" + 
						"  }\n" + 
						"}";

				reconnect = false;

			} else {
				auth = "{\n\r" + "  \"op\": 2,\n\r" + "  \"d\": {\n\r" + "    \"token\": \"" + token + "\",\n\r"
						+ "    \"intents\": 513,\n\r" + "    \"properties\": {\n\r" + "      \"$os\": \"linux\",\n\r"
						+ "      \"$browser\": \"my_library\",\n\r" + "      \"$device\": \"my_library\"\n\r"
						+ "    }\n\r" + "  }\n\r" + "}\n\r" + "";
			}
			socket.sendText(auth);
		} else if (op == 0) {
			String type = obj.getString("t");
			if (type.equals("MESSAGE_CREATE")) {
				JsonObject d = obj.getJsonObject("d");
				JsonObject author = d.getJsonObject("author");
				String message = d.getString("content");
				String user = author.getString("username");
				if (!user.equals(name)) {
					messageReceived(message, user);
				}
			} else if (type.equals("READY")) {
				JsonObject j = obj.getJsonObject("d");

				sessionID = j.getString("session_id");
				hbTimer = new Timer(heartbeatInterval, this);
				sendHeartbeat();
				 hbTimer.start();
			} else if (op == 1) {
				sendHeartbeat();
			}
		}

	}

	void messageReceived(String message, String user) {
		System.out.println(message + "   " + user);
		if (message.equals("apple")) {
			System.out.println("hiiiiiiiiiiiii");
			botMessage("hi");
		}
	}

	void sendHeartbeat() {
		String s = "{\n" + "    \"op\": 1,\n" + "    \"d\": " + sequenceCode + "\n" + "}";
		socket.sendText(s);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		sendHeartbeat();
	}

}
