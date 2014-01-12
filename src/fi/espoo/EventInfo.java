package fi.espoo;

import com.google.android.gms.maps.model.LatLng;

public class EventInfo {

	private LatLng position;
	private String title;
	private String details;

	public EventInfo(LatLng aPosition, String aInfo, String aDetails)
	{
		position = aPosition;
		title = aInfo;
		details = aDetails;
	}
	
	public EventInfo()
	{
		
	}
	
	public LatLng getPosition()
	{
		return position;
	}
	
	public String getInfo()
	{
		return title;	
	}
	
	public String getDate()
	{		
		return details;
	}
}
