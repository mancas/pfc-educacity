package com.mancas.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mancas.models.Site;

import android.util.Log;

public class ParseJSONSites {
    public static final String SITE_ID_TAG = "id";
    public static final String SITE_TITLE_TAG = "title";
    public static final String SITE_LATITUDE_TAG = "latitude";
    public static final String SITE_LONGITUDE_TAG = "longitude";
    public static final String SITE_INFORMATION_TAG = "information";

    /**
     * Checks the response from REST after getting site from server request
     * @param register the response from server
     * @return a new {@link Site}
     */
    public static Site parseSingleSiteResponse(String response)
    {
        Site site = new Site();
        try {
            JSONObject json = new JSONObject(response);
            site.setId(json.getInt(SITE_ID_TAG));
            site.setTitle(json.getString(SITE_TITLE_TAG));
            site.setLatitude(json.getDouble(SITE_LATITUDE_TAG));
            site.setLongitude(json.getDouble(SITE_LONGITUDE_TAG));
            site.setInformation(json.getString(SITE_INFORMATION_TAG));
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
            return site;
        }
        return site;
    }

    /**
     * Checks the response from REST after getting sites from server request
     * @param register the response from server
     * @return a list of {@link Site}
     */
    public static List<Site> parseMultipleSitesResponse(String response)
    {
        List<Site> sites = new ArrayList<Site>();
        try {
            JSONArray json = new JSONArray(response);
            for (int i = 0; i < json.length(); i++) {
                JSONObject siteObj = json.getJSONObject(i);
                Site site = new Site();
                site.setId(siteObj.getInt(SITE_ID_TAG));
                site.setTitle(siteObj.getString(SITE_TITLE_TAG));
                site.setLatitude(siteObj.getDouble(SITE_LATITUDE_TAG));
                site.setLongitude(siteObj.getDouble(SITE_LONGITUDE_TAG));
                site.setInformation(siteObj.getString(SITE_INFORMATION_TAG));
                sites.add(site);
            }
        } catch (JSONException e) {
            Log.d("JSON", e.getMessage());
            return sites;
        }
        return sites;
    }
}
