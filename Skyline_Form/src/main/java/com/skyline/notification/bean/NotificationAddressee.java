package com.skyline.notification.bean;

public class NotificationAddressee 
{
	public int ADDRESSEE_ID = 0;
    public int USER_ID = 0;
    public String USER_NAME = "";
    public String SEND_TYPE = "";
    public int ADDRESS_TYPE_ID = 0;
    public String ADDRESS_TYPE_NAME = "";
    public int IS_MESSAGE_ONLY = 0; //kd 28082017 added isMessageOnly to tab DistributionList
    
    public NotificationAddressee()
    {
    }
}
