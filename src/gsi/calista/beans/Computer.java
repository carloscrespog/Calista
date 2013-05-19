package gsi.calista.beans;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;
import gsi.calista.actions.Calista;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;

public class Computer implements DeviceAbstract {

	private String name;
	private String ipAddress;
	private String macAddress;
	
	private String root;
	private String pwd;

	private int state;
	private int lastState;

	private final DeviceType type = DeviceType.COMPUTER;

	private static final String FIELD_NAME_STATE = "STATE";
	private static final String FIELD_NAME_LAST_STATE = "LAST_STATE";

	private static final String[] FIELD_NAMES = new String[] {
			FIELD_NAME_STATE, FIELD_NAME_LAST_STATE };

	private static final Byte[] datatypes = new Byte[] { DataTypes.INTEGER,
			DataTypes.INTEGER };

	private static DataField[] outputFormat = new DataField[] {
			new DataField(FIELD_NAME_STATE, "integer", "Computer state."),
			new DataField(FIELD_NAME_LAST_STATE, "integer",
					"Computer last state.") };

	public Computer(String name, String ipAddress, String macAddress) {
		this.name = name;
		this.ipAddress = ipAddress;
		this.macAddress = macAddress;
		this.state = 0;
		this.lastState = 0;
		
		Properties prop= new Properties();
		try {
			prop.load(new FileInputStream("conf/calista.properties"));
			
			this.root= prop.getProperty(name+"_root");
			this.pwd= prop.getProperty(name+"_root_pwd");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public String getName() {
		return name;
	}

	public StreamElement getData() {

		StreamElement streamElement = new StreamElement(FIELD_NAMES, datatypes,
				new Serializable[] { state, lastState },
				System.currentTimeMillis());

		return streamElement;
	}

	public DataField[] getOutputFormat() {
		return outputFormat;
	}

	public DeviceType getType() {
		return type;
	}

	public boolean updateData() {
		lastState = state;
		try {

			InetAddress address = InetAddress.getByName(ipAddress);
			try {
				boolean stateB = address.isReachable(2000);

				if (stateB)
					state = 1;
				else
					state = 0;
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public void turnOFF() {

		Calista.shutdownComputer(ipAddress, root, pwd);

	}

	public void turnON() {

		Calista.turnOnComputer(ipAddress, macAddress);

	}

}
