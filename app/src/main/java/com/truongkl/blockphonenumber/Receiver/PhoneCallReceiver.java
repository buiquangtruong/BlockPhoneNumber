package com.truongkl.blockphonenumber.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.internal.telephony.ITelephony;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.truongkl.blockphonenumber.Models.Group;
import com.truongkl.blockphonenumber.Models.Number;
import com.truongkl.blockphonenumber.RoomDatabase.GroupDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDAO;
import com.truongkl.blockphonenumber.RoomDatabase.NumberDatabase;
import com.truongkl.blockphonenumber.Service.WarningCallService;
import com.truongkl.blockphonenumber.Until;

import static com.truongkl.blockphonenumber.Key.BLOCK_NUMBER;
import static com.truongkl.blockphonenumber.Key.FROM_HOUR;
import static com.truongkl.blockphonenumber.Key.FROM_MINUTE;
import static com.truongkl.blockphonenumber.Key.GROUP_NAME;
import static com.truongkl.blockphonenumber.Key.IS_BLOCK_IN_PERIOD;
import static com.truongkl.blockphonenumber.Key.SAVE_TIME;
import static com.truongkl.blockphonenumber.Key.TO_HOUR;
import static com.truongkl.blockphonenumber.Key.TO_MINUTE;

/**
 * Created by Truong KL on 11/4/2017.
 */

public class PhoneCallReceiver extends BroadcastReceiver {
    private List<String> listNumber ;
    private NumberDatabase numberDatabase;
    private NumberDAO numberDAO;
    private GroupDAO groupDAO;
    private String number;
    private FirebaseUser currentUser;
//    private String userPhoneNumber = "0964449404";
    private String userPhoneNumber;
    private SharedPreferences sharedPref;
    @Override
    public void onReceive(Context context, Intent intent) {
        // get telephony service
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephony.getCallState()){
            case TelephonyManager.CALL_STATE_RINGING:
                // get incoming call number
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Toast.makeText(context, "Phone calling " + number , Toast.LENGTH_SHORT).show();

                currentUser = FirebaseAuth.getInstance().getCurrentUser();
                userPhoneNumber = currentUser.getPhoneNumber();
                sharedPref = context.getSharedPreferences(SAVE_TIME, Context.MODE_PRIVATE);

                listNumber = new ArrayList<>();
                numberDatabase = NumberDatabase.getInstance(context);
                numberDAO = numberDatabase.numberDAO();
                groupDAO = numberDatabase.groupDAO();

                NumberLoader numberLoader = new NumberLoader();
                numberLoader.execute(context);

                break;

            case TelephonyManager.CALL_STATE_IDLE:
                context.stopService(new Intent(context, WarningCallService.class));
                break;
            default:
                break;
        }

    }

    // Ends phone call
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void breakCall(Context context) {
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(telephony);
            telephonyService.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class NumberLoader extends AsyncTask<Context, Void, Void>{
        @Override
        protected Void doInBackground(Context... contexts) {
            ArrayList<Number> numbers = (ArrayList<Number>) numberDAO.findNumber(number);
//            Number num = numberDAO.findNumber(number);
            if (numbers != null && numbers.size() != 0){
                ArrayList<Group> groups = new ArrayList<>();
                String groupName = "";
                for (Number number: numbers){
                    Group group = groupDAO.findGroupById(number.getGroupId());
                    groups.add(group);
                    groupName += group.getName() + ", ";
                }

                groupName = groupName.substring(0, groupName.length()-2);
//                Group group = groupDAO.findGroupById(num.getGroupId());

                if (isMyGroup(numbers)){
                    if (sharedPref.getBoolean(IS_BLOCK_IN_PERIOD, false)){
                        final Calendar c = Calendar.getInstance();
                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        int fromHour = sharedPref.getInt(FROM_HOUR, 8);
                        int fromMinute = sharedPref.getInt(FROM_MINUTE, 0);
                        int toHour = sharedPref.getInt(TO_HOUR, 18);
                        int toMinute = sharedPref.getInt(TO_MINUTE, 0);

                        int from = fromHour*60 + fromMinute;
                        int to = toHour*60 + toMinute;
                        int now = hour*60 + minute;
                        if (from < now && now < to){
                            if (isMyGroup(numbers)){
                                breakCall(contexts[0]);
                            }
                        }else{
                            showWarningWindow(contexts[0], groupName);
                        }
                    }else {
                        breakCall(contexts[0]);
                    }

                }else{
                    showWarningWindow(contexts[0], groupName);
                }

            }
            return null;
        }
    }

    private void showWarningWindow(Context context, String groupName){
        Intent intent = new Intent(context, WarningCallService.class);
        intent.putExtra(BLOCK_NUMBER, number);
        intent.putExtra(GROUP_NAME, groupName);
        context.startService(intent);
    }

    private boolean isMyGroup(ArrayList<Number> numbers){
        for (Number number: numbers){
            Group group = groupDAO.findGroupById(number.getGroupId());
            if (group.getUserPhoneNumber().equals(userPhoneNumber)){
                return true;
            }
        }
        return false;
    }
}
