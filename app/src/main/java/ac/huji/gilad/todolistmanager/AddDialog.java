package ac.huji.gilad.todolistmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddDialog extends DialogFragment {
    private Date reminderDate;
    private Date reminderTime;
    private EditText title;
    private EditText reminder;
    private EditText timeChoose;

    private DatePickerDialog reminderDialog;
    private TimePickerDialog timeDialog;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.add_dialog_fragment, null);

        title = (EditText) view.findViewById(R.id.new_to_do_item_edit_text);
        reminder = (EditText) view.findViewById(R.id.reminder_edit_text);
        timeChoose = (EditText) view.findViewById(R.id.reminder_time_edit_text);

        reminder.setInputType(InputType.TYPE_NULL);
        reminder.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    reminderDialog.show();
                }
            }
        });
        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reminderDialog.show();
            }
        });

        Calendar calendar = Calendar.getInstance();
        reminderDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                reminderDate = newDate.getTime();
                reminder.setText(dateFormat.format(reminderDate));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        reminderDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        timeChoose.setInputType(InputType.TYPE_NULL);
        timeChoose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    timeDialog.show();
                }
            }
        });
        timeChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeDialog.show();
            }
        });

        timeDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar newTime = Calendar.getInstance();
                newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                newTime.set(Calendar.MINUTE, minute);
                reminderTime = newTime.getTime();
                timeChoose.setText(timeFormat.format(reminderTime));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timeDialog.setTitle("Select reminder time");

        builder.setView(view);

        builder.setTitle("Add new TODO");

        builder.setPositiveButton("Add", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog mDialog = builder.create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (title.getText().toString().trim().length() == 0 ||
                                reminder.getText().toString().length() == 0 ||
                                timeChoose.getText().toString().length() == 0) {
                            Snackbar.make(view, "Don't you want to remember anything?", Snackbar.LENGTH_SHORT).show();
                        } else {
                            ToDoListManager manager = ((ToDoListManager) getActivity());
                            Calendar date = Calendar.getInstance();
                            Calendar time = Calendar.getInstance();
                            date.setTime(reminderDate);
                            time.setTime(reminderTime);
                            for (int type : new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE}) {
                                date.set(type, time.get(type));
                            }
                            manager.adapter.add(
                                    title.getText().toString(),
                                    date.getTime());
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });

        return mDialog;
    }
}
