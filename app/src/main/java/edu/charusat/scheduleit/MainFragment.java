package edu.charusat.scheduleit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import edu.charusat.scheduleit.MainFragments.SmsFragment;

/**
 * Created by HP on 13-09-2018.
 */

public class MainFragment extends Fragment implements View.OnClickListener{
    private TabLayout tabLayout;
    private TabAdapter adapter;
    private ViewPager viewPager;
    FloatingActionMenu floatingActionMenu;
    FloatingActionButton btn_sms;
    FloatingActionButton btn_whatsapp;
    FloatingActionButton btn_email;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_main,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        btn_sms = (FloatingActionButton)view.findViewById(R.id.sms);
        btn_email = (FloatingActionButton) view.findViewById(R.id.email);
        btn_whatsapp = (FloatingActionButton) view.findViewById(R.id.whatsapp);
        floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.floatingActionMenu);
        floatingActionMenu.setClosedOnTouchOutside(true);
        final View shadowView = view.findViewById(R.id.shadowView);

        int[] tabIcons={
            R.drawable.ic_schedule_24dp,
            R.drawable.ic_done_all_24dp,
            R.drawable.ic_failed_outline_24dp
        };

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        adapter = new TabAdapter(getFragmentManager());
        adapter.addFragment(new ScheduledFragment(),"Scheduled");
        adapter.addFragment(new CompletedFragment(),"Completed");
        adapter.addFragment(new FailedFragment(),"Failed");
        tabLayout.bringToFront();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);

        floatingActionMenu.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Clicked","True");
                if (!floatingActionMenu.isOpened()){
                    shadowView.setVisibility(View.VISIBLE);
                    Log.v("Opened","True");
                }
                else {
                    shadowView.setVisibility(View.GONE);
                }
                floatingActionMenu.toggle(true);
            }
        });
        btn_email.setOnClickListener(this);
        btn_whatsapp.setOnClickListener(this);
        btn_sms.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    public View getView(){
        return LayoutInflater.from(getContext()).inflate(R.layout.activity_main,null);
    }

    private static final String EXTRA_TEXT = "text";

    public static MainFragment createFor(String text) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sms:
                //Toast.makeText(getContext(),"Scheudle sms",Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getActivity(),SchedulerActivity.class);
                intent1.putExtra("pos",0);
                startActivity(intent1);
                break;
            case R.id.whatsapp:
                Toast.makeText(getContext(),"Scheudle whatsapp msg",Toast.LENGTH_SHORT).show();
                Intent intent2 = new Intent(getActivity(),SchedulerActivity.class);
                intent2.putExtra("pos",2);
                break;
            case R.id.email:
                Toast.makeText(getContext(),"Scheudle email",Toast.LENGTH_SHORT).show();
                Intent intent3 = new Intent(getActivity(),SchedulerActivity.class);
                intent3.putExtra("pos",1);
                break;
        }
    }

    /*@Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final String text = getArguments().getString(EXTRA_TEXT);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(v.getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }*/
}
