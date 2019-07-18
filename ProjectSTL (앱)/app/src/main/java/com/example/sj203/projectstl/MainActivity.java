package com.example.sj203.projectstl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.FragmentTransitionSupport;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import android.os.Handler;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Firebase 선언
    private DatabaseReference mDatabase;
    NavigationView navigationView;

    ////////////////////////////  블루투스 선언  ////////////////////////////

    BluetoothSocket mSocket;
    BluetoothDevice mRemoteDevice;

    OutputStream mOutputStream;
    InputStream mInputStream;

    public static final int REQUEST_ENABLE_BT = 1;
    public static boolean IsConnectedBluetooth = false;

    Set<BluetoothDevice> mDevices;

    Thread mWorkerThread;

    String mDelimiter = "U";

    int readBufferPosition;
    byte[] readBuffer;

    BluetoothAdapter mBluetoothAdapter;

    Integer traffic_1 = 0;
    Integer traffic_2 = 0;

    TLFragment tlfgragment;
    DustFragment dustfragment;

    ////////////////////////////  블루투스 선언  ////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // 툴바 레이아웃 기본생성
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        mDatabase = FirebaseDatabase.getInstance().getReference(); // 데이터베이스 정의

        // 값을 받아 사용되는 Fragment들 메모리에 정의
        tlfgragment = new TLFragment();
        dustfragment = new DustFragment();

        // Fragment에 첫번째(MainFragment) 화면 띄워주기
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new MainFragment()).addToBackStack(null).commit();

        // 블루투스 기능 활성화
        BluetoothConnect();

    }

    @Override
    public void onBackPressed() { // 뒤로가기 버튼 눌렸을때
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // 오른쪽 위의 버튼 누르면 나오는 옵션들 만듦
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { // 위에서 만든 옵션들 중 하나 실행되었을 때
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //왼쪽 네비게이션 바에서 어떤 메뉴가 선택되었을 때
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // 각각에 맞는 Fragment를 화면에 띄워줌
        if (id == R.id.nav_home) {
            changefragment_nonstack(new MainFragment());
        } else if (id == R.id.nav_livetrafficlight) {
            changefragment_nonstack(new TLFragment());
        } else if (id == R.id.nav_livecctv) {
            changefragment_nonstack(new CCTVFragment());
        } else if (id == R.id.nav_livedust) {
            changefragment_nonstack(new DustFragment());
        } else if (id == R.id.nav_share) {
            SelectDeviceBuilder();
        } else if (id == R.id.nav_alert) {
            changefragment_nonstack(new EmerFragment());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void card_ltl (View v)  { // 실시간 신호등 보여주는 Fragment로 이동
        changefragment_stack(new TLFragment());
        navigationView.setCheckedItem(R.id.nav_livetrafficlight);
    }
    public void card_lcctv (View v) { // 실시간 CCTV 보여주는 Fragment로 이동
        changefragment_stack(new CCTVFragment());
        navigationView.setCheckedItem(R.id.nav_livecctv);
    }
    public void card_ldust (View v) { // 미세먼지 상황 보여주는 Fragment로 이동
        changefragment_stack(new DustFragment());
        navigationView.setCheckedItem(R.id.nav_livedust);
    }
    public void card_alert (View v) { // 비상상황 바로가기 보여주는 Fragment로 이동
        changefragment_stack(new EmerFragment());
        navigationView.setCheckedItem(R.id.nav_alert);
    }
    public void projectstlonclick (View v) { // 위쪽 앱 이름 터치시 첫번째 화면으로 이동
        changefragment_nonstack(new MainFragment());
        navigationView.setCheckedItem(R.id.nav_home);
    }
    public void emer_112 (View v) { // 비상상황 바로가기 창에서 112가 눌렸을 때 112에 전화걸기
        dialContactPhone("112");
    }
    public void emer_119 (View v) { // 비상상황 바로가기 창에서 119가 눌렸을 때 119에 전화걸기
        dialContactPhone("119");
    }


    public void changefragment_stack (Fragment fragment) // 이미 있는 Fragment 위에 새로운 Fragment를 Stack처럼 쌓는다.
                                                         // 뒤로가기 누르면 전 화면으로 돌아감
    {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
    public void changefragment_nonstack (Fragment fragment) // 이미 있는 Fragment 없애고 새로운 Fragment를 보여준다
    {
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.fragment))
                .add(R.id.fragment, fragment)
                .commit();
    }

    private void dialContactPhone(final String phoneNumber) { // 받은 전화번호로 전화를 건다.
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }





////////////////////////////  블루투스 함수  ////////////////////////////

    private void BluetoothConnect() { // 블루투스 지원하는 기기인지 확인 후 블루투스 활성화 요청 보냄
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            //블루투스 지원을 하지 않음
            Toast.makeText(MainActivity.this, "이 기기는 블루투스를 지원하지 않습니다",
                    Toast.LENGTH_LONG).show();
        }
        else if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            SelectDeviceBuilder();
        }
    }



    BluetoothDevice getDeviceFromBondedList(String name) { // 블루투스 모듈에서 연결 가능한 디바이스 목록을 가져옴
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }


    void connectToSelectedDevices(String selectedDeviceName){ // 선택된 디바이스와 연결한다.
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);

            mSocket.connect();

            mOutputStream = mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                System.out.println("trying fallback...");

                mSocket = (BluetoothSocket) mRemoteDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mRemoteDevice,1);
                mSocket.connect();
                mOutputStream = mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();

                System.out.println("Connected");

                //setText connected
            }
            catch (Exception e2) {
                System.out.println( "Couldn't establish Bluetooth connection!");
            }
        }
    }


    public void SelectDeviceBuilder() // 연결 가능한 디바이스 목록을 이용하여 리스트 만든 후 띄워준다.
    {

        mDevices = mBluetoothAdapter.getBondedDevices();

        if(mDevices.size()==0)
            return ;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 설정");
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        listItems.add("취소");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == mDevices.size()) {

                } else {
                    // 연결할 장치를 선택한 경우
                    try {
                        // 선택한 장치와 연결을 시도함
                        connectToSelectedDevices(items[item].toString());
                        beginListenForData();
                    }

                    // 선택한 장치와 연결 실패
                    catch(Exception e2) {
                        Toast.makeText(getApplicationContext(), "블루투스 연결중 오류가 발생", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        builder.show();
    }

    void sendData(String msg) {
        //msg += mDelimiter;
        try {

            mOutputStream.write(msg.getBytes());    // 문자열 전송
            System.out.println(msg);

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();

        }
    }


    void beginListenForData(){ // 계속해서 블루투스 기기에서 보내는 정보를 받아온다.

        IsConnectedBluetooth = true;

        final Handler handler = new Handler();

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        int byteCount = mInputStream.available();
                        if(byteCount > 0)
                        {
                            SystemClock.sleep(1000);

                            byte[] rawBytes = new byte[byteCount]; // 저장할 공간을 만들어준다.

                            mInputStream.read(rawBytes); // 정보를 수신한다.

                            final String datastr = new String(rawBytes,"UTF-8"); // 수신된 정보를 UTF-8로 번역한다.
                            handler.post(new Runnable() {
                                public void run()
                                {
                                    System.out.println(datastr); // Log에 받은 정보 출력해준다.

                                    String[] GetValue = datastr.split("\n"); // 필요한 정보를 나누어 저장해준다.

                                    mDatabase.child("Traffic_1").setValue(traffic_1); // 데이터베이스에 신호위반한 건수를 올려준다.
                                    mDatabase.child("Traffic_2").setValue(traffic_2); // 데이터베이스에 위험할뻔한 건수를 올려준다.

                                    int len = GetValue.length;

                                    for (int i=0; i<len; i++)
                                    {
                                        tlfgragment.changetlinfo(GetValue[i]); // 신호등 정보 바꾸는 함수를 실행시켜준다.
                                        dustfragment.changedustinfo(GetValue[i]); // 미세먼지 정보 바꾸는 함수를 실행시켜준다.

                                        try {
                                            if (Integer.parseInt(GetValue[i]) > 3) { // 미세먼지 정도를 데이터베이스에 저장
                                                mDatabase.child("Dust").setValue(Integer.parseInt(GetValue[i]));
                                            }
                                        }
                                        catch (NumberFormatException ex) {

                                        }
                                    }
                                }
                            });
                        }

                    }
                    catch (IOException ex) {
                        // 데이터 수신 중 오류 발생.
                        Toast.makeText(getApplicationContext(), "데이터 통신 에러", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mWorkerThread.start();
    }

    ////////////////////////////  블루투스 함수  ////////////////////////////

}