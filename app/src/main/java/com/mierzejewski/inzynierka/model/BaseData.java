package com.mierzejewski.inzynierka.model;

public class BaseData
{
	protected final MainData mainData;
	
	public BaseData(MainData mainData)
	{
		this.mainData =  mainData;
	}

    public MainData getMainData()
    {
        return mainData;
    }


}
