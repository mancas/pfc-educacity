package com.mancas.utils;

/**
 * Class that represent an item of the navigation drawer
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class DrawerItem
{
    /**
     * The layout to render for the item
     */
    private int layout;
    /**
     * The icon resource to render for the item
     */
    private int icon;
    /**
     * The label text of the item
     */
    private int text;

    /**
     * Constructor that initializes a new navigation drawer item
     * @param layout the layout to render
     * @param icon the icon resource
     * @param text the label text
     */
    public DrawerItem(int layout, int icon, int text)
    {
        this.layout = layout;
        this.icon = icon;
        this.text = text;
    }

    /**
     * Returns the layout of the navigation drawer item that will be rendered
     * by the application in the navigation drawer
     * @return the layout of the navigation drawer item
     */
    public int getLayout()
    {
        return layout;
    }

    /**
     * Set the new layout for the item specified in layout argument
     * @param layout the new layout for the item
     */
    public void setLayout(int layout)
    {
        this.layout = layout;
    }

    /**
     * Returns the icon resource associated to the navigation drawer item
     * @return the icon of the navigation drawer item
     */
    public int getIcon()
    {
        return icon;
    }

    /**
     * Set a new icon resource for the navigation drawer item
     * @param icon the new icon resource for the navigation drawer item
     */
    public void setIcon(int icon)
    {
        this.icon = icon;
    }

    /**
     * Returns the label text associated to the navigation drawer item
     * @return the current label text of the navigation drawer item
     */
    public int getText()
    {
        return text;
    }

    /**
     * Set a new label text for the navigation drawer item
     * @param text the new label text for the navigation drawer item
     */
    public void setText(int text)
    {
        this.text = text;
    }
}
