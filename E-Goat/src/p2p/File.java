package p2p;

import java.util.ArrayList;
import java.util.List;

class File {
	private String sum;
	private List<String> addresses = new ArrayList<String>();
	
	File(String s, String a)
	{
		sum = s;
		addresses.add(a);
	}
	
	void addAddress(String a)
	{
		addresses.add(a);
	}
	
	String getSum()
	{
		return sum;
	}
	
	List<String> getAddresses()
	{
		return addresses;
	}

}
