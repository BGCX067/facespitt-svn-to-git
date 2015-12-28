import java.sql.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.ArrayList;
public class FacesDB 
{
	  private Connection connection; //used to hold the jdbc connection to the DB
	  private Statement statement; //used to create an instance of the connection
	  private ResultSet resultSet; //used to hold the result of your query (if one exists)
	  private String query;  //this will hold the query we are using
	  private String username, password;
	  private String lastLogin;
	
	public FacesDB()
	{
		username = "******"; //This is your username in oracle
		password = "******"; //This is your password in oracle
		try 
		{
		    DriverManager.registerDriver (new oracle.jdbc.driver.OracleDriver());
		    String url = "jdbc:oracle:thin:@db10.cs.pitt.edu:1521:dbclass"; 
		    connection = DriverManager.getConnection(url, username, password);     
		}
		catch(Exception Ex)  
		{
		    System.out.println("Error connecting to database.  Machine Error: " +
				       Ex.toString());
		}
	}
	public boolean Login(String Email, String Pword)
	{	
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    
		    query = "SELECT lastlogin FROM profile where email='"+Email+"' and password= '"+Pword+"'";
		    resultSet = statement.executeQuery(query);
		    if(resultSet.next())
		    {	
				updateLogin(Email);
		    	return true;
		    }
		    else
		    {
		    	return false;
		    }
		
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
			return false;
			
		}
	
	}
	public void updateLogin(String email)
	{
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement();
			query = "select lastlogin from profile where email = '" + email + "'";
			resultSet = statement.executeQuery(query);
			
			if (resultSet.next())
			{
				lastLogin = resultSet.getString(1);
			}
			
    		query = "update profile set lastlogin = systimestamp where email='"+email+"'";
    		int result = statement.executeUpdate(query);
    		if(result==1)
    		{
    			 connection.commit();
    		}
    		else
    		{
    			System.out.println("last login was not updated");
    		}
    		
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
	}
	public profile returnProfile(String email)
	{
		profile current = null;
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "SELECT * FROM profile where email='"+email+"' ";
			resultSet = statement.executeQuery(query);
			if(resultSet.next())
			{	
				
				current= new profile(resultSet.getInt(1),resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getDate(5), resultSet.getString(6),resultSet.getString(7), resultSet.getDate(8));
				
			}
			
			else
			{
				System.out.println("There has been an error with the system!");
				System.exit(0);
			}
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		return current;
		
	}
	
	public profile returnProfileByID(int ID)
	{
		profile current = null;
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "SELECT email FROM profile where userID="+ID+"";
			resultSet = statement.executeQuery(query);
			if(resultSet.next())
			{	
				
				current= returnProfile(resultSet.getString(1));
				
			}
			else
			{
				System.out.println("There has been an error with the system! Sorry");
				System.exit(0);
			}
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		return current;
		
	}
	
	public int returnNewUserID()
	{
		int count=1;
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "SELECT * FROM profile";
			resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				count++;
			}
			return count;
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
			return 0;
		}
		
	}
	
	public int retMsgID()
	{
		int count=1;
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "SELECT * FROM messages";
			resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				count++;
			}
			return count;
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
			return 0;
		}
		
		
	}
	
	
	public void Register(profile newUser)
	{
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "Insert into profile values(?,?,?,?,?,?,?,?)";
			PreparedStatement updateStatement = connection.prepareStatement(query);
			updateStatement.setInt(1, newUser.getUserID());
			updateStatement.setString(2, newUser.getName());
			updateStatement.setString(3, newUser.getEmail());
			updateStatement.setString(4, newUser.getPassword());
			updateStatement.setDate(5,newUser.getDate());
			updateStatement.setString(6, newUser.getPicURL());
			updateStatement.setString(7, newUser.getAboutMe());
			updateStatement.setDate(8, newUser.getLastLogin());
			updateStatement.executeUpdate();
			connection.commit();
			System.out.println("You have been successfully registerd you can now Login");
			
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}	
		
	}
	
	public void AddFriend(profile current)
	{		
		
		ArrayList<Integer> FriendsList = new ArrayList<Integer>();
		ArrayList<Integer> ListOfUsers = new ArrayList<Integer>();
		ArrayList<Integer> PendingFriends= new ArrayList<Integer>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID1,userID2 FROM friends where userID1="+current.getUserID()+" or userID2="+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	final int x = resultSet.getInt(1);
		    	final int y = resultSet.getInt(2);
		    	
		    	if(x!=current.getUserID())
		    	{	
		    		if(!FriendsList.contains(x))
		    		{	
		    			FriendsList.add(x);
		    		}
		    	}
		    	if(y!=current.getUserID())
		    	{	
		    		if(!FriendsList.contains(y))
		    		{
		    			FriendsList.add(y);
		    		}
		    	}	
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT toID,FromID FROM pendingFriends where fromID="+current.getUserID()+" or toID= "+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	final int x = resultSet.getInt(1);
		    	final int y = resultSet.getInt(2);
		    	if(!PendingFriends.contains(x))
		    	{	
		    		if(x!=current.getUserID())
		    		{	
		    			PendingFriends.add(x);
		    		}
		    	}
		    	if(!PendingFriends.contains(y))
		    	{
		    		if(y!=current.getUserID())
		    		{	
		    			PendingFriends.add(y);
		    		}	
		    	}
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		System.out.println("Here is a list of our users");
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID,uname FROM profile";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	System.out.println("User ID: "+resultSet.getInt(1)+" Name: "+resultSet.getString(2));
		    	ListOfUsers.add(resultSet.getInt(1));
		    }
		} 
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		System.out.println();
		Scanner inScan= new Scanner(System.in);
		boolean decesion = true;
		
		int choic=0;
		while(decesion)
		{
			System.out.println("Please Enter the userID of the user you want to add as a friend");
			decesion = false;
			try
			{
				choic=inScan.nextInt();	
			}
			catch(Exception e)
			{
				decesion=true;
				continue;
			}
			if(FriendsList.contains(choic))
			{
				System.out.println("You are already friends!!");
				decesion=true;
				continue;
			}
			if(PendingFriends.contains(choic))
			{
				System.out.println("You have already sent a friend request to that user or that user sent you a friend request");
				decesion=true;
				continue;
			}
			if(!ListOfUsers.contains(choic))
			{
				System.out.println("Please Enter the userID of the user you want to add as a friend from the list");
				decesion=true;
				continue;
			}
			if(choic==current.getUserID())
			{
				System.out.println("You can't send a friend request to yourself");
				decesion=true;
				continue;

			}
			
		}
		
		System.out.println("You are sending a friend request to: "+returnProfileByID(choic).getName());
		System.out.println("Please enter a message to be sent to the user");
		inScan.nextLine();
		String MSG = inScan.nextLine();
		System.out.println("Are you sure you want to send a friend request to: "+returnProfileByID(choic).getName());
		System.out.println("Please enter \"yes\" or \"no\"");
		String confirm = inScan.nextLine();
		while(!confirm.equals("yes")&&!confirm.equals("no"))
		{	
			System.out.println("Are you sure you want to send a friend request to: "+returnProfileByID(choic).getName());
			System.out.println("Please enter \"yes\" or \"no\"");
			confirm = inScan.nextLine();
			
		}
		if(confirm.equals("yes"))
		{
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "Insert into pendingFriends values(?,?,?)";
				PreparedStatement updateStatement = connection.prepareStatement(query);
				updateStatement.setInt(1, current.getUserID());
				updateStatement.setInt(2,choic);
				updateStatement.setString(3,MSG);
				updateStatement.executeUpdate();
				connection.commit();
				System.out.println("Sent Friend Request Successfully");
				
			}
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());	
			}	
		}
		else if(confirm.equals("no"))
		{
			System.out.println("Press any key to return to the main menu");
			String cont = inScan.nextLine();
		}
		
	}
	
	public ArrayList<Integer> returnGroups(profile current)
	{
		ArrayList<Integer> Groups = new ArrayList<Integer>();
		
		try
		{
			connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT gID FROM groupMembership where userID="+current.getUserID()+"";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	Groups.add(resultSet.getInt(1));
		    }
		}
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		int count=0;
		while(count<Groups.size())
		{	
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "SELECT gname FROM groups where gID="+Groups.get(count)+" ";
				resultSet = statement.executeQuery(query);
				if(resultSet.next())
				{		
					System.out.println("Group ID: "+Groups.get(count)+" Group Name: "+resultSet.getString(1));
				}
				count++;
			}	
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}	
		}	
		return Groups;
		
	}
	
	public ArrayList<Integer> returnFriendsList(profile current)
	{
		ArrayList<Integer> FriendsList = new ArrayList<Integer>();
		try 
		{
			connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID1,userID2 FROM friends where userID1="+current.getUserID()+" or userID2="+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {	
				final int x = resultSet.getInt(1);
				final int y = resultSet.getInt(2);
				
				if(x!=current.getUserID())
				{	
					if(!FriendsList.contains(x))
					{	
						FriendsList.add(x);
					}
				}
				if(y!=current.getUserID())
				{	
					if(!FriendsList.contains(y))
					{
						FriendsList.add(y);
					}
				}		
		    }	
	    }    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		int count=0;
		while(count<FriendsList.size())
		{	
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "SELECT uname FROM profile where userID="+FriendsList.get(count)+" ";
				resultSet = statement.executeQuery(query);
				if(resultSet.next())
				{		
					System.out.println("User ID: "+FriendsList.get(count)+" Name: "+resultSet.getString(1));
				}
				count++;
			}	
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}	
		}	
		return FriendsList;
		
	
	}
	
	public ArrayList<String> returnList()
	{
		ArrayList<String> List = new ArrayList<String>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT email FROM profile";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	List.add(resultSet.getString(1));
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		return List;
	}	
	
	public ArrayList<Integer> Friends(profile current)
	{
		ArrayList<Integer> FriendsList = new ArrayList<Integer>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID1,userID2 FROM friends where userID1="+current.getUserID()+" or userID2="+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	final int x = resultSet.getInt(1);
		    	final int y = resultSet.getInt(2);
		    	
				if(x!=current.getUserID())
				{	
		    		if(!FriendsList.contains(x))
		    		{	
		    			FriendsList.add(x);
		    		}
				}
				if(y!=current.getUserID())
				{	
		    		if(!FriendsList.contains(y))
		    		{
		    			FriendsList.add(y);
		    		}
				}		
				
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		return FriendsList;
	}
	
	public void DisplayFriends(profile current)
	{
		
		ArrayList<Integer> FriendsList = new ArrayList<Integer>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID1,userID2 FROM friends where userID1="+current.getUserID()+" or userID2="+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	final int x = resultSet.getInt(1);
		    	final int y = resultSet.getInt(2);
		    	
				if(x!=current.getUserID())
				{	
		    		if(!FriendsList.contains(x))
		    		{	
		    			FriendsList.add(x);
		    		}
				}
				if(y!=current.getUserID())
				{	
		    		if(!FriendsList.contains(y))
		    		{
		    			FriendsList.add(y);
		    		}
				}		
				
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		int count=0;
		while(count<FriendsList.size())
		{	
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "SELECT uname FROM profile where userID="+FriendsList.get(count)+" ";
				resultSet = statement.executeQuery(query);
				if(resultSet.next())
				{		
					System.out.println("User ID: "+FriendsList.get(count)+" Name: "+resultSet.getString(1));
				}
				count++;
			}	
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}	
		}	
		Scanner inScan= new Scanner(System.in);
		int choice;
		while(true)
		{
			try
			{
				System.out.println("Please select the User ID of the profile you want to view or 0 to go back to menu");
				choice=inScan.nextInt();
				if(FriendsList.contains(choice))
				{
					System.out.println();
					profile view = returnProfileByID(choice);
					System.out.println("Here is the profile you asked to view");
					System.out.println();
					System.out.println("UserID: "+view.getUserID());
					System.out.println("Name: "+view.getName());
					System.out.println("Email: "+view.getEmail());
					System.out.println("Date of Birth: "+view.getDate().toString());
					System.out.println("URL of picture: "+view.getPicURL());
					System.out.println("about me: "+view.getAboutMe());
					System.out.println("Last Login: "+view.getLastLogin().toString());
					System.out.println();
				}
				else if(choice==0)
				{
					break;
				}
				
			}
			catch(Exception e)
			{
				
			}
		}
		
		
		
		
		
		
	}
	
	public void SendMessage(profile userMessage)
	{
		Scanner inScan= new Scanner(System.in);
		String message;
		int msgID = retMsgID();
		int fromID= userMessage.getUserID();
		String GroupUser;
		Integer toUserID;
		Integer toGroupID=null;
		java.util.Date c_date = new java.util.Date();
		java.sql.Date date =new java.sql.Date(c_date.getTime());
		
		
		System.out.println("Please determine do you want to send to a \"group\" or a \"user\"");
		GroupUser = inScan.nextLine();
		while(!GroupUser.equals("group")&&!GroupUser.equals("user"))
		{
			System.out.println("Please determine do you want to send to a \"group\" or a \"user\"");
			GroupUser = inScan.nextLine();
		}
		if(GroupUser.equals("group"))
		{
			ArrayList<Integer> Groups=returnGroups(userMessage);
			
			while(true)
			{
				
				while(true)
				{	
					try
					{
						System.out.println("Please Enter the Group ID or 0 to go back to main menu");
						toGroupID=inScan.nextInt();
						if(Groups.contains(toGroupID))
						{	
							break;
						}
						else if(toGroupID==0)
						{
							break;
						}
						else
						{
							System.out.println("Please enter the Group ID of one of your groups");
						}
					}
					catch(Exception e)
					{
						inScan.nextLine();
						
					}
				}
				if(toGroupID==0)
				{
					break;
				}
				
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT * FROM Groups where gID ='"+toGroupID+"'";
					resultSet = statement.executeQuery(query);
					if(resultSet.next())
					{
						break;
					}
				
				}
				catch(Exception e)
				{
					
				}
			
			}
			if(toGroupID==0)
			{
				System.out.println("Press any key to return to main menu");
				inScan.nextLine();
			}
			else
			{	
				System.out.println("Please Enter your message");
				inScan.nextLine();
				message=inScan.nextLine();
				try {
						connection.setAutoCommit(false);
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						statement = connection.createStatement(); 
						query = "Insert into messages values(?,?,?,?,?,?)";
						PreparedStatement updateStatement = connection.prepareStatement(query);
						updateStatement.setInt(1, msgID);
						updateStatement.setInt(2, fromID);
						updateStatement.setString(3, message);
						updateStatement.setString(4, null);
						updateStatement.setInt(5, toGroupID);
						updateStatement.setTimestamp(6, new java.sql.Timestamp(c_date.getTime()));
						updateStatement.executeUpdate();
						connection.commit();
						System.out.println("Your message has been sent");
						System.out.println("Press any key to return to the main menu");
						String cont = inScan.nextLine();
				} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
			}	
		
		}
		else if(GroupUser.equals("user"))
		{
			ArrayList<Integer> FriendsList=returnFriendsList(userMessage);
			while(true)
			{	
				while(true)
				{	
					try
					{
						System.out.println("Please Enter the userID or 0 to go back to main menu");
						toUserID=inScan.nextInt();
						if(FriendsList.contains(toUserID))
						{	
							break;
						}
						else if(toUserID==0)
						{
							break;
						}
						else
						{
							System.out.println("Please enter the userID of one of your friends");
						}
					}
					catch(Exception e)
					{
						inScan.nextLine();
						
					}
				}
				if(toUserID==0)
				{
					break;
				}
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT * FROM profile where userID ='"+toUserID+"'";
					resultSet = statement.executeQuery(query);
					if(resultSet.next())
					{
						break;
					}
				
				}
				catch(Exception e)
				{
					
				}
			}
			if(toUserID==0)
			{
				System.out.println("Press any key to return to main menu");
				inScan.nextLine();
			}
			else
			{	
				System.out.println("Please Enter your message");
				inScan.nextLine();
				message=inScan.nextLine();
				try {
						connection.setAutoCommit(false);
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						statement = connection.createStatement(); 
						query = "Insert into messages values(?,?,?,?,?,?)";
						PreparedStatement updateStatement = connection.prepareStatement(query);
						updateStatement.setInt(1, msgID);
						updateStatement.setInt(2, fromID);
						updateStatement.setString(3, message);
						updateStatement.setInt(4, toUserID);
						updateStatement.setString(5, null);
						updateStatement.setTimestamp(6, new java.sql.Timestamp(c_date.getTime()));
						updateStatement.executeUpdate();
						connection.commit();
						System.out.println("Your message has been sent");
						System.out.println("Press any key to return to the main menu");
						String cont = inScan.nextLine();
					} 
				catch (SQLException e) 
				{
					e.printStackTrace();
				}
		}	
		}
		
		
	}
	
	public void PendingFriends(profile current)
	{
		
		
		ArrayList<Integer> PendingFriendsList = new ArrayList<Integer>();
		ArrayList<String> PendingFriendsMsgs = new ArrayList<String>();
		ArrayList<String> PendingFriendsNames = new ArrayList<String>();
		Scanner inScan = new Scanner(System.in);
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT FromID,message FROM PendingFriends where toID="+current.getUserID()+"";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	final int x = resultSet.getInt(1);
		    	String y = resultSet.getString(2);
		    	
		    	if(x!=current.getUserID())
		    	{	
		    		if(!PendingFriendsList.contains(x))
		    		{	
		    			PendingFriendsList.add(x);
		    			PendingFriendsMsgs.add(y);
		    		}
		    	}
		    	
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		if(PendingFriendsList.isEmpty())
		{
			System.out.println("You have no Friends requests");
			System.out.println("Press any to go back to menu");
			String cont = inScan.nextLine();
			
		}
		else
		{	
			System.out.println("Here is a list of your Friends requests");	
			int count=0;
			int choice;
			while(count<PendingFriendsList.size())
			{	
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT uname FROM profile where userID="+PendingFriendsList.get(count)+" ";
					resultSet = statement.executeQuery(query);
					if(resultSet.next())
					{		
						System.out.println("User ID: "+PendingFriendsList.get(count)+"\t Name: "+resultSet.getString(1));
						PendingFriendsNames.add(resultSet.getString(1));
					}
					count++;
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
			}
			
			
			
			while(true)	
			{	
				try
				{
					System.out.println("Please choose userID from the list to view the friend request");
					//System.out.println("Or Enter 0 to confirm all requests");
					choice = inScan.nextInt();
					if(PendingFriendsList.contains(choice))
					{
						break;
					}
					else if(choice==0)
					{
						break;	
					}
				}
				catch(Exception e)
				{
					
				}
			}
			System.out.println();
			System.out.println("Here is the information about this friend request");
			int c = PendingFriendsList.indexOf(choice);
			System.out.println("UserID "+PendingFriendsList.get(c));
			System.out.println("Name "+PendingFriendsNames.get(c));
			System.out.println("Messages "+PendingFriendsMsgs.get(c));
			System.out.println();
			System.out.println("Did you want to confirm this request or decline or do nothing");
			System.out.println("Please enter \"confirm\" or \"decline\" or \"nothing\"");
			inScan.nextLine();
			String confirm = inScan.nextLine();
			
			
			while(!confirm.equals("confirm") && !confirm.equals("decline") && !confirm.equals("nothing"))
			{
				System.out.println("Did you want to confirm this request or decline or do nothing");
				System.out.println("Please enter \"confirm\" or \"decline\" or \"nothing\"");
				confirm = inScan.nextLine();
			}
			if(confirm.equals("confirm"))
			{
				java.util.Date c_date = new java.util.Date();
				java.sql.Date date =new java.sql.Date(c_date.getTime());
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "Insert into friends values(?,?,?,?)";
					PreparedStatement updateStatement = connection.prepareStatement(query);
					updateStatement.setInt(1, PendingFriendsList.get(c));
					updateStatement.setInt(2, current.getUserID());
					updateStatement.setDate(3, date);
					updateStatement.setString(4, PendingFriendsMsgs.get(c));
					updateStatement.executeUpdate();
					connection.commit();
					System.out.println("You are now Friends");
					System.out.println();
					
					
					
				}
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "Delete FROM pendingFriends where FromID=? and ToID = ?";
					PreparedStatement updateStatement2 = connection.prepareStatement(query);
					updateStatement2.setInt(1,PendingFriendsList.get(c));
					updateStatement2.setInt(2,current.getUserID());
					updateStatement2.executeUpdate();
					connection.commit();
					System.out.println("Press any key to go back to main menu");
					String back = inScan.nextLine();
					
					
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
				
				
			}
			else if(confirm.equals("all"))
			{
				for(int i=0;i<PendingFriendsList.size();i++)
				{	
					java.util.Date c_date = new java.util.Date();
					java.sql.Date date =new java.sql.Date(c_date.getTime());
					try
					{
						connection.setAutoCommit(false);
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						statement = connection.createStatement(); 
						query = "Insert into friends values(?,?,?,?)";
						PreparedStatement updateStatement = connection.prepareStatement(query);
						updateStatement.setInt(1, PendingFriendsList.get(i));
						updateStatement.setInt(2, current.getUserID());
						updateStatement.setDate(3, date);
						updateStatement.setString(4, PendingFriendsMsgs.get(c));
						updateStatement.executeUpdate();
						connection.commit();
						System.out.println("You are now Friends");
						System.out.println();
						
						
						
					}
					catch (Exception Ex)
					{
						System.out.println("Machine Error: " + Ex.toString());
					}
					try
					{
						connection.setAutoCommit(false);
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						statement = connection.createStatement(); 
						query = "Delete FROM pendingFriends where FromID=? and ToID = ?";
						PreparedStatement updateStatement2 = connection.prepareStatement(query);
						updateStatement2.setInt(1,PendingFriendsList.get(c));
						updateStatement2.setInt(2,current.getUserID());
						updateStatement2.executeUpdate();
						connection.commit();
						
						
					}	
					catch (Exception Ex)
					{
						System.out.println("Machine Error: " + Ex.toString());
					}	
					
				}	
				
				
			}
			else if(confirm.equals("decline"))
			{
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "Delete FROM pendingFriends where FromID=? and ToID = ?";
					PreparedStatement updateStatement2 = connection.prepareStatement(query);
					updateStatement2.setInt(1,PendingFriendsList.get(c));
					updateStatement2.setInt(2,current.getUserID());
					updateStatement2.executeUpdate();
					connection.commit();
					System.out.println("You have declined this user");
					
					
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
				
			}
			else
			{
				//do nothing back to main menu
			}
			
		}	
		
	
	
	}
	
	public void DisplayAllMsgs(profile current)
	{
		ArrayList<Integer> MessageList = new ArrayList<Integer>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT M.msgID FROM (messages M join messageRecipient R on M.msgID = R.msgID) where R.userID="+current.getUserID()+"";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	MessageList.add(resultSet.getInt(1));
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		int count=0;
		while(count<MessageList.size())
		{	
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "SELECT fromID FROM Messages where msgID="+MessageList.get(count)+" ";
				resultSet = statement.executeQuery(query);
				if(resultSet.next())
				{		
					System.out.println("MsgID: "+MessageList.get(count)+"\t From: "+returnProfileByID(resultSet.getInt(1)).getName());
				}
				count++;
			}	
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}	
		}
		
		Scanner inScan = new Scanner(System.in);
		
		if(MessageList.isEmpty())
		{	
			System.out.println("You have no messages");
			System.out.println("Press any key to return to the main menu");
			String cont = inScan.nextLine();
		}
		
		else
		{
			System.out.println("Please Enter the number of the MsgID or 0 to go to main menu");
			int choice;
			while(true)
			{
				try
				{
					choice = inScan.nextInt();
					if(MessageList.contains(choice))
					{	
						break;
					}
					else if (choice==0)
					{
						break;
					}
				}
				
				catch(Exception e)
				{
					
				}
			}
			if(choice==0)
			{
				System.out.println("Press any key to return to the main menu");
				String cont = inScan.nextLine();
			}
			else
			{
				
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT * FROM Messages where msgID="+choice+" ";
					resultSet = statement.executeQuery(query);
					if(resultSet.next())
					{
						Messages msg = new Messages(resultSet.getInt(1),resultSet.getInt(2),resultSet.getString(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getDate(6));
						System.out.println();
						System.out.println("MsgID is "+msg.msgID);
						System.out.println("FromID: "+msg.fromID);
						String FromName = returnProfileByID(msg.fromID).getName();
						System.out.println("From: "+FromName);
						System.out.println("Date Sent: "+msg.m_date);
						System.out.println();
						System.out.println("Message: ");
						System.out.println("\t"+msg.message);
						System.out.println();
					}	
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
				
			}
		}
		
	}
	
	public void JoinGroup(profile current)
	{
		ArrayList<Integer> GroupsList = new ArrayList<Integer>();
		ArrayList<Integer> ListOfGroups = new ArrayList<Integer>();

		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    
		    query = "SELECT gID FROM groupMembership where userID = " + 
		    	current.getUserID() + "";
		    resultSet = statement.executeQuery(query);
		    
		    while(resultSet.next())
		    {
		    	GroupsList.add(resultSet.getInt(1));
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		if (GroupsList.size() < 10)
		{
			
			System.out.println("Here is a list of our groups");
			
			try 
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement();
				
				query = "SELECT * FROM groups";
				resultSet = statement.executeQuery(query);
				
				while(resultSet.next())
				{
					System.out.println("Group ID: " + resultSet.getInt(1));
					System.out.println("Group Name: " + resultSet.getString(2));
					System.out.println("Description: " + resultSet.getString(3));
					System.out.println();
					ListOfGroups.add(resultSet.getInt(1));
				}
				
				System.out.println();
			} 
			
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}
			
			System.out.println("Please Enter the Group ID of the group you want to join");
			Scanner inScan= new Scanner(System.in);
			int choice;
			
			while(true)
			{
				try
				{
					choice = inScan.nextInt();
					break;
				}
				
				catch(Exception e)
				{
					System.out.println("Please Enter the Group ID of the group you want "
						+ "to join");
					inScan.nextLine();
				}
			}
			
			
			while(GroupsList.contains(choice))
			{
				System.out.println("You are already in this group!!");
				System.out.println("Please Enter the groupID of the group you want to join or 0 to go back to menu");
				
				try
				{
					choice=inScan.nextInt();
					if(choice==0)
					{
						break;
					}
				}
				
				catch(Exception e)
				{
					System.out.println("Please Enter the Group ID of the group you want "
						+ "to join");
					inScan.nextLine();
				}	
			}
	
			while((!ListOfGroups.contains(choice)) && choice!= 0)
			{
				System.out.println("Please Enter the groupID of the group you want to join"
					+ " from the list or 0 to go back to main menu");
				
				try
				{
					choice=inScan.nextInt();
					if(choice==0)
					{
						break;
					}
				}
				
				catch(Exception e)
				{
					System.out.println("Please Enter the groupID of the group you want to "
						+ "join");
					inScan.nextLine();
				}	
				
			}
			
			
			String groupConfirm = inScan.nextLine();
			System.out.println();
			if(choice==0)
			{
				
			}
			else
			{
				System.out.println("Are you sure you want to join group " + choice + "?");
				System.out.println("Please enter \"yes\" or \"no\"");
				groupConfirm = inScan.nextLine();
			
				while(!groupConfirm.equals("yes") && !groupConfirm.equals("no"))
				{	
					System.out.println();
					System.out.println("Are you sure you want to join group " + choice + "?");
					System.out.println("Please enter \"yes\" or \"no\"");
					groupConfirm = inScan.nextLine();
				}
			
				if(groupConfirm.equals("yes"))
				{
					try
					{
						connection.setAutoCommit(false);
						connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
						statement = connection.createStatement(); 
					
						query = "Insert into groupMembership values(?,?)";
						PreparedStatement updateStatement = connection.prepareStatement(query);
						updateStatement.setInt(1, choice);
						updateStatement.setInt(2, current.getUserID());
						updateStatement.executeUpdate();
						connection.commit();
					
						System.out.println("Joined Group Successfully");
						System.out.println("Press any key to return to the main menu");
						String cont = inScan.nextLine();
					}
				
					catch (Exception Ex)
					{
						System.out.println("Machine Error: " + Ex.toString());	
					}	
				}
			
				else if(groupConfirm.equals("no"))
				{
					System.out.println("Press any key to return to the main menu");
					String cont = inScan.nextLine();
				}
			}
		}
		else if (GroupsList.size() >= 10)
		{
			Scanner inScan= new Scanner(System.in);
			System.out.println("You have reached the maximum number of groups you "
				+ "can join");
			System.out.println("Press any key to return to the main menu");
			String cont = inScan.nextLine();
		}
	}
	
	public void DropAccount(profile current)
	{
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement(); 
			query = "Delete FROM profile where userID = ?";
			PreparedStatement updateStatement2 = connection.prepareStatement(query);
			updateStatement2.setInt(1,current.getUserID());
			updateStatement2.executeUpdate();
			connection.commit();
			
			//query to get messages where the sender and receiver dropped their account
			/*query = "select R.msgID from (messageRecipient R join messages M on R.msgID"
				+ " = M.msgID) where (R.userID != (select userID from profile) and"
				" M.fromID != (select userID from profile))";*/
			
			Scanner inScan= new Scanner(System.in);
			System.out.println("Account deleted");
			System.out.println("Press any key to exit");
			String cont = inScan.nextLine();
		}	
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
	}
	
	public void FindUser(profile current)
	{
		ArrayList<Integer> UserList = new ArrayList<Integer>();
		String[] Patterns;
		Scanner inScan= new Scanner(System.in);
		String pattern;
		String cont;
		
		System.out.println("Please Enter your search string");
		pattern = inScan.nextLine();
		pattern = pattern.toLowerCase();
		Patterns = pattern.split("\\s+");
		
		for (int i = 0; i < Patterns.length; i++)
		{
			try 
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement();
				
				query = "SELECT userID FROM profile where REGEXP_LIKE (uname ,'" +Patterns[i]+"', 'i')" 
					+ " or REGEXP_LIKE (uname ,'" +Patterns[i]+"', 'i')";
					
				resultSet = statement.executeQuery(query);
				
				while(resultSet.next())
				{
					if (!UserList.contains(resultSet.getInt(1)))
					{
						UserList.add(resultSet.getInt(1));
					}
				}
			}    
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}
		}

		if(UserList.isEmpty())
		{	
			System.out.println();
			System.out.println("No users found matching your search criteria");
			System.out.println("Press any key to return to the main menu");
		}
		
		else
		{
			int count=0;
			System.out.println();
			System.out.println("Here's a list of matching users:");
			
			while(count < UserList.size())
			{	
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT userID, uname, email FROM profile where userID = "
						+ UserList.get(count) + " ";
					resultSet = statement.executeQuery(query);
					
					if(resultSet.next())
					{		
						System.out.println("userID: " + UserList.get(count));
						System.out.println("Name: " + 
							returnProfileByID(resultSet.getInt(1)).getName());
						System.out.println("Email: " + 
							returnProfileByID(resultSet.getInt(1)).getEmail());
						System.out.println();
					}
					
					count++;
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
			}
			
			System.out.println();
			System.out.println("Please enter the userID to view their profile or 0 to go to main menu");
			int choice;
			
			while(true)
			{
				try
				{
					choice = inScan.nextInt();
					
					if(UserList.contains(choice))
					{	
						break;
					}
					
					else if (choice == 0)
					{
						break;
					}
				}
				
				catch(Exception e)
				{
					
				}
			}
			
			if(choice == 0)
			{
				System.out.println("Press any key to return to the main menu");
			}
			
			else
			{
				System.out.println();
				profile view = returnProfileByID(choice);
				System.out.println("Here is the profile you selected:");
				System.out.println();
				System.out.println("UserID: "+view.getUserID());
				System.out.println("Name: "+view.getName());
				System.out.println("Email: "+view.getEmail());
				System.out.println("Date of Birth: "+view.getDate().toString());
				System.out.println("URL of picture: "+view.getPicURL());
				System.out.println("about me: "+view.getAboutMe());
				System.out.println("Last Login: "+view.getLastLogin().toString());
				System.out.println();	
				System.out.println("Press any key to return to the main menu");	
			}
		}
		
		cont = inScan.nextLine();
		cont = inScan.nextLine();
	}
	
	public void DisplayNewMsgs(profile current)

	{
		ArrayList<Integer> MessageList = new ArrayList<Integer>();
		
		try 
		{
			
			connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT M.msgID FROM (messages M join messageRecipient R on M.msgID = R.msgID) where R.userID = "+ current.getUserID() + " and CAST (M.dateSent AS TIMESTAMP) > TIMESTAMP' "+ lastLogin +"' ";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	MessageList.add(resultSet.getInt(1));
		    }
		}    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		int count=0;
		while(count<MessageList.size())
		{	
			try
			{
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				statement = connection.createStatement(); 
				query = "SELECT fromID FROM Messages where msgID="+MessageList.get(count)+" ";
				resultSet = statement.executeQuery(query);
				if(resultSet.next())
				{		
					System.out.println("MsgID: "+MessageList.get(count)+"\t From: "+returnProfileByID(resultSet.getInt(1)).getName());
				}
				count++;
			}	
			catch (Exception Ex)
			{
				System.out.println("Machine Error: " + Ex.toString());
			}	
		}
		
		Scanner inScan = new Scanner(System.in);
		
		if(MessageList.isEmpty())
		{	
			System.out.println("You have no new messages");
			System.out.println("Press any key to return to the main menu");
			String cont = inScan.nextLine();
		}
		
		else
		{
			System.out.println("Please Enter the number of the MsgID or 0 to go to main menu");
			int choice;
			while(true)
			{
				try
				{
					choice = inScan.nextInt();
					if(MessageList.contains(choice))
					{	
						break;
					}
					else if (choice==0)
					{
						break;
					}
				}
				
				catch(Exception e)
				{
					
				}
			}
			if(choice==0)
			{
				System.out.println("Press any key to return to the main menu");
				String cont = inScan.nextLine();
			}
			else
			{
				
				try
				{
					connection.setAutoCommit(false);
					connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
					statement = connection.createStatement(); 
					query = "SELECT * FROM Messages where msgID="+choice+" ";
					resultSet = statement.executeQuery(query);
					if(resultSet.next())
					{
						Messages msg = new Messages(resultSet.getInt(1),resultSet.getInt(2),resultSet.getString(3),resultSet.getInt(4),resultSet.getInt(5),resultSet.getDate(6));
						System.out.println();
						System.out.println("MsgID is "+msg.msgID);
						System.out.println("FromID: "+msg.fromID);
						String FromName = returnProfileByID(msg.fromID).getName();
						System.out.println("From: "+FromName);
						System.out.println("Date Sent: "+msg.m_date);
						System.out.println();
						System.out.println("Message: ");
						System.out.println("\t"+msg.message);
						System.out.println();
						System.out.println("Press any key to return to the main menu");
						inScan.nextLine();
						String cont = inScan.nextLine();
					}	
				}	
				catch (Exception Ex)
				{
					System.out.println("Machine Error: " + Ex.toString());
				}	
				
			}
		}
		
	}
	public void DegreesOfSepration(profile current)
	{
		System.out.println("Here is a list of our users");
		ArrayList<Integer> ListOfUsers = new ArrayList<Integer>();
		try 
		{
		    connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID,uname FROM profile";
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {
		    	System.out.println("User ID: "+resultSet.getInt(1)+" Name: "+resultSet.getString(2));
		    	ListOfUsers.add(resultSet.getInt(1));
		    }
		} 
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		System.out.println();
		Scanner inScan = new Scanner(System.in);
		boolean decesion = true;
		
		int choic=0;
		while(decesion)
		{
			System.out.println("Please Enter the userID of the user you want check degree of sepration");
			decesion = false;
			try
			{
				choic=inScan.nextInt();	
			}
			catch(Exception e)
			{
				decesion=true;
				continue;
			}
			if(!ListOfUsers.contains(choic))
			{
				System.out.println("Please Enter a userID from the list");
				decesion=true;
				continue;
			}
			
		}
		boolean x = true;
		boolean y=true;
		if(Friends(current).contains(choic))
		{
			System.out.println("You are Friends with user "+choic);
		}
		else
		{
			ArrayList<Integer> FriendsOfFriends1 = Friends(returnProfileByID(choic));
			for(int i=0; i<FriendsOfFriends1.size();i++)
			{	
				if(Friends(returnProfileByID(FriendsOfFriends1.get(i))).contains(current.getUserID()))
				{
					System.out.println("You are Friends with user "+FriendsOfFriends1.get(i)+" who is friends with user "+choic);
					x=false;
					y=false;
					break;
				}
			}
			if(x)
			{
				for(int i=0;i<FriendsOfFriends1.size();i++)
				{
					ArrayList<Integer> FriendsOfFriends2 = Friends(returnProfileByID(FriendsOfFriends1.get(i)));
					for(int j=0; j<FriendsOfFriends2.size(); j++)
					{
						if(Friends(returnProfileByID(FriendsOfFriends2.get(j))).contains(current.getUserID()))
						{
							System.out.println("Here is the route: ");
							System.out.println("You are friends with user "+FriendsOfFriends2.get(j));
							System.out.println("User "+FriendsOfFriends2.get(j)+" is Friends with user "+FriendsOfFriends1.get(i));
							System.out.println("User "+FriendsOfFriends1.get(i)+" is Friends with "+choic);
							x=false;
							y=false;
							break;
						}
					}
					
				}
			}
			if(y)
			{
				System.out.println("We could not find a way wit three hops between you and user "+choic);
			}
		}
		System.out.println();
		System.out.println("Press any key to return to main menu");
		String cont = inScan.nextLine();
	}
	public void MyStat(profile current)
	{
		Scanner inScan = new Scanner(System.in);
		ArrayList<Integer> Users = new ArrayList<Integer>();
		ArrayList<Integer> Numbers= new ArrayList<Integer>();
		
		int topk=0;
		while(true)
		{	
			try
			{
				System.out.println("Please Enter how many friends you want to show?");
				topk = inScan.nextInt();
				break;
			}
			catch(Exception e)
			{
				
			}
		}
		int months = 0;
		while(true)
		{	
			try
			{
				System.out.println("Please Enter the number of Months to show the Statistics");
				months= inScan.nextInt();
				break;
			}
			catch(Exception e)
			{
				
			}
		}	
		months = -1*months;
		int counter=0;
		
		try
		{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			statement = connection.createStatement();
			
			query = "select toUserID ,count(*) AS NUM from messages msgs where msgs.toUserID is not null and dateSent >= ADD_MONTHS(SYSDATE,"+ months+") group by toUserID Having NOT EXISTS (Select * from messages mmg where mmg.toUserID is not null and mmg.toUserID = msgs.toUserID group by mmg.toUserID HAVING count(mmg.toUserID) > Count(msgs.toUserID)) union select FromID ,count(*) AS NUM from messages msgs where msgs.FromID is not null and dateSent >= ADD_MONTHS(SYSDATE,"+months+") group by FromID Having NOT EXISTS (Select * from messages mmg where mmg.FromID is not null and mmg.FromID = msgs.FromID group by mmg.FromID HAVING count(mmg.FromID) > Count(msgs.FromID)) Order by NUM DESC";
			resultSet = statement.executeQuery(query);
			while(resultSet.next())
			{
				int x =resultSet.getInt(1);
				int y= resultSet.getInt(2);
				
				if(!Users.contains(x))
				{	
					Users.add(x);
					Numbers.add(y);
				}
				
			}
		}
		catch(Exception Ex)	
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		ArrayList<Integer> FriendsList = new ArrayList<Integer>();
		try 
		{
			connection.setAutoCommit(false);
		    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		    statement = connection.createStatement();
		    query = "SELECT userID1,userID2 FROM friends where userID1="+current.getUserID()+" or userID2="+current.getUserID();
		    resultSet = statement.executeQuery(query);
		    while(resultSet.next())
		    {	
				final int x = resultSet.getInt(1);
				final int y = resultSet.getInt(2);
				
				if(x!=current.getUserID())
				{	
					if(!FriendsList.contains(x))
					{	
						FriendsList.add(x);
					}
				}
				if(y!=current.getUserID())
				{	
					if(!FriendsList.contains(y))
					{
						FriendsList.add(y);
					}
				}		
		    }	
	    }    
		catch (Exception Ex)
		{
			System.out.println("Machine Error: " + Ex.toString());
		}
		
		System.out.println("Here is a list of your top " +topk+ " friends who sent or recieved messages");
		System.out.println("For the past "+-months+" months");
		System.out.println();
		int index=0;
		while(counter<topk)
		{
			if(index>= Users.size())
			{
				break;
			}
			else if(FriendsList.contains(Users.get(index)))
			{
				System.out.println("User: "+Users.get(index)+"\t Name: "+returnProfileByID(Users.get(index)).getName()+"\t Number of messages: "+Numbers.get(index));
				counter++;
			}
			index++;
			
		}
		System.out.println();
		System.out.println("Press any key to go back to main menu");
		inScan.nextLine();
		String conti = inScan.nextLine();
		/*
		 	all users who received most number of messages in order you define top using rownum <= topk
		 	within number of months
		 	/*
			query= "select toUserID ,count(*) AS NUM from messages msgs" 
				+"where msgs.toUserID is not null and dateSent >= ADD_MONTHS(SYSDATE,-3)" 
				+"group by toUserID Having NOT EXISTS (Select * from messages mmg" 
				+"where mmg.toUserID is not null and mmg.toUserID = msgs.toUserID"
				+"group by mmg.toUserID HAVING count(mmg.toUserID) > Count(msgs.toUserID))"
				+"union"
				+"select FromID ,count(*) AS NUM from messages msgs" 
				+"where msgs.FromID is not null and dateSent >= ADD_MONTHS(SYSDATE,-3)"
				+"group by FromID Having NOT EXISTS (Select * from messages mmg" 
				+"where mmg.FromID is not null and mmg.FromID = msgs.FromID"
				+"group by mmg.FromID HAVING count(mmg.FromID) > Count(msgs.FromID))"
				+"Order by NUM DESC";
			*/
		 	 
			
		
		
		
	}
	
	


}
