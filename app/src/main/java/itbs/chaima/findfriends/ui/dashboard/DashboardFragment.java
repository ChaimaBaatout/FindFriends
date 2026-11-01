package itbs.chaima.findfriends.ui.dashboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import itbs.chaima.findfriends.Config;
import itbs.chaima.findfriends.Friend;
import itbs.chaima.findfriends.JSONParser;
import itbs.chaima.findfriends.MainActivity;
import itbs.chaima.findfriends.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    String num,lon,lat;
    @SuppressLint("MissingPermission")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        class Loading extends AsyncTask {  //thread

            ProgressDialog alert=new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                // cette methode s'execute dans l'UITHREAD usedr interface thread
                /** elle a accee a l'interface et de ses composant**/
                alert=new ProgressDialog(getActivity());
                alert.setTitle("Download");
                alert.setMessage("Please wait ...");


                alert.setCancelable(false);
                alert.show();

            }

            @Override
            protected Object doInBackground(Object[] objects) {
                //=> correspond a la methode run cad 2 eme thread
                /** pas d'accee a l'interface ni a ses composant**/

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                JSONParser parser=new JSONParser();
                String url="http://"+ Config.IP+"/servicephp/addfriend.php";

                HashMap<String,String>param=new HashMap<String,String>();
                param.put("nom",binding.ednomDash.getText().toString());

                param.put("num",binding.ednumDash.getText().toString());
                param.put("longitude",binding.edlongDash.getText().toString());
                param.put("latitude",binding.edlatDash.getText().toString());




                JSONObject response=parser.makeHttpRequest(url,"POST", param);

                try {
                    int success=response.getInt("success");
                   publishProgress();//appel implicite pr onprogressupdate
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Object[] values) {
                //travail au cours du thread
                if((int) values[0]==1)
                {
                    alert.setMessage("ajout avec success");
                }
                else{
                    alert.setMessage("ajout echoué");
                }
            }

            @Override
            protected void onPostExecute(Object o) {
                /** elle a accee a l'interface et de ses composant**/

                alert.dismiss();


            }
        }
        /**recuperer la derniere position connue**/
        FusedLocationProviderClient mClient= LocationServices.getFusedLocationProviderClient(DashboardFragment.this.getActivity());
        if(MainActivity.gps_permission) {
            mClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double lon = location.getLongitude();
                        double lat = location.getAltitude();

                        binding.edlongDash.setText(lon + "");
                        binding.edlatDash.setText(lat + "");
                    }
                }
            });
            /** recuperer une mis a jour de la position gps **/
            LocationRequest request = LocationRequest.create().setSmallestDisplacement(10).setInterval(60000);
            LocationCallback callback = new LocationCallback() {
                @Override
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    double lon = location.getLongitude();
                    double lat = location.getAltitude();

                    binding.edlongDash.setText(lon + "");
                    binding.edlatDash.setText(lat + "");
                }
            };

        mClient.requestLocationUpdates(request,callback,null);
        }



        binding.btnenvoieDash.setOnClickListener(view -> {
             num=binding.ednumDash.getText().toString();
             lon=binding.edlongDash.getText().toString();
             lat=binding.edlatDash.getText().toString();

            // envoie sms

            // demande permission
            if(ActivityCompat.checkSelfPermission(DashboardFragment.this.getActivity(), Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_DENIED){

                requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},1);
                //  => affiche une boite de dialog

            }else{
                envoieSms(num,lon,lat);
            }
        });

        return root;
    }

    private void requestPermissions(DashboardFragment dashboardFragment, String[] strings, int i) {
    }
    // code d'envoie d'un message !!!!!!


    void envoieSms(String num, String lon, String lat){

        SmsManager manager= SmsManager.getDefault(); //sim1
        manager.sendTextMessage(num, null,"FindFriends#"+lon+"#"+lat,null,null);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){

            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                envoieSms(num,lon,lat);
            }else{
                Toast.makeText(getActivity(), "sms position non envoyé", Toast.LENGTH_SHORT).show();
            }
        }
    }
}