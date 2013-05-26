package org.skeeve.launcher;

public class Realm{
	static byte unnamedRealms;
	static byte count;
	private String name;
	private String address;
	
	static {
		if (Options.isFirstLoad == true){
			unnamedRealms = 1;
			count = 0;
		}
	}
	
	Realm(String address){
		this("Сервер " + unnamedRealms, address);
		unnamedRealms+=1;
	}
	
	Realm(String name, String address){
		this.name = name;
		this.address = address;
	}
	public String toString(){
		return name + " (" + address +")";
	}
	
	public static Realm stringToRealm(String string){
		Realm realm = new Realm(string.substring(0, (string.indexOf('(')-1)),
				string.substring((string.indexOf('(')+1), string.indexOf(')')));
		return realm;
	}
}
