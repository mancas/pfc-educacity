package com.mancas.synchronize;

/**
 * Class to manage user data synchronized with the REST server
 * @author Manuel Casas Barrado
 * @version 1.0
 */
public class Synchronize
{
    /**
     * The current instance of this class because this class is a singleton
     */
    private static volatile Synchronize instance = null;

    public static synchronized Synchronize getInstance()
    {
        if (instance == null) {
            instance = new Synchronize();
        }

        return instance;
    }

    private Synchronize()
    {
    }
    
    public static void uploadImages()
    {
        
    }

    public static void uploadProfile()
    {
        
    }
}
