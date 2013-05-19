package gsi.calista.beans;

import gsn.beans.DataField;
import gsn.beans.StreamElement;

public interface DeviceAbstract {


	public String getName();

	public StreamElement getData();
	
	public DataField[] getOutputFormat();
	
	public boolean updateData();
}
