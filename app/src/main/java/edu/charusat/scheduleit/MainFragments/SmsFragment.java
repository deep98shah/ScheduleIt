package edu.charusat.scheduleit.MainFragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.pchmn.materialchips.ChipsInput;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import edu.charusat.scheduleit.ContactChip;
import edu.charusat.scheduleit.MainActivity;
import edu.charusat.scheduleit.R;
import edu.charusat.scheduleit.Receiver.SmsReceiver;


/**
 * Created by HP on 28-09-2018.
 */

public class SmsFragment extends Fragment implements View.OnClickListener{
    private View view;
    TelephonyManager telephonyManager;
    SubscriptionManager subscriptionManager;
    List<String> simOperatorNames;
    Calendar cal;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private TextView textViewDate;
    private TextView textViewTime;
    private int curr_day,curr_month,curr_year,curr_hour,curr_min;
    private String sms_text;
    private ChipsInput chipsInput;
    private List<ContactChip> mContactList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sms,container,false);
        telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        subscriptionManager = (SubscriptionManager) getActivity().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        simOperatorNames = new ArrayList<>();

        textViewDate = (TextView) view.findViewById(R.id.tv_date);
        textViewTime = (TextView) view.findViewById(R.id.tv_time);

        mContactList = new ArrayList<>();
        chipsInput = (ChipsInput) view.findViewById(R.id.chips_input);
        build_suggestion_list();

        cal = Calendar.getInstance();
        //get current instances of date and time
        curr_day = cal.get(Calendar.DAY_OF_MONTH);
        curr_month = cal.get(Calendar.MONTH);
        curr_year = cal.get(Calendar.YEAR);
        curr_hour = cal.get(Calendar.HOUR_OF_DAY);
        curr_min = cal.get(Calendar.MINUTE);
        //Set current date and time to TextView
        textViewDate.setText(curr_day+"/"+curr_month+"/"+curr_year);
        textViewTime.setText(curr_hour+":"+curr_min);
        textViewDate.setOnClickListener(this);
        textViewTime.setOnClickListener(this);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v("Fragment","OnCreateOptionsMenu");
        inflater.inflate(R.menu.schedule_btn_item,menu);
        LinearLayout btn_schedule = (LinearLayout) menu.findItem(R.id.schedule_btn).getActionView();
        btn_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Schedule item",Toast.LENGTH_SHORT).show();
                List<ContactChip> contactSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
                for (int i=0;i<contactSelected.size();i++){
                    setAlarmForSMS(i);
                }

            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("Fragment","onOptionsItemSelected");
        switch (item.getItemId()){
            case R.id.schedule_btn:
                Toast.makeText(getActivity(),"Schedule button",Toast.LENGTH_SHORT).show();
                Log.v("Sms","Scheduled");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

            List<SubscriptionInfo> subInfoList = subscriptionManager.getActiveSubscriptionInfoList();
            for(int i=0;i<subInfoList.size();i++) {
                simOperatorNames.add("Sim " + (i + 1) + ": " + subInfoList.get(i).getCarrierName());
            }
            addItemsToSpinner(simOperatorNames);
            //build_suggestion_list();
    }


    private void addItemsToSpinner(List<String> arrayList){
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_sim);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item,arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

    }

    private void build_suggestion_list(){
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);

        // loop over all contacts
        if(phones != null) {
            while (phones.moveToNext()) {
                // get contact info
                String phoneNumber = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if(avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get phone number
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

                    while (pCur != null && pCur.moveToNext()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    pCur.close();

                }
                ContactChip contactChip = new ContactChip(id, avatarUri, name, phoneNumber);
                // add contact to the list
                mContactList.add(contactChip);
            }
            phones.close();
        }

        // pass contact list to chips input
        chipsInput.setFilterableList(mContactList);
        //chipsInput.setFilterableList(MainActivity.mContactList);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_date:
                datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        textViewDate.setText(dayOfMonth+"/"+month+"/"+year);
                        cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        cal.set(Calendar.MONTH,month);
                        cal.set(Calendar.YEAR,year);
                        Log.v("Date: ",String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                    }
                },curr_year,curr_month,curr_day);
                datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();
                break;
            case R.id.tv_time:
                timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        textViewTime.setText(hourOfDay+":"+minute);
                        cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        cal.set(Calendar.MINUTE,minute);
                        Log.v("Time: ",String.valueOf(cal.get(Calendar.HOUR_OF_DAY)));
                    }
                },curr_hour,curr_min,true);
                timePickerDialog.show();
                break;
        }
    }

    private void setAlarmForSMS(int id){
        List<ContactChip> contactSelected = (List<ContactChip>) chipsInput.getSelectedChipList();
        for (int i=0;i<contactSelected.size();i++){
            Log.v("Contact " +i+": ",contactSelected.get(i).getInfo());
        }
        EditText et_msg = (EditText)view.findViewById(R.id.et_message);
        sms_text = et_msg.getText().toString();
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), SmsReceiver.class);
        intent.putExtra("message",sms_text);
        intent.putExtra("phonenumber",contactSelected.get(id).getInfo());
        intent.putExtra("contactname",contactSelected.get(id).getLabel());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),id,intent,0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal.getTimeInMillis(),pendingIntent);
    }

}
