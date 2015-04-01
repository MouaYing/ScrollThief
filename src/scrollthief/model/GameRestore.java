package scrollthief.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class GameRestore {
	
	private GameData data;
	private String filePath = "gamedata.dat";
	
	public GameRestore(GameData data) {
		this.data = data;
	}
	

	public GameRestore() {
		this.data = new GameData();
	}

	public GameData getData() {
		return data;
	}

	public boolean Save() {
		// serialize the Queue
		System.out.println("serializing Game Data");
		try {
			File f = new File(filePath);
			if(!f.exists()){
				if(!f.createNewFile()){
					return false;
				}
			}
		    FileOutputStream fout = new FileOutputStream(filePath);
		    ObjectOutputStream oos = new ObjectOutputStream(fout);
		    oos.writeObject(data);
		    oos.close();
		}
	   catch (Exception e) { 
		   e.printStackTrace();
		   return false;
		}
		return true;
	}
	
	public boolean Load() {
	    
		data = new GameData();
		    
		// unserialize the Queue
		System.out.println("unserializing Game Data");
		try {
			File f = new File(filePath);
			if(!f.exists())
				return false;
			FileInputStream fin = new FileInputStream(filePath);
			ObjectInputStream ois = new ObjectInputStream(fin);
			data = (GameData) ois.readObject();
			ois.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;    
	}
}
