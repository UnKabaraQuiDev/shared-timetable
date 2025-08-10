package lu.kbra.shared_timetable.client;

import java.util.ArrayList;

import lu.kbra.shared_timetable.client.frame.TimetableFrame;
import lu.kbra.shared_timetable.client.network.WSClient;

public class ClientMain {

	private WSClient wsClient;
	private TimetableFrame timetableFrame;

	private ClientMain() {
		timetableFrame = new TimetableFrame(new ArrayList<>());
	}

	public static void main(String[] args) {
		new ClientMain();
	}

}
