
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacesPitt 
{
	
	private static final String EPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+"pitt*(\\.edu)$";
	
	public static void main(String args[])
	{
		FacesDB DBaccess = new FacesDB();
		System.out.println("Hi, Please enter \"Login\" to login or \"Register\" to register");
		Scanner inScan = new Scanner(System.in);
		String LoginOrReg = inScan.nextLine();
		while(!LoginOrReg.equals("Login") && !LoginOrReg.equals("Register"))
		{
			System.out.println("Hi, Please enter \"Login\" to login or \"Register\" to register");
			LoginOrReg = inScan.nextLine();
		}
		if(LoginOrReg.equals("Login"))
		{
			System.out.println("Please enter your email");
			String email= inScan.nextLine();
			System.out.println("Please enter your password");
			String pword = inScan.nextLine();
			if(DBaccess.Login(email, pword))
			{
				System.out.println("You have successfully logged in");
				profile current = DBaccess.returnProfile(email);
				boolean Logout = true;
				while(Logout)
				{	
					
					System.out.println();
					System.out.println("Hey, "+current.getName()+" Welcome to Faces@pitt");
					System.out.println();
					System.out.println("Here is the menu of Faces@pitt");
					System.out.println("0-Logout");
					System.out.println("1-Login ");
					System.out.println("2-Register ");
					System.out.println("3-Send a message ");
					System.out.println("4-Add a friend ");
					System.out.println("5-Display All Messages ");
					System.out.println("6-Display New Messages ");
					System.out.println("7-Display friends ");
					System.out.println("8-Find user ");
					System.out.println("9-Confirm Friends Requests ");
					System.out.println("10-Three Degrees of Sepration");
					System.out.println("11-Join a group ");
					System.out.println("12-My Statistics");
					System.out.println("13-Drop account");
					System.out.println();
					int choice=0;
					boolean exp = true;
					while(exp)
					{	
						System.out.println("Please select one of numbers:");
						try
						{
							choice = inScan.nextInt();
							exp=false;
						}
						catch(Exception e)
						{
							System.out.println("Please enter a number from the menu");
							exp=true;
							inScan.nextLine();
						}
					}
					if(choice==0)
					{
						System.out.println("Thank you! See you soon");
						Logout=false;
					}
					if(choice==1)
					{
						System.out.println("You are already logged in");
					}
					if(choice==2)
					{
						System.out.println("You choose the register option when you run the program");
					}
					
					if(choice==3)
					{
						DBaccess.SendMessage(current);
					}
					if(choice==4)
					{
						System.out.println();
						DBaccess.AddFriend(current);
						System.out.println();
					}
					if(choice==5)
					{
						System.out.println();
						DBaccess.DisplayAllMsgs(current);
						System.out.println();
			
					}
					if(choice==6)
					{
						System.out.println();
						DBaccess.DisplayNewMsgs(current);
						System.out.println();
					}
					if(choice==7)
					{
						System.out.println();
						DBaccess.DisplayFriends(current);
						System.out.println();
					}
					
					if(choice == 8)
					{
						System.out.println();
						DBaccess.FindUser(current);
						System.out.println();
					}
					
					if (choice == 9)
					{
						DBaccess.PendingFriends(current);
					}
					
					if(choice == 10)
					{
						DBaccess.DegreesOfSepration(current);
					}
					
					if(choice == 11)
					{
						System.out.println();
						DBaccess.JoinGroup(current);
						System.out.println();
					}
					
					if(choice == 12)
					{
						DBaccess.MyStat(current);
					}
					
					if(choice == 13)
					{
						System.out.println();
						DBaccess.DropAccount(current);
						System.out.println();
						System.exit(0);
						Logout = false;
					}
				}
				
			}
			else
			{
				System.out.println("Please try again");
				main(args);
			}
		}
		else if(LoginOrReg.equals("Register"))
		{
			System.out.println("Please Enter your Name");
			String name=inScan.nextLine();
			Pattern patt = Pattern.compile(EPattern);
			Matcher match;
			System.out.println("Please Enter your Pitt email");
			String email=inScan.nextLine();
			match = patt.matcher(email);
			
			while(DBaccess.returnList().contains(email) || !match.matches())
			{
				if(!match.matches())
				{
					System.out.println("this is NOT a valid Pitt email");
				}	
				else if(DBaccess.returnList().contains(email))
				{	
					System.out.println("this is email is already registered");
				}
				
				System.out.println("Please Enter your Pitt email");
				email=inScan.nextLine();
				match = patt.matcher(email);
			}
			
			System.out.println("Please Enter your password");
			String password=inScan.nextLine();
			System.out.println("Please confirm your password");
			String password2 = inScan.nextLine();
			while(!password.equals(password2))
			{
				System.out.println("The passwords didn't match");
				System.out.println("Please Enter your password");
				password=inScan.nextLine();
				System.out.println("Please confirm your password");
				password2 = inScan.nextLine();
			}
			System.out.println("Please Enter your date of birth yyyy-mm-dd");
			java.sql.Date DOB=null;
			while(true)
			{	
				String dateOfbirth = inScan.nextLine();
				
				try 
				{
					DOB=java.sql.Date.valueOf(dateOfbirth);
					break;
				} 
				catch (Exception e) 
				{
					System.out.println("Please enter the date as the format shown");	
				}
			}
			System.out.println("Please Enter a URL to picture");
			String picURL = inScan.nextLine();
			System.out.println("Please Enter a short self description");
			String description = inScan.nextLine();
			java.sql.Date DL = null;
			profile newUser;
			int userID = DBaccess.returnNewUserID();
			newUser= new profile(userID,name,email,password,DOB,picURL,description,DL);
			DBaccess.Register(newUser);
			main(args);
		
		}
		
		
	}

}
