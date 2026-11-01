package itbs.chaima.findfriends.ui.home;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import itbs.chaima.findfriends.Config;
import itbs.chaima.findfriends.Friend;
import itbs.chaima.findfriends.JSONParser;
import itbs.chaima.findfriends.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;



    ArrayList<Friend> data=new ArrayList<Friend>();
    ArrayAdapter ad;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        ad=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,data);
        binding.lv.setAdapter(ad);

        binding.btndownload.setOnClickListener(v->{
            Loading l=new Loading();
            l.execute();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
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
            JSONObject response=parser.makeRequest("http://"+ Config.IP+"/servicephp/getAll.php");

            try {
                int success=response.getInt("success");
                if(success==0){
                    String msg=response.getString("msg");
                    //afficher un msg d'erreur
                }else{
                    JSONArray tableau=response.getJSONArray("result");
                    data.clear();
                    for (int i=0;i<tableau.length();i++){
                        JSONObject ligne= tableau.getJSONObject(i);
                        String nom=ligne.getString("nom");
                        String numero=ligne.getString("num");
                        String longitude=ligne.getString("longitude");
                        String latitude=ligne.getString("latitude");

                        data.add(new Friend(nom,numero,longitude,latitude));



                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            /** elle a accee a l'interface et de ses composant**/
            ad.notifyDataSetChanged();
            alert.dismiss();


        }
    }
}