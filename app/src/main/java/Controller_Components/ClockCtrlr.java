package Controller_Components;

import com.example.chargergui.dateTime;

//Provides a means to configure management of time tracking by Charging Station.
public class ClockCtrlr {
    dateTime DateTime ;// Contains the current date and time.
    String TimeOffset ; //string Configured local time offset in the format: "+01:00", "-02:00" etc.
    dateTime NextTimeOffsetTransitionDateTime  ; //Date time of the next time offset transition.
    String TimeSource ;//string Via this variable, the Charging Station provides the CSMS with the option to
    // configure a clock source, if more than 1 are implemented.
    String TimeZone ; //string Configured current local time zone in the format: "Europe/Oslo", "Asia/Singapore" etc.
    int TimeAdjustmentReportingThreshold ; //If set, then time adjustments with an absolute value in seconds larger than this
    //need to be reported as a security event SettingSystemTime.
}
