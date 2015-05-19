package pl.kurbanek.sterownik2;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ControllerListActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 0;
	BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private ListView bpt_list;
	private ArrayAdapter<String> mArrayAdapter;
	Set<BluetoothDevice> pairedDevices;
	private BluetoothDevice mmDevice;
	BluetoothSocket mmSocket;
    public OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller_list_activity);
		
		// ------------ BLUETOOTH -------------
		// czy urz¹dzenie ob³uguje Bluetooth
		if(myBluetoothAdapter==null){
			Toast.makeText(getApplicationContext(), "Twoje urz¹dzenie nie obs³uguje bluetooth'a", Toast.LENGTH_LONG).show();
			finish();
		}
		// czy urz¹dzenie jest w³¹czone -> odpowiedz nastepuje w onActivityResult
		if(!myBluetoothAdapter.isEnabled()){
			Intent enableMyBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableMyBT, REQUEST_ENABLE_BT);
		}
		// pobieranie sparowanych urz¹dzeñ
		int i=0;
		pairedDevices = myBluetoothAdapter.getBondedDevices();
		final String [] deviceList = new String[pairedDevices.size()];
		// If there are paired devicess
		if (pairedDevices.size() > 0) {
			// Loop through paired devices	
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a ListView
				deviceList[i]=(device.getName() + "\n" + device.getAddress());
				// INFORMACJE DO LOG CATA
				Log.d("BLUETOOTH LOG W£ASNY", "JAKAŒ MOJA WIADOMOŒÆ");
				i++;
			}
		}
		// -------- LISTA WE W£ASNYM STYLU -------
		bpt_list=(ListView)findViewById(R.id.bt_list);
		List_Custom lista = new List_Custom(this, deviceList);
		bpt_list.setAdapter(lista);
		
		// wybieranie urz¹dzenia z listy przez u¿ytkownika - wybrane urz¹dzenie jest przypisywane do zmiennej mmDevice
		bpt_list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getApplicationContext(), deviceList[position], Toast.LENGTH_SHORT).show();
					//Toast.makeText(getApplicationContext(), "Click ListItem Number " + item, Toast.LENGTH_LONG).show();
					for (BluetoothDevice device : pairedDevices) {
						if((device.getName() + "\n" + device.getAddress()).equals(deviceList[position])){
							mmDevice = device;
							Toast.makeText(getApplicationContext(), "£¹czenie... ", Toast.LENGTH_SHORT).show();
					        break;
					    } 
					} 
					try {
						openBT();
						// usunac
						for(int k=0; k<100; k++){
							String cos = "siemaaaa";
							sendData("sztos\n");
							sendData(cos);
						}
		            } catch (IOException ex) {
		            	ex.printStackTrace();
		            }
					// dopisac przejscie do innego activity
					Intent intent1 = new Intent(getBaseContext(), AnimationActivity.class);
					startActivity(intent1);
					//finish();
			}
		}); 

	}
	
	// ------------ ODPOWIEDZ NA RZADANIE Z INTENCJI  REQUEST_ENABLE_BT ---------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
	if(requestCode == REQUEST_ENABLE_BT){
		if(myBluetoothAdapter.isEnabled()) {
			Toast.makeText(getApplicationContext(), "W³¹czono Bluetooth", Toast.LENGTH_LONG).show();
	    }else{  
	    	Toast.makeText(getApplicationContext(), "Aby ko¿ystaæ z aplikacji Bluetooth musi byæ w³¹czony", Toast.LENGTH_LONG).show();
	        finish();
	         }
	    }
	 }
	// ---------- POLECENIE POLACZ Z URZADZENIEM -------------------
	void openBT() throws IOException {
	        //UUID MY_UUID;
			// Standard SerialPortService ID
	        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
	        mmSocket.connect();
	        mmOutputStream = mmSocket.getOutputStream();
	        mmInputStream = mmSocket.getInputStream();
	        
	        // zainicjalizowanie odbioru danych
	        beginListenForData();
	        // wys³anie do uC informacji zgodnie zprotoko³em ze po³¹czono
	        sendData("polaczono\n");
	        //sendData("obr:"+Integer.toString(obroty)+"\n");
	        //sendData("obr:"+Integer.toString(odleglosc)+"\n");
	        //sendData("obr:"+Integer.toString(opoznienie)+"\n");
	        // usun¹æ to info i daæ dopiero gdy bêd¹ odebrane dane
	        Toast.makeText(getApplicationContext(), "Po³¹czono z: " + mmDevice.getName(), Toast.LENGTH_LONG).show();
	}
	// ------- BEGIN LISTEN FOR DATA ----------
	void beginListenForData() {
	    try {
	        final Handler handler = new Handler();
	         
	        // This is the ASCII code for a newline character
	        final byte delimiter = 10;

	        stopWorker = false;
	        readBufferPosition = 0;
	        readBuffer = new byte[1024];
	         
	        workerThread = new Thread(new Runnable() {
	            public void run() {
//	            	final DrawingView view=(DrawingView)findViewById(R.id.drawingView);
	                while (!Thread.currentThread().isInterrupted()
	                        && !stopWorker) {
	                     
	                    try {
	                         
	                        int bytesAvailable = mmInputStream.available();
	                        if (bytesAvailable > 0) {
	                            byte[] packetBytes = new byte[bytesAvailable];
	                            mmInputStream.read(packetBytes);
	                            for (int i = 0; i < bytesAvailable; i++) {
	                                byte b = packetBytes[i];
	                                if (b == delimiter) {
	                                    byte[] encodedBytes = new byte[readBufferPosition];
	                                    System.arraycopy(readBuffer, 0,
	                                            encodedBytes, 0,
	                                            encodedBytes.length);
	                                    final String data = new String(
	                                            encodedBytes, "US-ASCII");
	                                    readBufferPosition = 0;

	                                    handler.post(new Runnable() {
	                                        public void run() {
	                                        	// dane s¹ pobierane do zmiennej data - przekazac j¹ do parsowania i nizej odpalic swoja funkcje
	                                        	Toast.makeText(getApplicationContext(), "dane: "+data, Toast.LENGTH_LONG).show();
//	                                        	view.rotateSegment(1, Integer.parseInt(data));
	                                        	//view.rotateSegment(2, 10);s
	                                        }
	                                    });
	                                } else {
	                                    readBuffer[readBufferPosition++] = b;
	                                }
	                            }
	                        }
	                         
	                    } catch (IOException ex) {
	                        stopWorker = true;
	                    }
	                     
	                }
	            }
	        });

	        workerThread.start();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	// ------------------ SEND DATA -----------------
	void sendData(String data) throws IOException {
	    try {
	        // the text typed by the user
	        String msg = data;
	        //myTextbox.getText().toString();
	        msg += "\n";
	        mmOutputStream.write(msg.getBytes());
	        // tell the user data were sent
	        //Toast.makeText(getApplicationContext(), "Zapisano ", Toast.LENGTH_LONG).show();
	        mmOutputStream.flush();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
