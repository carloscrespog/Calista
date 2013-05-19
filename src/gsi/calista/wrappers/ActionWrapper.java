package gsi.calista.wrappers;

import gsi.calista.actions.Calista;
import gsi.calista.actions.RaspberryPI;
import gsi.calista.beans.Actuator;
import gsi.calista.beans.Computer;
import gsi.calista.beans.Door;
import gsn.beans.AddressBean;
import gsn.beans.DataField;
import gsn.beans.DataTypes;
import gsn.beans.StreamElement;
import gsn.wrappers.AbstractWrapper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class ActionWrapper extends AbstractWrapper {

	private final transient Logger logger = Logger
			.getLogger(ActionWrapper.class);

	private static int threadCounter = 0;

	private ServerSocket serverSocket;

	private static final String FIELD_NAME_ACTION = "ACTION";
	private static final String FIELD_ACTION_RESPONSE = "RESPONSE";

	private static final String[] OUTPUT_FIELD_NAMES = new String[] {
			FIELD_NAME_ACTION, FIELD_ACTION_RESPONSE };

	private static final Byte[] OUTPUT_FIELD_TYPES = new Byte[] {
			DataTypes.VARCHAR, DataTypes.VARCHAR };

	private static final transient DataField[] cachedOutputStructure = new DataField[] {
			new DataField(FIELD_NAME_ACTION, "VARCHAR(100)",
					"status:added or removed"),
			new DataField(FIELD_ACTION_RESPONSE, "VARCHAR(100)",
					"Virtual Sensor Filename") };

	private static final String[] FIELD_NAMES = new String[] {
			FIELD_NAME_ACTION, FIELD_ACTION_RESPONSE };

	private AddressBean paramsbeans;

	private static int PORT = 8000;
	private String element;
	private String value;
	private String type;
	private List<NameValuePair> params;
	private String filesPath = "files/";

	public boolean initialize() {
		setName("Action listener " + (++threadCounter));
		paramsbeans = getActiveAddressBean();

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("conf/calista.properties"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (properties.getProperty("actionwrapper-port") != null) {
			PORT = Integer.parseInt(properties
					.getProperty("actionwrapper-port"));

		} else {
			return false;
		}

		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.err.println("Could not listen on port: " + PORT);
			System.exit(1);
		}

		return true;
	}

	public void run() {

		threadCounter++;

		Socket clientSocket = null;

		while (isActive()) {

			try {

				clientSocket = serverSocket.accept();

				BufferedReader inFromClient;

				inFromClient = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));

				String requestString = inFromClient.readLine();
				System.out.println(requestString);
				String headerLine = requestString;

				StringTokenizer tokenizer = new StringTokenizer(headerLine);
				String httpMethod = tokenizer.nextToken();
				String httpQueryString = tokenizer.nextToken();
				String path = requestString.split("\\?")[0];

				PrintWriter out = new PrintWriter(
						clientSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						clientSocket.getInputStream()));
				String outputLine = "Fatal error"; // inputLine;
				System.out.println("The Client "
						+ clientSocket.getInetAddress() + ":"
						+ clientSocket.getPort() + " is connected");

				// parsing the uri
				String httpResult = "HTTP/1.1 200 OK\r\n";
				JSONObject json = new JSONObject();

				if (path.equals("GET /calista/action")) {
					params = URLEncodedUtils.parse(new URI(httpQueryString),
							"UTF-8");
					element = getParamValue("element");
					type = getParamValue("type");
					value = getParamValue("value");

					if (type != null) {
						if (type.equals("led") && value != null) {
							int val = Integer.parseInt(value);
							RaspberryPI raspi = new RaspberryPI();
							switch (val) {
							case 1:

								raspi.setDigitalValue(RaspiPin.GPIO_01,
										PinState.HIGH);
								raspi.shutdownPinController();
								outputLine = "Led on";
								break;
							case 0:

								raspi.setDigitalValue(RaspiPin.GPIO_01,
										PinState.LOW);
								raspi.shutdownPinController();
								outputLine = "Led off";

								break;

							default:
								outputLine = "Posible led values: 0,1.";
								break;
							}

						}

						else if (type.equals("actuator") && value != null) {
							int val = Integer.parseInt(value);

							Actuator actuator = (Actuator) RaspberryPIWrapper
									.findDeviceById(element);
							if (actuator != null) {
								if (val == 1 || val == 0) {

									actuator.setDigitalValue(val);
									outputLine = "Actuator state changed to :"
											+ value;

								} else {
									outputLine = "Value not allowed for this actuator: "
											+ value;
								}
							} else {
								outputLine = "Actuator '" + element
										+ "' not found";
							}

						} else if (type.equals("door") && value != null) {
							int val = Integer.parseInt(value);

							Door door = (Door) RaspberryPIWrapper
									.findDeviceById(element);

							if (door != null) {

								if (val == 1) {

									outputLine = "Door state changed to :"
											+ value;

								} else {
									outputLine = "Value not allowed for this door: "
											+ value;
								}

							} else {
								outputLine = "Computer '" + element
										+ "' not found";
							}

						} else if (type.equals("computer") && value != null) {

							int val = Integer.parseInt(value);
							Computer computer = (Computer) RaspberryPIWrapper
									.findDeviceById(element);
							if (computer != null) {

								if (val == 1) {
									computer.turnON();

									outputLine = "Computer turned on ";

								} else if (val == 0) {
									computer.turnOFF();
									outputLine = "Computer turned off ";

								}

							} else {

								outputLine = "Door '" + element + "' not found";
							}

						} else if (type.equals("action")) {
							outputLine = executeAction(element);

						} else {
							outputLine = "Query error--> type " + type
									+ " is not supported";

						}

					} else {

						outputLine = "Query error--> the query must have this params: element, type, value. Element="
								+ element + " Type=" + type + " Value=" + value;

					}

				} else {

					outputLine = "Path error--> the path must be 'GET /calista/action' instead '"
							+ path + "'";

					httpResult = "HTTP/1.1 404 Not Found\r\n";

				}

				json.put("result", outputLine);

				outputLine = json.toString();
				System.out.println(outputLine);
				out.write(httpResult); // Return status code for OK (200)

				out.write("Content-Type:json \r\n\r\n\r\n");

				out.print(outputLine);

				// Shutting down connection
				out.close();

				in.close();
				clientSocket.close();

				StreamElement streamElement = new StreamElement(
						OUTPUT_FIELD_NAMES, OUTPUT_FIELD_TYPES,
						new Serializable[] { path, outputLine },
						System.currentTimeMillis());

				postStreamElement(streamElement);
			} catch (Exception e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}

		}

		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getParamValue(String name) {
		String value = null;
		for (NameValuePair param : params) {
			if (param.getName().equals(name))
				value = param.getValue();
		}
		return value;
	}

	public void dispose() {
		threadCounter--;
	}

	/**
	 * The output fields exported by this virtual sensor.
	 * 
	 * @return The strutcture of the output.
	 */

	public final DataField[] getOutputFormat() {
		return cachedOutputStructure;
	}

	public String getWrapperName() {
		return "System memory consumption usage";
	}

	private String executeAction(String element) {
		if (element.equals("play")) {
			Calista.play(filesPath + value);
			return "playing file" + value;
		}
		if (element.equals("tweet")) {
			Calista.sendTweet(value);
			return "tweet sent" + value;
		}
		if (element.equals("email")) {
			String from = getParamValue("from");
			String to = getParamValue("to");
			String title = getParamValue("title");
			if (from != null && to != null && title != null) {
				Calista.sendEmail(from, to, title, value);
				return "email sent";
			} else {
				return "Query error--> email not params enough";

			}

		} else {
			return "Query error--> action " + element + " is not supported";

		}

	}
}
