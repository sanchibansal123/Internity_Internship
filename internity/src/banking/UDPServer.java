package banking;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.IOException;
import java.net.DatagramPacket;


public class UDPServer
{
	public static void main(String[] args)
	{
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(9999);			//ds socket will accept packets comming from port 9999 only
			
			byte[] b= new byte[1024];				//
			
			System.out.println("Server Program running");
			
			//This will store the received packet (byte [] that will store received info, size of array)
			DatagramPacket dp = new DatagramPacket(b,b.length);		
			
			//ds will receive the incoming packet and save it in dp
			ds.receive(dp);			
			
			String str = new String(dp.getData(),0,dp.getLength());
			
			byte[] b2;
			InetAddress ia = InetAddress.getByName("localhost");
			DatagramPacket dp2 = null;
			try 
			{
				int num = Integer.parseInt(str);
				int result = num * 2;
				
				b2 = String.valueOf(result).getBytes();   //converting result to byte
				
				
				dp2 = new DatagramPacket(b2,b2.length,ia,dp.getPort());
				ds.send(dp2);
			} catch (NumberFormatException e) 
			{
				b2 = String.valueOf("Invalid input").getBytes();
				dp2 = new DatagramPacket(b2,b2.length,ia,dp.getPort());
				ds.send(dp2);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally
		{
			ds.close();
		}
		
	}
}
