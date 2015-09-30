package com.mierzejewski.inzynierka.model;

/**
 * Created by dom on 11/11/14.
 */
public abstract class AbsDatabaseItem
{
    boolean isStoredInDb = false;
    boolean hasChanged = false;

    public boolean isHasChanged()
    {
        return hasChanged;
    }

    public void setChanged()
    {
        this.hasChanged = true;
    }

    public boolean isStoredInDb()
    {
        return isStoredInDb;
    }

    public void setStoredInDb(boolean isStoredInDb)
    {
        this.isStoredInDb = isStoredInDb;
    }

    public boolean isNeedToSaveChanges()
    {
        return hasChanged  || !isStoredInDb;
    }
}
