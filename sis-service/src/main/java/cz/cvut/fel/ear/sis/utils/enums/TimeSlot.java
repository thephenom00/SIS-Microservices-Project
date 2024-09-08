package cz.cvut.fel.ear.sis.utils.enums;
import java.time.LocalTime;

public enum TimeSlot {
    SLOT1(LocalTime.of(7, 30), LocalTime.of(9, 0)),
    SLOT2(LocalTime.of(9, 15), LocalTime.of(10, 45)),
    SLOT3(LocalTime.of(11, 0), LocalTime.of(12, 30)),
    SLOT4(LocalTime.of(12, 45), LocalTime.of(14, 15)),
    SLOT5(LocalTime.of(14, 30), LocalTime.of(16, 0)),
    SLOT6(LocalTime.of(16, 15), LocalTime.of(17, 45)),
    SLOT7(LocalTime.of(18, 0), LocalTime.of(19, 30));

    private final LocalTime startTime;
    private final LocalTime endTime;

    TimeSlot(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}

