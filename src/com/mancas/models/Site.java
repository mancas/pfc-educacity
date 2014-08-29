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

    public Site()
    {
    }

    public Site(Integer id, Double lat, Double lng, String title, String information)
    {
        mId = id;
        mLatitude = lat;
        mLongitude = lng;
        mTitle = title;
        mInformation = information;
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
     */
    public void setId(Integer id)
    {
        mId = id;
    }

    /**
     * Gets the latitude coordinate of this site
     */
    public void setLatitude(Double latitude)
    {
        mLatitude = latitude;
    }

    /**
     * Gets the longitude coordinate of this site
     */
    public void setLongitude(Double longitude)
    {
        mLongitude = longitude;
    }

    /**
     * Gets the title of this site
     */
    public void setTitle(String title)
    {
        mTitle = title;
    }

    /**
     * Gets the information about this site
     */
    public void setInformation(String information)
    {
        mInformation = information;
    }
}