package dev.shobhik.balansere;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.shobhik.balansere.adapter.DeviceItemAdapter;
import dev.shobhik.balansere.rotator.RotationAttitudeDemo;
import dev.shobhik.balansere.rotator.RotationSensorActivity;
import dev.shobhik.balansere.service.NSDService;


public class MainActivity extends AppCompatActivity {
    private MainActivity mActivity;
    public static final String TAG = "RotationAttitudeDemo";
    private static final int SENSOR_CODE = 1337;

    Context mContext;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
//    WifiP2pManager.PeerListListener myPeerListListener;
    NSDService mNsdService;
    String SERVICES = "_http._tcp.";

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();

    ListView lv;
    TextView tv;
    DeviceItemAdapter mAdapter;
    Button findPeersButton;
    Button startServerButton;
    Button rotationVectorButton;
    int precisionDigits = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        lv = findViewById(R.id.listview);
        tv = findViewById(R.id.textview1);
        mAdapter = new DeviceItemAdapter(mContext, R.id.listview, new ArrayList<WifiP2pDevice>());
        lv.setAdapter(mAdapter);
        findPeersButton = findViewById(R.id.button);
        startServerButton = findViewById(R.id.button2);
        rotationVectorButton = findViewById(R.id.button3);

        //Initialize WiFi interaction objects
        mNsdService = new NSDService(mContext);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        findPeersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv.setText("Searching for peers");
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        //TODO: Implement
                        Log.v(TAG, "Success finding peers");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        //TODO: Implement
                        Log.v(TAG, "Failure finding peers");
                    }
                });

            }
        });

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SensorActivity.class));
            }
        });
        rotationVectorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sensorIntent = new Intent(mContext, RotationSensorActivity.class);
//                sensorIntent.putExtra("returnType", "boolean");
                sensorIntent.putExtra("returnType", "float");
                sensorIntent.putExtra("precision", precisionDigits);
                sensorIntent.putExtra("boolPrecision", precisionDigits);
                startActivityForResult(sensorIntent, SENSOR_CODE);
            }
        });
        startServerButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(mContext, SensorActivityBackup.class));
                return true;
            }
        });
        rotationVectorButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(mContext, RotationAttitudeDemo.class));
                return true;
            }
        });
    }



    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SENSOR_CODE) {
            if(resultCode == RESULT_OK) {
                String result = "";
                if(data.hasExtra("isLevel")){
                    result = "" + data.getBooleanExtra("isLevel", false);
                } else if (data.hasExtra("value")) {
                    result = "" + String.format(getDecimalFormat(), data.getFloatExtra("value", 0.0f));
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(result);
                builder.create().show();

            }

        }
    }
    private String getDecimalFormat() {
        switch (precisionDigits) {
            case 0:
                return "%.0f";
            case 1:
                return "%.1f";
            case 2:
                return "%.2f";
            case 3:
                return "%.3f";
            case 4:
                return "%.4f";
            default:
                return "%.1f";
        }
    }




    private WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
//            List<WifiP2pDevice> refreshedPeers = (List<WifiP2pDevice>) peerList.getDeviceList();
            Log.v(TAG, "Listener, result size: " + refreshedPeers.size());
            if (!refreshedPeers.equals(peers)) {
                Log.v(TAG, "Listener, refreshing list");
                peers.clear();
                peers.addAll(refreshedPeers);

                // If an AdapterView is backed by this data, notify it
                // of the change.  For instance, if you have a ListView of
                // available peers, trigger an update.
                mAdapter.notifyDataSetChanged();

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {
                Log.d(MainActivity.TAG, "No devices found");
                tv.setText("No devices found. Goodbye cruel world!");
                return;
            }
        }
    };


    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;
        private MainActivity mActivity;


        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                           MainActivity activity) {
            super();
            this.mManager = manager;
            this.mChannel = channel;
            this.mActivity = activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                Log.v(TAG, "Receiver: STATE CHANGED ACTION" + intent.getDataString());
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                } else {
                    // Wi-Fi P2P is not enabled
                }

            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                Log.v(TAG, "Receiver: PEERS CHANGED ACTION" + intent.getDataString());
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                if (mManager != null) {
                    mManager.requestPeers(mChannel, myPeerListListener);
                }

            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                // Respond to new connection or disconnections
                Log.v(TAG, "Receiver: CONNECTION CHANGED ACTION" + intent.getDataString());
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
                Log.v(TAG, "Receiver: DEVICE WIFI STATE CHANGED ACTION" + intent.getDataString());
            }
        }

    }
}
