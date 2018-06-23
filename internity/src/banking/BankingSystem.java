package banking;



import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BankingSystem  
{


	static Connection connection;
	String name;
	int accountNo;
	String accType;
	String password;
	double currentBal;
	String pan;
	File file;
	Date d = new Date();
	String date = new SimpleDateFormat("dd-MMM-yyyy").format(d);
	String time = new SimpleDateFormat("HH:mm:ss").format(d);
	
	
	SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
	
	public void createAccount()
	{
		Savepoint sp2 = null;
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			connection.setAutoCommit(false);
				
			sp2 = connection.setSavepoint();
				
			while (true) 
			{
				System.out.println("Enter your name");
				this.name = br.readLine();
				
				//checking no. of characters for name
				if (this.name.length() == 0) {
					System.out.println("Fill name properly");
					continue;
				} 
				else if (this.name.length() > 10) {
					System.out.println("Name can have maximum 10 characters");
				}
				else
					break;
			}
				
				
			while (true)          
			{
				System.out.println("Enter the pan card number");
				this.pan = new String(br.readLine());
				//checking no. of characters for PAN number
				if (this.pan.length() == 0 || this.pan.length() > 10) {
					System.out.println("Invalid PAN card number");
					continue;
				} 
				else
					break;
			}
				
			System.out.println("Enter the type of Account you want to create \n1. Savings    2. Current");
			while (true) 
			{
				String type = br.readLine();
				if (type.equals("1"))
				{
					this.accType = "SAVINGS";
					break;
				}
				else if (type.equals("2"))
				{
					this.accType = "CURRENT";
					break;	
				}
				else 
					System.out.println("Enter either '1' or '2'");
			}
				
				
			PreparedStatement pst01 = connection.prepareStatement("Select account_no from accountinfo order by account_no desc");
			ResultSet rs01 = pst01.executeQuery();
			int flag = 0;
			while(rs01.next())
			{
				this.accountNo = rs01.getInt(1)+1;
				flag = 1;
				break;
			}
				
			if(flag == 0)
				this.accountNo = 1000000000;
				
			while (true) 
			{
				System.out.println("Enter the password");
				this.password = br.readLine();
					
				//Ensuring password has valid number of characetrs
				if(this.password.length()==0)
					System.out.println("Enter password properly");
					
				else if(this.password.length() > 10)
					System.out.println("Password can't have more thn 10 characters");
					
				else
					break;
			}
				
				
			String fileName = new String("t"+this.accountNo+".txt");
			file = new File("C://Users//Vasu Bansal//Desktop//Java//"+fileName);
			file.createNewFile();
				
			PreparedStatement pst02 = connection.prepareStatement("Insert into accountinfo values (?,?,?,?,?,?,?)");
			pst02.setInt(1, this.accountNo);
			pst02.setString(2, this.password);
			pst02.setString(3, this.name.toUpperCase());
			pst02.setDouble(4, this.currentBal);
			pst02.setString(5, this.accType);
			pst02.setString(6, this.pan);
			pst02.setString(7, date);
				
				
			if(pst02.executeUpdate()==1)
				System.out.println("Account created");
				
			System.out.println("Your Account Number is: "+this.accountNo);
				
			String sql04 = "create table t"+this.accountNo+" (particulars varchar(15) not null, cheque_no int, dates varchar(11) not null, times varchar(8) not null,";
			sql04 += " credit number, debit number, total_amt number not null)";
				
			PreparedStatement pst04 = connection.prepareStatement(sql04);
				
			pst04.executeUpdate();
					
			connection.commit();
				
			while(true)
			{
				System.out.println("\nCurrent Balance: Rs"+this.currentBal);
				System.out.println("Enter the choice");
				System.out.println("Press 1 to credit money");
				System.out.println("Press 2 to debit money");
				System.out.println("Press 3 to print the paassbook");
				System.out.println("Press 4 to change personal settings");
				System.out.println("Press 5 to exit");
				int ch = Integer.parseInt(br.readLine());
					
				switch(ch)
				{
					case 1: this.credit();
							break;
							
					case 2: this.debit();
							break;
								
					case 3: this.printPassbook();
							break;
						
					case 4: this.changeSettings();
							break;
								
					case 5: System.exit(0);
				}
			}
		} 
		catch (IOException |SQLException e) 
		{	
				System.out.println("Some Error occurred");
				try {connection.rollback(sp2);} catch(SQLException e1) {}
		}
		finally
		{
			try {connection.close();} catch(SQLException e) {}
		}
	}
		

	
	
	public int oldUser(int acc, String pass)
	{
		while(true)
		{
			try
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
				connection.setAutoCommit(false);
				
				int flag = 00;
				
				//Extracting info of old user
				PreparedStatement pst03 = connection.prepareStatement("Select * from accountinfo where account_no=?");
				pst03.setInt(1, acc);
				ResultSet rs03 = pst03.executeQuery();
				
				while(rs03.next())
				{
					if(rs03.getString(2).equals(pass))
					{
						this.accountNo = rs03.getInt(1);
						this.name = rs03.getString(3);
						this.currentBal = rs03.getInt(4);
						
						flag = 11;
					}
					else
						flag = 01;
				}
				
				file = new File("C://Users//Vasu Bansal//Desktop//Java//"+"t"+this.accountNo+".txt");
				
				if(flag==11)
				{
					System.out.println("Logged in!");
					System.out.println("\nWelcome "+this.name);
					System.out.println("Your current balance is Rs "+this.currentBal);
					
					while(true)
					{
						System.out.println("Enter the choice");
						System.out.println("Press 1 to credit money");
						System.out.println("Press 2 to debit money");
						System.out.println("Press 3 to print the paassbook");
						System.out.println("Press 4 to change personal settings");
						System.out.println("Press 5 to create a new user");
						System.out.println("Press 6 to exit");
						
						String ch = br.readLine();
						
						if(!(ch.equals("1") || ch.equals("2") || ch.equals("3") || ch.equals("4") || ch.equals("5") || ch.equals("6")))
							continue;
						
						switch(Integer.parseInt(ch))
						{
							case 1: this.credit();
									break;
									
							case 2: this.debit();
									break;
									
							case 3: this.printPassbook();
									break;
							
							case 4: int r = this.changeSettings();
									if(r==2)
										return r;
									break;
									
							case 5: return 1;
									
								
							case 6: System.exit(0);
						}
					}
					
					
				}
				else if(flag==01)
				{
					System.out.println("Invalid password");
					System.out.println("Enter Password");
					pass = br.readLine();
					
				}
				else
				{
					System.out.println("Invalid Account Number");
					System.out.println("Enter Account Number");
					acc = Integer.parseInt(br.readLine());
					
					System.out.println("Enter Password");
					pass = br.readLine();
				}
			}
			catch(SQLException |IOException e)
			{
				System.out.println(e);
			}
			
			finally
			{
				try {connection.close();} catch(SQLException e) {}
			}
			return -1;
		}
	
	}
	
	public void credit()
	{
		Savepoint sp1=null;
		FileWriter writer = null;
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			connection.setAutoCommit(false);
			
			System.out.println("Enter the amount to be credited");
			double camount;
			while (true) 
			{
				try 
				{
					camount = Double.parseDouble(br.readLine());
					if(camount<=0)
						throw new NumberFormatException();
					break;
				}catch (NumberFormatException e) {System.out.println("Enter valid amount");}
			}
			
			if(camount>0)
				this.currentBal += camount;
			
			String part = new String();
			while(true)
			{
				System.out.println("Enter the particulars");
				
				part = new String(br.readLine());
				if(part.length()==0)
					System.out.println("Particulars can't be empty");
				
				else if(part.length()>15)
					System.out.println("Particulars can have maximum 15 characters");
				
				else 
					break;
			}
			
			
			int cheque;
			if(part.equalsIgnoreCase("cash"))
					cheque = 0;
			else
			{
				System.out.println("Enter the cheque number if any (if not then enter '0')");
				while (true) 
				{
					try 
					{
						cheque = Integer.parseInt(br.readLine());
						break;
					} 
					catch (NumberFormatException e) 
					{
						System.out.println("Enter valid cheque number");
					}
				}
			}
			
					
			sp1 = connection.setSavepoint();
			
			PreparedStatement pst04 = connection.prepareStatement("Insert into t"+this.accountNo+" values (?,?,?,?,?,?,?)");
//			pst04.setInt(1, 1);
			pst04.setString(1, part.toUpperCase());
			pst04.setInt(2, cheque);
			pst04.setString(3, this.date);
			pst04.setString(4, this.time);
			pst04.setDouble(5, camount);
			pst04.setDouble(6, -1);
			pst04.setDouble(7, this.currentBal);
			
			PreparedStatement pst05 = connection.prepareStatement("update accountInfo set current_bal = ? where account_no = ?");
			pst05.setDouble(1, this.currentBal);
			pst05.setInt(2, this.accountNo);
			
			if((pst04.executeUpdate() & pst05.executeUpdate())==1)
			{
				System.out.println("Amount Credited");
				System.out.println("Final Balance: Rs" +this.currentBal);	
			}
			else
				throw new SQLException("Error Occurred!");
			
			
			writer = new FileWriter(this.file);
			writer.write("Credit Operation");
			writer.write("\r\n");
			writer.write("Date: "+this.date);
			writer.write("\r\n");
			writer.write("Amount credited: "+camount);
			writer.write("\r\n");
			writer.write("Final Balance:" +this.currentBal);
			writer.write("\r\n");
			writer.write("\r\n");
			
			connection.commit();
			
		} 
		catch (SQLException |IOException e) 
		{
			try {connection.rollback(sp1);} catch(SQLException e1) {}
			e.printStackTrace();
		}
		finally
		{
			try {connection.close(); writer.close();} catch(SQLException |IOException e) {}
		}
	}
	
	public void debit()
	{
		Savepoint sp1=null;
		FileWriter writer = null;
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			connection.setAutoCommit(false);
			
			double damount;
			while (true) 
			{
				System.out.println("Enter the amount to be debited");
				try 
				{
					damount = Double.parseDouble(br.readLine());
					if(damount>0){
						if(damount<=this.currentBal){
							if(damount<30000){
								this.currentBal -= damount;
								break;
							}
							else
								System.out.println("Amount more than 30000 can't be withdrawn");
						}
						else
							System.out.println("Not enough to debit money");
					}
					else
						System.out.println("Negative transaction not allowed");
					
				} 
				catch (NumberFormatException e) 
				{
					System.out.println("Invalid Debit Amount");
				}
			}
			
			
				
			String part = new String();
			while(true)
			{
				System.out.println("Enter the particulars");
				
				part = new String(br.readLine());
				if(part.length()==0)
					System.out.println("Particulars can't be empty");
				
				else if(part.length()>15)
					System.out.println("Particulars can have maximum 15 characters");
				
				else 
					break;
			}
			
			
			int cheque;
			if(part.equalsIgnoreCase("cash"))
					cheque = 0;
			else
			{
				System.out.println("Enter the cheque number if any (if not then enter '0')");
				while (true) 
				{
					try 
					{
						cheque = Integer.parseInt(br.readLine());
						break;
					} 
					catch (NumberFormatException e) 
					{
						System.out.println("Enter valid cheque number");
					}
				}
			}
			
					
			sp1 = connection.setSavepoint();
			
			PreparedStatement pst04 = connection.prepareStatement("Insert into t"+this.accountNo+" values (?,?,?,?,?,?,?)");
//			pst04.setInt(1, 1);
			pst04.setString(1, part.toUpperCase());
			pst04.setInt(2, cheque);
			pst04.setString(3, this.date);
			pst04.setString(4, this.time);
			pst04.setDouble(5, -1.0);
			pst04.setDouble(6, damount);
			pst04.setDouble(7, this.currentBal);
			
			PreparedStatement pst05 = connection.prepareStatement("update accountInfo set current_bal = ? where account_no = ?");
			pst05.setDouble(1, this.currentBal);
			pst05.setInt(2, this.accountNo);
			
			int i=pst04.executeUpdate();
			int j=pst05.executeUpdate();
			if((i==1) && (j==1))
			{
				System.out.println("Amount Debited");
				System.out.println("Final Balance: Rs "+this.currentBal);
			}
			else
				throw new SQLException("Error Occurred!");
			
			writer = new FileWriter(this.file);
			writer.write("Debit Operation");
			writer.write("\r\n");
			writer.write("Date: "+this.date);
			writer.write("\r\n");
			writer.write("Amount Debit: "+damount);
			writer.write("\r\n");
			writer.write("Final Balance:" +this.currentBal);
			writer.write("\r\n");
			writer.write("\r\n");
			
			connection.commit();
			
		} 
		catch (SQLException |IOException e) 
		{
			try {connection.rollback(sp1);} catch(SQLException e1) {e1.printStackTrace();}
			e.printStackTrace();
		}
		finally
		{
			try {connection.close(); writer.close();} catch(SQLException |IOException e) {e.printStackTrace();}
		}
	}
	
	public void printPassbook()
	{
		try 
		{
			//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			connection.setAutoCommit(false);
			
			int n=0;
			
			System.out.println("\nparticulars\tcheque_no\tdate\ttime\tcredit\tdebit\ttoal_amt");
			PreparedStatement pst06 = connection.prepareStatement("Select * from t"+this.accountNo+" order by dates desc, times desc");
			ResultSet rs06 = pst06.executeQuery();
			while(rs06.next())
			{
				System.out.print(rs06.getString("particulars")+"\t   ");
				
				if(rs06.getInt("cheque_no") == 0)
					System.out.print("-"+"\t");
				else
					System.out.print(rs06.getInt("cheque_no")+"\t");
				System.out.print(rs06.getString("dates")+"\t");
				System.out.print(rs06.getString("times")+"\t");
				
				if(rs06.getInt("credit")==-1)
					System.out.print("-\t");
				else
					System.out.print(rs06.getInt("credit")+"\t");
				
				if(rs06.getInt("debit")==-1)
					System.out.print("-"+"\t");
				else
					System.out.print(rs06.getInt("debit")+"\t");
				
				System.out.println(rs06.getInt("total_amt")+"\t");
				 
				n=1;
			}
			
			if(n==0)
				System.out.println("No Transactions performed till now!\n\n");
			
			connection.commit();
		} 
		catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {connection.close(); } catch(SQLException se) {}
		}
	}
	
	
	public int changeSettings()
	{
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			connection.setAutoCommit(false);
			
			System.out.println("Enter your choice");
			System.out.println("Enter 1 to change password");
			System.out.println("Enter 2 delete the account");
			
			while(true)
			{
				String c = br.readLine(); 
				if(c.equals("1"))
				{
					System.out.println("Enter the new password");
					String password = br.readLine();
					
					PreparedStatement pst08 = connection.prepareStatement("update accountinfo set password = ? where account_no = ?");
					pst08.setString(1, password);
					pst08.setInt(2, this.accountNo);
					
					if(pst08.executeUpdate()==1)
					{
						System.out.println("Password changed");
						break;
					}
					
				}
				else if(c.equals("2"))
				{
					return 2;
				}
				else
				{
					System.out.println("Enter valid number");
				}
				
			}
			
		} 
		catch (SQLException | IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try {connection.close();} catch(SQLException se) {}
		}
		return -1;
	}
	
	
	public static void main(String[] args) 
	{
		try
		{
			connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			connection.setAutoCommit(false);
			BankingSystem customer;
			
			
			
			while (true) 
			{
				System.out.println("Enter the choice");
				System.out.println("1. New User");
				System.out.println("2. Old user");
				int ch=0;
				try 
				{
					ch = Integer.parseInt(br.readLine());
				} 
				catch (NumberFormatException e1) {
					System.out.println("Invalid Input");
					continue;
				}
				
				switch (ch) 
				{
					case 1:
						customer = new BankingSystem();
						customer.createAccount();
						break;
						
					case 2:
						customer = new BankingSystem();
						
						int acc;
						
						while(true)
						{
							try 
							{
								System.out.println("Enter the account Number");
								acc = Integer.parseInt(br.readLine());
								if(String.valueOf(acc).length()>10)
									System.out.println("Invalid account no");
								else break;
							} catch (NumberFormatException e) {
								System.out.println("Inavlid account number");
							}
							
						}
						
						System.out.println("Enter the password");
						String p;
						while(true)
						{
							p = br.readLine();
							if(String.valueOf(p).length()>15)
								System.out.println("Invalid password");
							else break;
						}
						
						int num = customer.oldUser(acc, p);
						
						if (num == 1) {
							customer = null;
							ch = 1;
							continue;
						}
						else if(num == 2)
						{
							connection = DriverManager.getConnection("jdbc:oracle:thin:@VizarD:1521:xe","hr","sanchi");
							PreparedStatement pst09 = connection.prepareStatement("Delete from accountinfo where account_no = ?");
							pst09.setInt(1, acc);
							pst09.executeUpdate();
							
							PreparedStatement pst10 = connection.prepareStatement("Drop table t"+acc);
							pst10.executeUpdate();
							
							System.out.println("Account deleted\n");
							connection.commit();
						}
						break;
				}
			}
			
		}
		catch(SQLException | IOException e1)
		{
			e1.printStackTrace();
		}
		finally
		{
			try {connection.close();} catch(SQLException e) {}
		}

	}

}
