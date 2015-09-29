package pl.marcinmierzejewski.materialbudget.model;

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
