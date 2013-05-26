package org.skeeve.launcher;

import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class Options {
	
	static int defaultServer = 0; 
	static String gameDirectory;
	static boolean defaultServerFlag;
	static Preferences userPrefs;
	static boolean isFirstLoad;
	static byte unnamedRealms;
	static byte count;
	public Options(){
		
	}
	
	static void loadOptions(){
		userPrefs = Preferences.userRoot().node("SkeeveLauncherPrefs");
		defaultServerFlag = userPrefs.getBoolean("DefaultServerFlag", defaultServerFlag);
		Realm.count = (byte)userPrefs.getInt("RealmCounter", Realm.count);
		Realm.unnamedRealms = (byte) userPrefs.getInt("UnnamedRealmsCounter", Realm.unnamedRealms);
		gameDirectory = userPrefs.get("GameDirectory", gameDirectory);
		defaultServer = userPrefs.getInt("DefaultServer", defaultServer);
		isFirstLoad = userPrefs.getBoolean("IsFirstLoad", isFirstLoad);
		if (isFirstLoad() == true){
			Realm.unnamedRealms = 1;
			Realm.count = 0;
			userPrefs.putInt("RealmCounter", Realm.count);
			userPrefs.putInt("UnnamedRealmsCounter", Realm.unnamedRealms);
		}
	}
	
	static Realm[] loadPossibleRealms(){
		Realm[] realms; 
		realms = new Realm[Realm.count];
		String string;
		String name;
		for (int i=0;i<Realm.count;i++){
			name = "Server "+i;
			string = userPrefs.get(name, "Error");
			if (string != "Error"){
				realms[i] = Realm.stringToRealm(string);
			}
			else { 
				JOptionPane.showMessageDialog(null,"Îøèáêà çàãðóçêè äàííûõ: "+name,
					"À âîò õóé! ìýéëìè, ÅÏÒÀ!", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
		return realms;
	}
	
	static boolean isFirstLoad(){
		if (gameDirectory == null){
			isFirstLoad = true;
		}
		return isFirstLoad;
	}
	
	static void setDefaultServer(int number){
		userPrefs.putInt("DefaultServer", number);
		defaultServer = number; 
	}
	
	static void setGameDirectory(String gameDirectory){
		userPrefs.put("GameDirectory", gameDirectory);
		Options.gameDirectory = gameDirectory;
	}
	
	static void addServer(Realm realm){
		userPrefs.put("Server "+Realm.count, realm.toString());
		Realm.count++;	
		userPrefs.putInt("RealmCounter", Realm.count);
	}
	static void setDefaultServerFlag(boolean flag){
		userPrefs.putBoolean("DefaultServerFlag", flag);
		defaultServerFlag = flag;
	}
	static void deleteServer(int server){
		String value;
		for (int i=server;i<(Realm.count-1);i++){
			value = userPrefs.get("Server "+(i+1), "Error");
			userPrefs.put("Server "+i, value);
		}
		Realm.count--;
		userPrefs.remove("Server " + (Realm.count));
		userPrefs.putInt("RealmCounter", Realm.count);
	}
	static void editServer(int server, String name, String realmlist){
		userPrefs.put("Server " + server, name + " ("+realmlist+")");
	}
}
