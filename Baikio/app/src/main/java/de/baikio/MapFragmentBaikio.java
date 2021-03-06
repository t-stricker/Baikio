package de.baikio;



import android.content.Intent;
import android.net.Uri;


import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.ViewGroup;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;


import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.*;
import java.text.DateFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragmentBaikio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragmentBaikio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragmentBaikio extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private View mViewMap = null;
    private FloatingActionButton fab;
    ListView classListView = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GoogleApiClient gac;
    private Location miLocacion;
    private Button boton;
    private EditText direccion;
    private String tiempo;
    private LocationRequest mLocationRequest;
    private double latitud, longitud, latitudPedida,longitudPedida;
    private boolean encontraL,pidiendoUpdateLocacion;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean createApiClient = false;
    private boolean locationRequest = false;

    private boolean onMap = false;
    private MapFragment mapfrag = null;

    private Firebase myFirebaseRef;
    private Object data;

    private List<report> reports = new ArrayList<>();
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragmentBaikio newInstance(String param1, String param2) {
        MapFragmentBaikio fragment = new MapFragmentBaikio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MapFragmentBaikio() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        myFirebaseRef = new Firebase("https://baikio.firebaseio.com/reports");

/*
        if(!createApiClient){this.crearGoogleApiClient();}
        if(!locationRequest){this.createLocationRequest();}

*/


        this.crearGoogleApiClient();
        this.createLocationRequest();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
/*
        if(mViewMap == null) {
            mViewMap = inflater.inflate(R.layout.fragment_maps,
                    container, false);
        }
*/
        onMap = true;
        mViewMap = inflater.inflate(R.layout.fragment_maps,
                container, false);

        final View actionB = mViewMap.findViewById(R.id.action_b);

        FloatingActionButton actionC = new FloatingActionButton(getActivity());
        actionC.setTitle("Hide/Show Action above");
        actionC.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) mViewMap.findViewById(R.id.multiple_actions);
        menuMultipleActions.addButton(actionC);

        /*
        final FloatingActionButton removeAction = (FloatingActionButton) mView.findViewById(R.id.button_remove);
        removeAction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FloatingActionsMenu) mView.findViewById(R.id.multiple_actions_down)).removeButton(removeAction);
            }
        });


        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(getResources().getColor(R.color.white));
        ((FloatingActionButton) mView.findViewById(R.id.setter_drawable)).setIconDrawable(drawable);

*/
        final FloatingActionButton actionA = (FloatingActionButton) mViewMap.findViewById(R.id.action_a);
        actionA.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                actionA.setTitle("Action A clicked");
                Log.d("INFO", "A pressed");
                Intent intent = new Intent(getActivity(), CreateReportActivity.class);
            }
        });

        // Test that FAMs containing FABs with visibility GONE do not cause crashes
       // mView.findViewById(R.id.button_gone).setVisibility(View.GONE);

        /*
        final FloatingActionButton actionEnable = (FloatingActionButton) mView.findViewById(R.id.action_enable);
        actionEnable.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                menuMultipleActions.setEnabled(!menuMultipleActions.isEnabled());
            }
        });
        */

       /*  FloatingActionsMenu rightLabels = (FloatingActionsMenu) mView.findViewById(R.id.right_labels);
        FloatingActionButton addedOnce = new FloatingActionButton(getActivity());
        addedOnce.setTitle("Added once");
        rightLabels.addButton(addedOnce);

        FloatingActionButton addedTwice = new FloatingActionButton(getActivity());
        addedTwice.setTitle("Added twice");
        rightLabels.addButton(addedTwice);
        rightLabels.removeButton(addedTwice);
        rightLabels.addButton(addedTwice);
*/

        /*
        if(mapfrag == null) {
            mapfrag = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            mapfrag.getMapAsync(this);
        }
        */

        mapfrag = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapfrag.getMapAsync(this);



        this.boton=(Button) mViewMap.findViewById(R.id.button);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCoordinates(v);
            }
        });

        this.direccion=(EditText) mViewMap.findViewById(R.id.editText);
        // Inflate the layout for this fragment

        return mViewMap;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        onMap = false;
        Log.d("destroy","view now");
        this.encontraL = false;
        MapFragment mapfrag = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        android.app.FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
        ft.remove(mapfrag);
        ft.commit();
        mMap = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    protected synchronized void crearGoogleApiClient(){
        this.gac = new GoogleApiClient.Builder(getActivity()).addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        if(this.gac!=null){
            this.gac.connect();
        }
        createApiClient = true;
    }

    public void onConnected(Bundle conexion){
        miLocacion= LocationServices.FusedLocationApi.getLastLocation(this.gac);
        onMapReady(mapfrag.getMap());
        Log.d(this.miLocacion+"", "mi locacion");
        if(this.miLocacion!=null){
            this.latitud=this.miLocacion.getLatitude();
            this.longitud=this.miLocacion.getLongitude();
            //Disable Map Toolbar:
            mMap.getUiSettings().setMapToolbarEnabled(false);

            if(mMap != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(this.latitud, this.longitud), 14));
                //mMap.addMarker(new MarkerOptions().position(new LatLng(this.latitud, this.longitud)).title("Yo"));
            }

            if(this.encontraL==false){
                this.encontraL=true;

            }


        }

    }

    protected void createLocationRequest() {
        //this.pidiendoUpdateLocacion=true;
        this.mLocationRequest = new LocationRequest();
        this.mLocationRequest.setInterval(10000);
        this.mLocationRequest.setFastestInterval(5000);
        this.mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if(pidiendoUpdateLocacion){
            this.startLocationUpdates();
        }
        locationRequest = true;
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(this.gac, mLocationRequest, this);
    }


    public void onConnectionSuspended(int x){

    }

    public void onConnectionFailed(ConnectionResult x){

    }

    public void onLocationChanged(Location l){
        this.miLocacion=l;
        this.tiempo= DateFormat.getTimeInstance().format(new Date());
        this.updateUI();
    }

    private void updateUI() {
        this.latitud=this.miLocacion.getLatitude();
        this.longitud=this.miLocacion.getLongitude();
        /*if(destinoLat==this.latitud && destinoLong==this.longitud){
            this.stopUpdates();
        }*/
    }

    public void stopUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(this.gac, this);
    }

    public void getCoordinates(View v){
        Geocoder code= new Geocoder(getActivity());
        if(code.isPresent()){
            Log.d("+++++++","esta entrando a las direccion");
        }
        try {
            Log.d("direccion", this.direccion.getText()+"");
            List<Address> direcciones = code.getFromLocationName(this.direccion.getText().toString()+",Guadalajara", 5);
            if(direcciones.size()>0) {
                Address ubicacion = direcciones.get(0);
                this.latitudPedida = ubicacion.getLatitude();
                this.longitudPedida = ubicacion.getLongitude();
                if (ubicacion != null) {
                    this.trazar();
                }
            }

        }
        catch (IOException e){
            Log.d("exception",e.toString() );
        }
    }

    public void trazar(){
        mMap.clear();
        mMap.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(this.latitud, this.longitud))
                .add(new LatLng(this.latitudPedida, this.longitudPedida)));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        fetchMarkerLocations();

    }

    public void fetchMarkerLocations() {

        // Attach an listener to read the data at our posts reference
        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if(!onMap)
                {
                    return;
                }
                System.out.println("There are " + snapshot.getChildrenCount() + " reports");

                for (DataSnapshot reportSnapshot: snapshot.getChildren()) {
                    report myReport = reportSnapshot.getValue(report.class);

                    float color;
                    String[] geo = myReport.get_Location().split("\\s+");

                    if (myReport.get_damageType().equalsIgnoreCase("High"))
                    {
                         color = BitmapDescriptorFactory.HUE_RED;
                    }
                    else if (myReport.get_damageType().equalsIgnoreCase("Medium"))
                    {
                         color = BitmapDescriptorFactory.HUE_ORANGE;
                    }
                    else if (myReport.get_damageType().equalsIgnoreCase("Low"))
                    {
                         color = BitmapDescriptorFactory.HUE_YELLOW;
                    }
                    else
                    {
                        color = BitmapDescriptorFactory.HUE_GREEN;
                    }


                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble(geo[0]), Double.parseDouble(geo[1])))
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                            .title(myReport.get_description())
                            .snippet("Created by: " + myReport.get_Title() + " Level: " + myReport.get_damageType()));
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }
}
