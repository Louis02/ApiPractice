import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.swing.JOptionPane;

public class showFinder {
	public static void main(String[] args) throws IOException {
		String stringInput = JOptionPane.showInputDialog("Please Enter a Show: ");
		int ID = getShowID(stringInput);
		System.out.println(ID);
		URL site = new URL("https://api.tvmaze.com/shows/"+ID+"/seasons");
		HttpURLConnection connection = (HttpURLConnection) site.openConnection();
		InputStream input = connection.getInputStream();
		JsonReader reader = Json.createReader(input);
		JsonArray obj = reader.readArray();
		for(int i = 0; i<obj.size();i++) {
			JsonObject getObj = obj.getJsonObject(i);
			
			System.out.println("Season "+(i+1)+" "+getObj.getInt("episodeOrder"));
			System.out.println(getObj.getString("premiereDate"));
		}
		String name  = obj.getString(ID, "name");
		input.close();
	}

	public static int getShowID(String show) throws IOException {
		
		URL site = new URL("https://api.tvmaze.com/singlesearch/shows?q="+show);
		HttpURLConnection connection = (HttpURLConnection) site.openConnection();
		InputStream input = connection.getInputStream();
		JsonReader reader = Json.createReader(input);
		JsonObject obj = reader.readObject();
		input.close();
		int ID = obj.getInt("id");
		

		return ID;
	}
}
