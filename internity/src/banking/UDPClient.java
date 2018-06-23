package banking;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {

	public static void main(String[] args) 
	{
		DatagramSocket ds = null;              //
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			ds = new DatagramSocket();
			
			System.out.println("Enter the number");
			String i = br.readLine();
				
			byte[] data = String.valueOf(i).getBytes();                              //bytes of string
				
			InetAddress ip = InetAddress.getByName("localhost");		//IP address to which data is sent
				
			DatagramPacket dp = new DatagramPacket(data, data.length, ip, 9999);    //Packet that is sent (data, size of data, ip, port no)
			ds.send(dp);				//send data packet
				
			byte[] b1 = new byte[1024];   //will hold the received data in byte format
			DatagramPacket dp1 = new DatagramPacket(b1,b1.length);
			ds.receive(dp1);
			
			String str = new String(dp1.getData(),0,dp1.getLength());
			System.out.println("Result: "+str);
		}
		catch(Exception e){}
		finally
		{
			ds.close();
		}
	}

}
