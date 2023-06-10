package com.example.demoapi;

import android.app.DownloadManager;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.demoapi.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnGuardar;
    private Button btnBuscar;
    private Button btnActualizar;
    private Button btnEliminar;
    private EditText etCodigoBarras;
    private EditText etDescripcion;
    private EditText etMarca;
    private EditText etPrecioCompra;
    private EditText etPrecioVenta;
    private EditText etExixtencias;
    private ListView lvProductos;
    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private String url= "http://10.10.62.17:3300/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGuardar=findViewById(R.id.btnGuardar);
        btnBuscar=findViewById(R.id.btnBuscar);
        btnActualizar=findViewById(R.id.btnActualizar);
        btnEliminar=findViewById(R.id.btnEliminar);

        etCodigoBarras=findViewById(R.id.etCodigoBarras);
        etDescripcion=findViewById(R.id.etDescripcion);
        etMarca=findViewById(R.id.etMarca);
        etPrecioCompra=findViewById(R.id.etPrecioCompra);
        etPrecioVenta=findViewById(R.id.etPrecioVenta);
        etExixtencias=findViewById(R.id.etExistencias);

        lvProductos=findViewById(R.id.lvProductos);

        colaPeticiones= Volley.newRequestQueue( this);
        listarProductos();

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion= new JsonObjectRequest(
                        Request.Method.GET,
                        url + "/" + etCodigoBarras.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                else{
                                    try {
                                        etDescripcion.setText(response.getString("descripcion"));
                                        etMarca.setText(response.getString("marca"));
                                        etPrecioCompra.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioVenta.setText(String.valueOf(response.getInt("precioventa")));
                                        etExixtencias.setText(String.valueOf(response.getInt("existencias")));
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("codogobarras",etCodigoBarras.getText().toString());
                    producto.put("descripcion",etDescripcion.getText().toString());
                    producto.put("marca",etMarca.getText().toString());
                    producto.put("preciocompra",Float.parseFloat(etPrecioCompra.getText().toString()));
                    producto.put("precioventa",Float.parseFloat(etPrecioVenta.getText().toString()));
                    producto.put("existencias",Integer.parseInt(etExixtencias.getText().toString()));
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(
                        Request.Method.POST,
                        url + "insert/",
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Producto Insertado"))
                                        Toast.makeText(MainActivity.this, "Producto insertado con éxito", Toast.LENGTH_SHORT).show();
                                        etCodigoBarras.setText("");
                                        etDescripcion.setText("");
                                        etMarca.setText("");
                                        etPrecioCompra.setText("");
                                        etPrecioVenta.setText("");
                                        etExixtencias.setText("");
                                        adapter.clear();
                                        lvProductos.setAdapter(adapter);
                                        listarProductos();
                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCodigoBarras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese el código de barras", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url + "/borrar/" + etCodigoBarras.getText().toString(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Producto eliminado")) {
                                            Toast.makeText(MainActivity.this, "Producto exitosamente eliminado", Toast.LENGTH_SHORT).show();
                                            etCodigoBarras.setText("");
                                            etDescripcion.setText("");
                                            etMarca.setText("");
                                            etPrecioCompra.setText("");
                                            etPrecioVenta.setText("");
                                            etExixtencias.setText("");
                                            adapter.clear();
                                            lvProductos.setAdapter(adapter);
                                            listarProductos();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(jsonObjectRequest);
                }
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCodigoBarras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Seleccione un articulo", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject productos = new JSONObject();
                    try {
                        productos.put("codigobarras", etCodigoBarras.getText().toString());
                        if (!etDescripcion.getText().toString().isEmpty()) {
                            productos.put("descripcion", etDescripcion.getText().toString());
                        }
                        if (!etMarca.getText().toString().isEmpty()) {
                            productos.put("marca", etMarca.getText().toString());
                        }
                        if (!etPrecioCompra.getText().toString().isEmpty()) {
                            productos.put("preciocompra", Float.parseFloat(etPrecioCompra.getText().toString()));
                        }
                        if (!etPrecioVenta.getText().toString().isEmpty()) {
                            productos.put("precioventa", Float.parseFloat(etPrecioVenta.getText().toString()));
                        }
                        if (!etExixtencias.getText().toString().isEmpty()) {
                            productos.put("existencias", Float.parseFloat(etExixtencias.getText().toString()));
                        }
                    } catch (JSONException e) {

                    }
                }
            }
        });

    }

    protected void listarProductos(){
        jsonArrayRequest= new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i=0; i< response.length();i++) {
                            try{
                                String codigobarras=response.getJSONObject(i).getString("codigobarras");
                                String descripcion= response.getJSONObject(i).getString("descripcion");
                                String marca= response.getJSONObject(i).getString("marca");
                                origenDatos.add(codigobarras+" - "+descripcion+" - "+marca);
                            }catch (JSONException e){

                            }
                        }
                        adapter=new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, origenDatos);
                        lvProductos.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}

