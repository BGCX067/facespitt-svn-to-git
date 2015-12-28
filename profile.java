import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.List;


public class profile 

{
	private int userID;
	private String name;
	private String email;
	private String password;
	private java.sql.Date dateOfBirth;
	private String picURL;
	private String aboutME;
	private java.sql.Date lastLogin;
	
	
	public profile(int uID, String Name, String eAddress, String pword, java.sql.Date Birth, String pic,String abMe, java.sql.Date lLogin)
	{
		userID=uID;
		name=Name;
		email=eAddress;
		password=pword;
		dateOfBirth=Birth;
		picURL=pic;
		aboutME=abMe;
		lastLogin=lLogin;
	}
	
	public int getUserID()
	{
		return this.userID;
	}
	public String getName()
	{
		return this.name;
	}
	public String getPassword()
	{
		return this.password;
	}
	public String getEmail()
	{
		return this.email;
	}
	public java.sql.Date getDate()
	{
		return this.dateOfBirth;
	}
	public String getPicURL()
	{
		return this.picURL;
	}
	public String getAboutMe()
	{
		return this.aboutME;
	}
	public java.sql.Date getLastLogin()
	{
		return this.lastLogin;
	} 
}
