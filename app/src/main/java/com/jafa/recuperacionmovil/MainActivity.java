package com.jafa.recuperacionmovil;

import androidx.appcompat.app.AppCompatActivity;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView datos1, datos2, datos3;
    Button btAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        datos1 = findViewById(R.id.lbldatoUno);
        datos2 = findViewById(R.id.lbldatoDos);
        datos3 = findViewById(R.id.lbldatoTres);
        btAceptar = findViewById(R.id.btnObtener);

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWS();
            }
        });
    }

    private void loginWS() {
        String url = "https://api.openweathermap.org/data/2.5/forecast?lat=-32.91711542962828&lon=-60.779945923290136&appid=bd5e378503939ddaee76f12ad7a97608";

        StringRequest postRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray valorlista = jsonResponse.getJSONArray("list");

                    if (valorlista.length() >= 5) {
                        JSONObject datoclima = valorlista.getJSONObject(4);
                        int clouds = datoclima.getJSONObject("clouds").getInt("all");
                        double windSpeed = datoclima.getJSONObject("wind").getDouble("speed");
                        int visibility = datoclima.getInt("visibility");

                        mostrarDatos(String.valueOf(clouds), String.valueOf(windSpeed), String.valueOf(visibility));
                    } else {

                        Toast.makeText(MainActivity.this, "Error: Datos insuficientes", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    Toast.makeText(MainActivity.this, "Error: Fallo al procesar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.getMessage());

                Toast.makeText(MainActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

        Volley.newRequestQueue(this).add(postRequest);
    }


    private void mostrarDatos(String dato1, String dato2, String dato3) {
        BDHelper admin = new BDHelper(this, "BDDRecuperacion.db", null, 1);
        SQLiteDatabase bd = admin.getWritableDatabase();

        ContentValues datosRegistrar = new ContentValues();
        datosRegistrar.put("dat_clouds", dato1);
        datosRegistrar.put("dat_wind", dato2);
        datosRegistrar.put("dat_visibility", dato3);


        bd.insert("BDDRecuperacion", null, datosRegistrar);


        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();


        datos1.setText(dato1);
        datos2.setText(dato2);
        datos3.setText(dato3);
    }
}