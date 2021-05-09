package com.skyline.notification.bean;

public class Notification
{
    public int NOTIFICATION_ID = 0;
    public int notificationRowOrder = 0;
    public String MODULE_NAME = "";
    public int MODULE_ID = 0;
    public int MESSAGE_TYPE_ID = 0;
    public String DESCRIPTION = "";
    public int IS_ACTIVE = 1;
    public int RESEND = 0;
    public int INCLUDE_ATTACHMENT = 0;
    public String EMAIL_SUBJECT = "";
    public int SCHEDULER_ID = 0;
    public int TRIGGER_TYPE_ID = 0;
    public int ON_SAVE_ID = 0;
    public String SCHEDULER_NAME = "";
    public int SCHEDULER_INTERVAL = 0;
    public String EMAIL_BODY = "";
    public int COLUMN_NUMBER = -1;
    public int COPY_TO_PRODUCTION = 0;
    
    public Notification()
    {     
    }
}

