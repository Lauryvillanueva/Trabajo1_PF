package com.uninorte.transdigital;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Campo1 extends AppCompatActivity implements View.OnClickListener , ActivityCompat.OnRequestPermissionsResultCallback {
    private RadioGroup rdgGrupo;
    private static final String TAG = "LogsAndroid";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1 ;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_EXTRA_COMMANDS = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 3;

    //Fecha y Hora...............................................................................
    EditText name, date, hour,otro;
    long ahora = System.currentTimeMillis();
    Date fecha = new Date(ahora);
    DateFormat df = new SimpleDateFormat("dd/MM/yy");
    String salida1 = df.format(fecha);
    String cla = "";
    Date hora = new Date(ahora);
    DateFormat dh = new SimpleDateFormat("HH:mm:ss");
    String salida2 = dh.format(hora);
    //Localizacion------------------------------------------------------------------------------
    String ubicacion = "Ubicacion";
    String latitud = "";
    String longitud = "";
    String gravedad = "";
    String choque,objeto;
    String mname="";
    String area="",sector="",zona="",diseño="",condc="";
    String id_cl="";
    TextView mensaje1;
    Spinner accidente;
    private Button mRegister;
    Intent it;

    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON parser class
    JSONParser jsonParser = new JSONParser();

    //testing on Emulator:
    private static final String REGISTER_URL = "https://transitodigital-asalcedod.c9users.io/form.php";
    //ids
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
//--------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campo1);

        //-------------------------------------------------------------------
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Campo1.this,MainActivity.class);
                startActivity(i);
            }
        });
        //----------------------------------------------------------------------



        it = new Intent(this, Cond_Vehi_Prop.class);

        rdgGrupo = (RadioGroup) findViewById(R.id.rb);
        rdgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radioButton4) {
                    Log.d(TAG, "La gravedad del accidente es con muertos");
                    gravedad = "Muertos";
                } else if (checkedId == R.id.radioButton5) {
                    Log.d(TAG, "La gravedad del accidente es con heridos");
                    gravedad = "Heridos";
                } else if (checkedId == R.id.radioButton6) {
                    Log.d(TAG, "La gravedad del accidente es con solo daños");
                    gravedad = "Solo Daños";
                }
            }

        });

        accidente=(Spinner)findViewById(R.id.acc);
        List<String> values = new ArrayList<String>();
        values.add("Seleccione...");
        values.add("Caida");
        values.add("Choque");
        values.add("Incendio");
        values.add("Volcaminto");
        values.add("Ocupante");
        values.add("Otro");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accidente.setAdapter(dataAdapter);
        accidente.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cla=parent.getItemAtPosition(position).toString();
                if(parent.getItemAtPosition(position).toString().equals("Seleccione...")){
                    cla="";
                }
                if(parent.getItemAtPosition(position).toString().equals("Otro")){
                    otro=(EditText)findViewById(R.id.otro);
                    otro.setVisibility(View.VISIBLE);
                }else{
                    otro=(EditText)findViewById(R.id.otro);
                    otro.setVisibility(View.INVISIBLE);
                }
                if(parent.getItemAtPosition(position).toString().equals("Choque")){
                    final String[] items = {"Vehiculo","Tren","Semoviente","Objeto fijo"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(Campo1.this);
                    builder.setTitle("Seleccione Choque con: ");
                    builder.setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            choque=items[item];
                            if(items[item].equals("Objeto fijo")){
                                final String[] items = {"Muro","Poste","Arbol","Baranda","Semaforo","Inmueble","Hidratante","Valla señal","Tarima, caseta, vehiculo estacionada","Otro"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(Campo1.this);
                                builder.setTitle("Objeto fijo: ");
                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int item) {
                                        objeto=items[item];
                                        if(items[item].equals("Otro")){
                                            otro=(EditText)findViewById(R.id.otro);
                                            otro.setVisibility(View.VISIBLE);
                                        }else{
                                            otro=(EditText)findViewById(R.id.otro);
                                            otro.setVisibility(View.INVISIBLE);
                                        }
                                        dialog.cancel();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cla="";
            }
        });


        //NUEVO..................................................................................
        name = (EditText) findViewById(R.id.name);
        date = (EditText) findViewById(R.id.date);
        hour = (EditText) findViewById(R.id.hour);
        date.setText(salida1);
        hour.setText(salida2);
        String nombre = name.getText().toString();
        String fecha = salida1;
        String hora = salida2;
        mRegister = (Button) findViewById(R.id.next);
        mRegister.setOnClickListener(this);
        //
        mensaje1 = (TextView) findViewById(R.id.mensaje1);

		/* Uso de la clase LocationManager para obtener la localizacion del GPS */
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Ubica Local = new Ubica();
        Local.setLocaliza(this);
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            }
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS},
                        MY_PERMISSIONS_REQUEST_ACCESS_LOCATION_EXTRA_COMMANDS);

            }
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                (LocationListener) Local);
        mensaje1.setText(ubicacion);
    }
    //-----------------------------------------

    //-----------------------------------------


    boolean validar(EditText name, String a){
        boolean sw = false;
        if(!a.equals("")){
            sw=true;
        }
        return sw;
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    ubicacion = "Mi direccion es: \n"
                            + DirCalle.getAddressLine(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public class Ubica implements LocationListener {
        Campo1 localiza;

        public Campo1 getLocaliza() {
            return localiza;
        }

        public void setLocaliza(Campo1 localiza) {
            this.localiza = localiza;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion

            latitud=""+loc.getLatitude();
            longitud=""+loc.getLongitude();
            this.localiza.setLocation(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //
        }

    }

    public boolean valid(EditText cam, String campo){
        if (TextUtils.isEmpty(campo)){
            return false;
        }else{
            return true;
        }
    }

    public void onClick(View view) {
        ArrayList<Boolean> data = new ArrayList<>();
        Accidente accidente =new Accidente();
        String mname = name.getText().toString();
        String mdate = date.getText().toString();
        String mhour = hour.getText().toString();
        data.add(validar(name,mname));
        data.add(validar(date,mdate));
        data.add(validar(hour,mhour));
        int cont=0;
        boolean sw=true;
        while(cont<data.size()){
            if(data.get(cont)==false){
                sw=false;
                break;
            }
            cont++;
        }if(sw == true && !TextUtils.isEmpty(latitud) && !TextUtils.isEmpty(longitud) && !TextUtils.isEmpty(gravedad) && !TextUtils.isEmpty(salida1) && !TextUtils.isEmpty(salida2)){
            accidente.setOt(mname);
            accidente.setLatitud(latitud);
            accidente.setLongitud(longitud);
            accidente.setUbicacion(ubicacion);
            accidente.setGravedad(gravedad);
            accidente.setR_fecha(salida1);
            accidente.setR_hora(salida2);
            accidente.setCaracteristicasl(id_cl);
            accidente.setA_fecha(mdate);
            accidente.setA_hora(mhour);
            accidente.setAccidente(cla);
            accidente.setChoque(choque);
            accidente.setObjetof(objeto);
            accidente.setCaracteristicasl(id_cl);
            accidente.save();
            /*List<ClaseAccidente> c = new Select().from(ClaseAccidente.class).queryList();
            for (ClaseAccidente ca : c) {
                Toast.makeText(this, ca.a, Toast.LENGTH_LONG).show();
            }*/

            startActivity(it);
            finish();
        }else{
            Toast.makeText(this,"Existen campos sin completar.",Toast.LENGTH_SHORT).show();
        }
    }
    /*class Addform extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Campo1.this);
            pDialog.setMessage("Saving...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            // TODO Auto-generated method stub
            // Check for success tag
            int success;
            String name = args[0];
            String date = args[1];
            String hour = args[2];

            try {
                // Building Parameters
                List params = new ArrayList();
                params.add(new BasicNameValuePair("name", name));
                params.add(new BasicNameValuePair("date", date));
                params.add(new BasicNameValuePair("hour", hour));

                Log.d("request!", "starting");

                //Posting user data to script
                JSONObject json = jsonParser.makeHttpRequest(
                        REGISTER_URL, "POST", params);

                // full json response
                Log.d("Registering attempt", json.toString());

                // json success element
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Formulario enviado!", json.toString());
                    //finish();
                    startActivity(it);
                    return json.getString(TAG_MESSAGE);
                } else {
                    Log.d("Failure!", json.getString(TAG_MESSAGE));
                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            pDialog.dismiss();
            if (file_url != null){
                Toast.makeText(Campo1.this, file_url, Toast.LENGTH_LONG).show();
            }
        }
    }*/
    //------------------------------------------------------------------------------------------------------------

    public void onClick_Informe2(View view) {
        //se va al campo 2, para continuar con el informe-- El campo 2 es Cond_Veh_Prop
        Intent i = new Intent(this, Cond_Vehi_Prop.class);
        startActivity(i);
    }

    public void onClick_Ubicacion(View view) {
        mensaje1.setText(ubicacion);
        if(!latitud.equals("") && !longitud.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Desea confirmar la ubicacion desde el Mapa?")
                    .setTitle("Advertencia")
                    .setCancelable(false)
                    .setIcon(R.drawable.location_map)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                    .setPositiveButton("Continuar",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Campo1.this, Mapa.class);
                                    i.putExtra("latitud", latitud);
                                    i.putExtra("longitud", longitud);
                                    i.putExtra("ubicacion",ubicacion);
                                    startActivityForResult(i,1);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        //MANEJO DE las imagenes
        //Intent i = new Intent(this, ReadComments.class);
        // startActivity(i);
    }

    public void onClick_lugar(View view) {
        //se va a la pestaña para las caracteristicas del lugar
        Intent i = new Intent(this, Caracteristicas_Lugar.class);
        i.putExtra("idinfo",salida1+salida2);
        startActivityForResult(i,2);
    }

    public void onClick_CaracteVias(View view) {
        //se va a la pestañana para las caracteristicas de las vias
        //Intent i = new Intent(this, Caracteristicas_Vias.class);
        Intent i = new Intent(this, Anexos.class);
        startActivityForResult(i,3);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if (resultCode==RESULT_OK){
                ubicacion=data.getStringExtra("ubicacion");
                latitud=data.getStringExtra("latitud");
                longitud=data.getStringExtra("longitud");
                mensaje1.setText(ubicacion);
            }
        }
        if(requestCode==2){
            if (resultCode==RESULT_OK){
                area=data.getStringExtra("area");
                sector=data.getStringExtra("sector");
                zona=data.getStringExtra("zona");
                diseño=data.getStringExtra("diseño");
                condc=data.getStringExtra("condicionc");
                id_cl=data.getStringExtra("idcl");
                List<Caracteristicasl> cl = new Select().from(Caracteristicasl.class).queryList();
                for (Caracteristicasl a : cl) {

                }
            }
        }
        if(requestCode==3){
            if (resultCode==RESULT_OK){
                /*ubicacion=data.getStringExtra("ubicacion");
                latitud=data.getStringExtra("latitud");
                longitud=data.getStringExtra("longitud");
                mensaje1.setText(ubicacion);*/
            }
        }
    }

}