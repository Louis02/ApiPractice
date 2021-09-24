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

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;
import com.neovisionaries.ws.client.WebSocketState;

public class discordBot extends WebSocketAdapter {
	String channelID;
	String token;

	public discordBot(String c, String t)  {
		this.channelID = c;
		this.token = t;
		WebSocketFactory wsf = new WebSocketFactory();
		try {
			WebSocket web = wsf.createSocket(getGateway());
			web.addListener(this);
			try {
				web.connect();
			}
			catch(WebSocketException e){
				
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			web.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		String channelID = "890750425744171111";
		BufferedReader br = new BufferedReader(new FileReader("/Users/league/Desktop/Token.txt"));
		String token = br.readLine();
		br.close();
		new discordBot(channelID, token);
	}

	public void botMessage(String input) {
		try {

			String message = "{\"content\":\"" + input + "\"}";
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
			JsonObject o = getJsonObjectFromString(data);
			return o.getString("url");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

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
	System.out.println("disconnected");
	}
	@Override
	public void onError(WebSocket websocket, WebSocketException cause) {
		System.out.println(cause);
	}
	@Override
	public void onTextMessage(WebSocket websocket, String text) {
	System.out.println(text);
	}
	@Override
	public void onFrame(WebSocket websocket, WebSocketFrame frame) {
		System.out.println(frame);
	}
}
