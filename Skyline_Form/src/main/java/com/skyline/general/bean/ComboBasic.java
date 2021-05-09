package com.skyline.general.bean;

public class ComboBasic 
{
    public String id = "";
    public String text = "";
    public String[] attributes = null;
    
    public ComboBasic()
    {
    }
    
    public ComboBasic(int size)
    {
        attributes = new String[(size > 0) ? size:1];
    }
}
