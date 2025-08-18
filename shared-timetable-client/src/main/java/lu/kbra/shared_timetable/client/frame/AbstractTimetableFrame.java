package lu.kbra.shared_timetable.client.frame;

import lu.kbra.shared_timetable.client.config.TimetableFrameConfig;

public interface AbstractTimetableFrame {

	void setActive();

	void validateConfig(TimetableFrameConfig timetableFrameConfig);
	
}
