package p2p;

import java.util.ArrayList;
import java.util.List;

public class Plik {
	String nazwa;
	String suma;
	List<String> adresy = new ArrayList<String>();
	
	Plik(String n, String s, String a)
	{
		nazwa=n;
		suma=s;
		adresy.add(a);
	}
	
	void dodajAdres(String a)
	{
		adresy.add(a);
	}
	
	String getSuma()
	{
		return suma;
	}
	
	List<String> getAdresy()
	{
		return adresy;
	}

}
