package edu.charusat.scheduleit;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private static final int POS_HOME = 0;
    private static final int POS_PROFILE = 1;
    private static final int POS_HISTORY = 2;
    private static final int POS_SETTINGS = 3;
    private static final int POS_SIGNOUT = 5;
    private boolean doubleBackToExitPressesOnce = false;
    private String[] titles;
    private Drawable[] icons;

    private RelativeLayout rootLayout;
    DrawerAdapter adapter;
    DrawerItem item;
    private SlidingRootNav slidingRootNav;
    Toolbar toolbar;
    public static List<ContactChip> mContactList;

    private final int REQUEST_PERMISSION_SENDSMS = 2;
    private final int REQUEST_PERMISSION_READCONTACTS = 3;
    private final int REQUEST_PERMISSION_READPHONESTATE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* mContactList = new ArrayList<>();
        new RetreiveContactsBackground(this).execute();*/
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CONTACTS,
                android.Manifest.permission.SEND_SMS,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.GET_ACCOUNTS,
                android.Manifest.permission.INTERNET
        };

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withMenuLayout(R.layout.layout_left_drawer)
                .withSavedState(savedInstanceState)
                .inject();
        titles = loadTitles();
        icons = loadIcons();

        adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_HOME).setChecked(true),
                createItemFor(POS_PROFILE),
                createItemFor(POS_HISTORY),
                createItemFor(POS_SETTINGS),
                new SpaceItem(48),
                createItemFor(POS_SIGNOUT)));
        adapter.setListener(this);

        RecyclerView list = (RecyclerView) findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_HOME);
        //init();
        //String string = getSupportFragmentManager().getBackStackEntryAt(0).getName();
        //Log.v("Returned: ",String.valueOf(getSupportFragmentManager().getBackStackEntryAt(0).getName()));
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this,PERMISSIONS,1);
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        switch(requestCode){
            case REQUEST_PERMISSION_READPHONESTATE:
                if (!(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(getApplicationContext(),"Please allow all permissions",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_SENDSMS:
                if (!(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(getApplicationContext(),"Please allow all permissions",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_READCONTACTS:
                if (!(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    Toast.makeText(getApplicationContext(),"Please allow all permissions",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /*@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!slidingRootNav.isMenuHidden() && slidingRootNav!=null){
            boolean touched = contentFrame.dispatchTouchEvent(ev);
            Log.d("touched: ",String.valueOf(touched));
            if (touched){
                slidingRootNav.closeMenu();
            }
            return true;
        }
        else{
            return super.dispatchTouchEvent(ev);
        }
    }*/


    public void init(){
        //showFragment(new MainFragment(),"Home");
        /*getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame,new MainFragment())
                .addToBackStack("Home")
                .commit();*/
        //Log.v("Returned: ",String.valueOf(getSupportFragmentManager().getBackStackEntryAt(0).getName()));
    }
    @Override
    public void onBackPressed() {
        if (!slidingRootNav.isMenuHidden())
            slidingRootNav.closeMenu();
        else{
            Log.v("Before condition: ",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
            if (getSupportFragmentManager().getBackStackEntryCount() >= 1)
            {
                Log.v("Bef pop: ",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                getSupportFragmentManager().popBackStackImmediate();
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,new MainFragment()).commit();
                getSupportActionBar().setTitle("Home");
                item = adapter.items.get(POS_HOME);
                for (int i = 0; i < adapter.items.size(); i++) {
                    DrawerItem item = adapter.items.get(i);
                    if (item.isChecked()) {
                        item.setChecked(false);
                        adapter.notifyItemChanged(i);
                        break;
                    }
                }
                item.setChecked(true);
                adapter.notifyItemChanged(POS_HOME);
                //adapter.setSelected(POS_HOME);
                Log.v("After pop: ",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                //showFragment(new MainFragment(),"Home");
            }
            else
            {
                if(doubleBackToExitPressesOnce){
                    super.onBackPressed();  
                }
                doubleBackToExitPressesOnce = true;
                Toast.makeText(MainActivity.this,"Press back again to exit",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressesOnce = false;
                    }
                },2000);
            }
                //super.onBackPressed();
        }
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(icons[position], titles[position])
                .withIconTint(color(R.color.textColorSecondary))
                .withTextTint(color(R.color.textColorPrimary))
                .withSelectedIconTint(color(R.color.colorAccent))
                .withSelectedTextTint(color(R.color.colorAccent));
    }
    private String[] loadTitles(){
        return getResources().getStringArray(R.array.ld_titles);
    }

    private Drawable[] loadIcons(){
        TypedArray typedArray = getResources().obtainTypedArray(R.array.ld_icons);
        Drawable[] icons = new Drawable[typedArray.length()];
        for(int i=0;i<typedArray.length();i++){
            int id= typedArray.getResourceId(i,0);
            if(id!=0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        typedArray.recycle();
        return icons;
    }

    private void showFragment(Fragment fragment, String str) {
        if (str.equals("Home"))
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .addToBackStack(str)
                    .commit();
        }
        else
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, fragment)
                    .commit();
        }
    }
    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
    @Override
    public void onItemSelected(int position) {
        switch (position){
            case POS_PROFILE:
                showFragment(new ProfileFragment(),"Profile");
                getSupportActionBar().setTitle("Profile");
                if (getSupportFragmentManager().getBackStackEntryCount()==0){
                    getSupportFragmentManager().beginTransaction().addToBackStack("Home").commit();
                }
                /*Intent intent = new Intent(MainActivity.this,SchedulerActivity.class);
                intent.putExtra("fragment","Profile");
                startActivity(intent);*/
                Log.v("Returned: ",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                break;
            case POS_HOME:
                showFragment(new MainFragment(),"Home");
                getSupportActionBar().setTitle("Home");
                Log.v("Returned: ",String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
                break;
            case POS_HISTORY:
                showFragment(new HistoryFragment(),"History");
                getSupportActionBar().setTitle("History");
                if (getSupportFragmentManager().getBackStackEntryCount()==0){
                    getSupportFragmentManager().beginTransaction().addToBackStack("Home").commit();
                }
                break;
            case POS_SETTINGS:
                showFragment(new SettingsFragment(),"Settings");
                getSupportActionBar().setTitle("Settings");
                if (getSupportFragmentManager().getBackStackEntryCount()==0){
                    getSupportFragmentManager().beginTransaction().addToBackStack("Home").commit();
                }
                break;

        }
        slidingRootNav.closeMenu();
        /*Fragment selectedScreen = MainFragment.createFor(titles[position]);
        showFragment(selectedScreen);*/
    }

}
