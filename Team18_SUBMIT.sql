drop table profile cascade constraints;
drop table friends cascade constraints;
drop table pendingfriends cascade constraints;
drop table messages cascade constraints;
drop table messageRecipient cascade constraints;
drop table groups cascade constraints;
drop table groupMembership cascade constraints;
drop view GroupMessaging;
purge recyclebin;

create table profile
(
	userID number(10),
	uname varchar2(64),
	email varchar2(32),
	password varchar2(32),
	date_of_birth date,
	picture_URL varchar2(128),
	aboutme varchar2(1024),
	lastlogin date,
	constraint profile_pk primary key(userID),
	constraint profile_ak unique(email)
);

create table friends
(
	userID1 number(10),
	userID2 number(10),
	JDate date,
	message varchar2(1024),
	constraint friends_pk primary key(userID1, userID2),
	constraint friends_fk1 foreign key(userID1) references profile(userID),
	constraint friends_fk2 foreign key(userID2) references profile(userID)
);

create table pendingfriends
(
	fromID number(10),
	toID number(10),
	message varchar2(1024),
	constraint pendingfriends_pk primary key(fromID, toID),
	constraint pendingfriends_fk1 foreign key(fromID) references profile(userID),
	constraint pendingfriends_fk2 foreign key(toID) references profile(userID)
);

create table groups
(
	gID number(10),
	gname varchar2(64),
	description varchar2(128),
	constraint groups_pk primary key(gID),
	constraint groups_ak unique(gname)
);

create table messages
(
	msgID number(10),
	fromID number(10),
	message varchar2(1024),
	ToUserID number(10) default NULL,
	ToGroupID number(10) default NULL,
	m_date date,
	constraint messages_pk primary key(msgID),
	constraint messages_fk1 foreign key(fromID) references profile(userID),
	constraint messages_fk2 foreign key(ToUserID) references profile(userID),
	constraint messages_fk3 foreign key(ToGroupID) references groups(gID)
);

create table messageRecipient
(
	msgID number(10),
	userID number(10),
	constraint messageRecipient_pk primary key(msgID, userID),
	constraint messageRecipient_fk1 foreign key(msgID) references messages(msgID),
	constraint messageRecipient_fk2 foreign key(userID) references profile(userID)
);



create table groupMembership
(
	gID number(10),
	userID number(10),
	constraint groupMembership_pk primary key(gID, userID),
	constraint groupMembership_fk1 foreign key(gID) references groups(gID),
	constraint groupMembership_fk2 foreign key(userID) references profile(userID)
);

create view GroupMessaging as 
		select messages.msgID as msgID , groupMembership.userID as userID
  		from messages,groupMembership
 		where messages.ToGroupID = groupMembership.gID
 		order by msgID;

create or replace trigger SendMessage
after insert on messages
FOR EACH ROW
when (new.ToGroupID is NULL)
BEGIN
	insert into messageRecipient values(:new.msgID, :new.ToUserID);
END;
/



create or replace trigger SendGroupMessage
after insert on groupMembership
FOR EACH ROW
BEGIN
	insert into messageRecipient (msgID,userID)
	select distinct messages.msgID,profile.userID
	from messages, profile
	where messages.TouserID is null and :new.gID=messages.ToGroupID and :new.userID=profile.userID;		
END;
/

create or replace trigger DropAccount
after delete on profile
FOR EACH ROW
BEGIN
	delete from groupMembership where userID = :old.userID;
END;
/




Insert into profile values (1, 'Shenoda', 'shg@pitt.edu', 'shpwd', '13-OCT-1977', '/afs/pitt.edu/home/s/g/shg18/public/photo.jpg', 'CS 1555 TA', '11-NOV-2012');
Insert into profile values (2, 'Lory', 'lra@pitt.edu', 'lpwd', '08-MAR-1986', NULL, 'Member of ADMT Lab', '10-NOV-2012');
Insert into profile values (3, 'Peter', 'pdj@pitt.edu', 'ppwd', '09-JAN-1984', 'http://www.cs.pitt.edu/~peter', 'Graduate Student in CS dept.', '10-NOV-2012');
Insert into profile values (4,'Alexandrie', 'alx@pitt.edu','apwd','21-AUG-1975', NULL, 'Architecture researcher','11-NOV-2012');
Insert into profile values(5,'Panickos','pnk@pitt.edu','kpwd','08-SEP-1989',NULL,'ADMT Lab researcher','08-NOV-2012');
Insert into profile values(6,'Socratis', 'soc@pitt.edu', 'spwd', '17-MAY-1981', NULL, 'TA in CS dept','09-NOV-2012');
Insert into profile values(7,'Yaw', 'yaw@pitt.edu', 'ypwd', '27-FEB-1987', NULL, 'Staff at CS dept','07-NOV-2012');


Insert into friends values(1,2, '06-JAN-2008', 'Hey, it is me  Shenoda!' );
Insert into friends values (1,5, '15-JAN-2011', 'Hey, it is me  Shenoda!');
Insert into friends values (2,3,'23-AUG-2007', 'Hey, it is me  Lory!');
Insert into friends values (2,4,'17-FEB-2008', 'Hey, it is me  Lory!');
Insert into friends values (3,4,'16-SEP-2010', 'Hey, it is me  Peter!');
Insert into friends values (4,6,'06-OCT-2010', 'Hey, it is me  Alexandrie!');
Insert into friends values (6,7,'13-SEP-2012', 'Hey, it is me  Socratis!');


Insert into pendingfriends values (7,4,'Hey, it is me Yaw');
Insert into pendingfriends values (5,2,'Hey, it is me Panickos');
Insert into pendingfriends values (2,6,'Hey, it is me Lory');

Insert into Groups values (1, 'Grads at CS', 'list of all graduate students');
Insert into Groups values (2, 'DB Group', 'member of the ADMT Lab.');




Insert into messages values (1, 1, 'are we meeting tomorrow for the project?', 2, NULL, '09-NOV-2012');
Insert into messages values(2, 1, 'Peter''s pub tomorrow?', 5, NULL, '07-NOV-2012');
Insert into messages values(3, 2, 'Please join our DB Group forum tomorrow', NULL, 1, '06-NOV-2012');
Insert into messages values(4, 5, 'Here is the paper I will present tomorrow', NULL, 2, '04-NOV-2012');

--------------------Insert into messageRecipient values(3,1);--------------------
--------------------Insert into messageRecipient values(3,2);--------------------
--------------------Insert into messageRecipient values(3,3);--------------------
--------------------Insert into messageRecipient values(3,4);--------------------
--------------------Insert into messageRecipient values(3,5);--------------------
--------------------Insert into messageRecipient values(3,6);--------------------
--------------------Insert into messageRecipient values(3,7);--------------------
--------------------Insert into messageRecipient values(4,1);--------------------
--------------------Insert into messageRecipient values(4,2);--------------------
--------------------Insert into messageRecipient values(4,5);--------------------

Insert into groupMembership values(1,1);
Insert into groupMembership values(1,2);
Insert into groupMembership values(1,3);
Insert into groupMembership values(1,4);
Insert into groupMembership values(1,5);
Insert into groupMembership values(1,6);
Insert into groupMembership values(1,7);
Insert into groupMembership values(2,1);
Insert into groupMembership values(2,2);
Insert into groupMembership values(2,5);

