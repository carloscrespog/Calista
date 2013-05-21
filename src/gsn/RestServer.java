package gsn;

import gsi.calista.actions.Calista;
import gsi.calista.beans.Actuator;
import gsi.calista.beans.Computer;
import gsi.calista.beans.DeviceAbstract;
import gsi.calista.beans.Door;
import gsi.calista.wrappers.RaspberryPIWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rest")
public class RestServer {

	private String filesPath = "files/";

	@GET
	@Path("/{name}/{value}")
	@Produces(MediaType.TEXT_XML)
	public String setValue(@PathParam("name") String name,
			@PathParam("value") String value) {
		String response = "";
		DeviceAbstract device = RaspberryPIWrapper.findDeviceById(name);

		if (device != null) {
			response = "<?xml version=\"1.0\"?>" + "<result>"
					+ setValue(device, value) + "</result>";

		} else {

			response = "<?xml version=\"1.0\"?>" + "<result>" + "element "
					+ name + " not found" + "</result>";

		}
		
		System.out.println("Response " + response);
		return response;
	}

	@GET
	@Path("/action/tweet/{value}")
	@Produces(MediaType.TEXT_XML)
	public String tweet(@PathParam("value") String value) {
		Calista.sendTweet(value);
		return "<?xml version=\"1.0\"?>" + "<result>" + "tweet sent: " + value
				+ "</result>";
	}

	@GET
	@Path("/action/play/{file}")
	@Produces(MediaType.TEXT_XML)
	public String play(@PathParam("file") String file) {

		Calista.play(filesPath + file);
		return "<?xml version=\"1.0\"?>" + "<result>" + "playing: " + file
				+ "</result>";
	}

	@GET
	@Path("/action/email/{from}/{to}/{title}/{content}")
	@Produces(MediaType.TEXT_XML)
	public String email(@PathParam("from") String from,
			@PathParam("to") String to, @PathParam("title") String title,
			@PathParam("content") String content) {

		Calista.sendEmail(from, to, title, content);

		return "<?xml version=\"1.0\"?>" + "<result>" + "email sent: "
				+ "title=" + title + "from=" + from + "to=" + to + "content="
				+ content + "</result>";
	}

	private String setValue(DeviceAbstract device, String value) {

		if (device instanceof Computer) {
			Computer computer = (Computer) device;
			try {
				int val = Integer.parseInt(value);
				if (val == 1) {
					computer.turnON();
					return "Computer " + computer.getName() + "turned on ";

				} else if (val == 0) {
					computer.turnOFF();
					return "Computer " + computer.getName() + " turned off ";

				}
			} catch (NumberFormatException e) {
				e.printStackTrace();

				return value + "not allowed";
			}

		} else if (device instanceof Actuator) {
			Actuator actuator = (Actuator) device;
			try {
				int val = Integer.parseInt(value);

				if (val == 1 || val == 0) {

					actuator.setDigitalValue(val);
					return "Actuator state changed to :" + value;

				} else {
					return "Value not allowed for this actuator: " + value;
				}

			} catch (NumberFormatException e) {
				e.printStackTrace();

				return value + " not allowed";
			}

		} else if (device instanceof Door) {

			Door door = (Door) device;
			try {
				int val = Integer.parseInt(value);

				if (val == 1) {
					
					door.open();
					return "Door state changed to :" + value;

				} else {
					return "Value not allowed for this door: " + value;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return value + " not allowed";
			}
		}
		return "device not supported";
	}
}
