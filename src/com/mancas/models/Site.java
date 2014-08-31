package com.mancas.models;

/**
 * This class represent a POI (Point of interest) that will be drawed in the map
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class Site
{
    private Integer mId;
    private Double mLatitude;
    private Double mLongitude;
    private String mTitle;
    private String mInformation;
    private String mImage;
    private boolean mError = false;

    public Site()
    {
    }

    public Site(Integer id, Double lat, Double lng, String title, String information, String image)
    {
        mId = id;
        mLatitude = lat;
        mLongitude = lng;
        mTitle = title;
        mInformation = information;
        mImage = image;
    }

    /**
     * Gets the ID of this site
     * @return the id that identify this site
     */
    public Integer getId()
    {
        return mId;
    }

    /**
     * Gets the latitude coordinate of this site
     * @return the latitude coordinate of this site
     */
    public Double getLatitude()
    {
        return mLatitude;
    }

    /**
     * Gets the longitude coordinate of this site
     * @return the longitude coordinate of this site
     */
    public Double getLongitude()
    {
        return mLongitude;
    }

    /**
     * Gets the title of this site
     * @return the title of this site
     */
    public String getTitle()
    {
        return mTitle;
    }

    /**
     * Gets the information about this site
     * @return the information about this site
     */
    public String getInformation()
    {
        return mInformation;
    }

    /**
     * Sets the ID of this site
     * @param id ID of this site
     */
    public void setId(Integer id)
    {
        mId = id;
    }

    /**
     * Sets the latitude coordinate of this site
     * @param latitude latitude coordinate
     */
    public void setLatitude(Double latitude)
    {
        mLatitude = latitude;
    }

    /**
     * Sets the longitude coordinate of this site
     * @param longitude longitude coordinate
     */
    public void setLongitude(Double longitude)
    {
        mLongitude = longitude;
    }

    /**
     * Sets the title of this site
     * @param title title of this site
     */
    public void setTitle(String title)
    {
        mTitle = title;
    }

    /**
     * Sets the information about this site
     * @param information information of this site
     */
    public void setInformation(String information)
    {
        mInformation = information;
    }

    /**
     * Sets the image of this site
     * @param image image of this site
     */
    public void setImage(String image)
    {
        mImage = image;
    }

    /**
     * Gets the image about this site
     * @returns the image of this site
     */
    public String getImage()
    {
        return mImage;
    }

    /**
     * Sets if this model has errors
     * @param error
     */
    public void setError(boolean error)
    {
        mError = error;
    }

    /**
     * Check if the site has errors
     * @return true if has errors, or false if not
     */
    public boolean hasErrors()
    {
        return mError;
    }
}