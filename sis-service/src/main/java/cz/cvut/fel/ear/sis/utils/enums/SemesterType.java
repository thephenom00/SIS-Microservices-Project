package cz.cvut.fel.ear.sis.utils.enums;
import java.time.MonthDay;

public enum SemesterType {
    SPRING(MonthDay.of(1, 1), MonthDay.of(6, 30)),
    FALL(MonthDay.of(7, 1), MonthDay.of(12, 31));

    private final MonthDay startDate;
    private final MonthDay endDate;

    SemesterType(MonthDay startDate, MonthDay endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public MonthDay getStartDate() {
        return startDate;
    }

    public MonthDay getEndDate() {
        return endDate;
    }
}
