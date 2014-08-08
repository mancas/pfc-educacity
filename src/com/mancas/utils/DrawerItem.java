package com.mancas.utils;

public class DrawerItem
{
    private int layout;
    private int icon;
    private int text;
    
    public DrawerItem(int layout, int icon, int text)
    {
        this.layout = layout;
        this.icon = icon;
        this.text = text;
    }

    public int getLayout()
    {
        return layout;
    }

    public void setLayout(int layout)
    {
        this.layout = layout;
    }

    public int getIcon()
    {
        return icon;
    }

    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    public int getText()
    {
        return text;
    }

    public void setText(int text)
    {
        this.text = text;
    }
}
