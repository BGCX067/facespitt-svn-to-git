
public class Messages 
{
	int msgID;
	int fromID;
	String message;
	int ToUserID;
	int ToGroupID;
	java.sql.Date m_date;
	
	public Messages(int ID,int FID,String MSG,int UID,int GID, java.sql.Date mdate)
	{
		msgID=ID;
		fromID=FID;
		message=MSG;
		ToUserID=UID;
		ToGroupID=GID;
		m_date=mdate;
	}
	

}
