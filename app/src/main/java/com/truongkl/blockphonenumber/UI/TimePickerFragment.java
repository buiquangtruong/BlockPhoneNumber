package com.truongkl.blockphonenumber.UI;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Truong KL on 12/6/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private OnSetDoneListener onSetDoneListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        onSetDoneListener.onSetDone(hourOfDay,minute);
    }

    public void setOnSetDoneListener(OnSetDoneListener onSetDoneListener) {
        this.onSetDoneListener = onSetDoneListener;
    }

    public interface OnSetDoneListener {
        void onSetDone(int hourOfDay, int minute);
    }
}
