package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapp.Fragment.Fragment_Home;
import com.example.myapp.Fragment.Fragment_Profile;
import com.example.myapp.Model.Cart;
import com.example.myapp.Model.Product;
import com.example.myapp.Model.Service;
import com.example.myapp.Model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private ImageView img_cart;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout home_layout;
    private TextView title;
    public static User user;
    public static List<Product> productList;
    public static List<Service> listService;
    public static List<Cart> list;
    public static List<User> userList;
    private static void onClick(View v) {
        switch (v.getId()){
            case R.id.img_cart:
                Intent intent = new Intent(v.getContext(), CartActivity.class );
                v.getContext().startActivity(intent);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initUI();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("user");
        list = new ArrayList<>();
        userList = new ArrayList<>();
        GetListProduct();
        getDataService();
        getUserData();
        img_cart.setOnClickListener(HomeActivity::onClick);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.home_layout, new Fragment_Home());
        fragmentTransaction.commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.item_home:
                        title.setText("Trang chủ");
                        fragment = new Fragment_Home();
                        break;
                    case R.id.item_profile:
                        title.setText("Cá nhân");
                        fragment = new Fragment_Profile();
                        break;
                    default:
                        fragment = new Fragment_Home();
                }
                transaction.replace(R.id.home_layout, fragment);
                transaction.commit();
                return true;
            }
        });
    }




    private void initUI() {
        title = findViewById(R.id.tv_title_home);
        img_cart = findViewById(R.id.img_cart);
        bottomNavigationView = findViewById(R.id.nav_home);
        home_layout = findViewById(R.id.home_layout);
    }
    private void GetListProduct(){
        productList = new ArrayList<>();
        productList.clear();
        String url = "https://cnpm-web-be.herokuapp.com/api/product";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        productList.add(new Product(object.getString("idProduct"),
                                object.getString("name"),
                                object.getString("type"),
                                object.getInt("price"),
                                object.getInt("promoPrice"),
                                object.getInt("amount"),
                                object.getString("image"),
                                object.getString("description")
                                ));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }


    public void getDataService(){
        listService = new ArrayList<>();
        listService.clear();
        String url = "https://cnpm-web-be.herokuapp.com/api/service";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        listService.add(new Service(object.getString("idService"),
                                object.getString("name"),
                                object.getInt("price"),
                                object.getInt("promoPrice"),
                                object.getString("address"),
                                object.getString("description"),
                                object.getString("image"),
                                object.getString("supplier")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    private void getCart(){
        LoginActivity.cartList.clear();
        String url = "https://cnpm-web-be.herokuapp.com/api/cart" ;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        Cart cart = new Cart(object.getString("idCustomer"),
                                object.getString("idProduct"),
                                object.getString("amount"),
                                object.getString("id"));
                        LoginActivity.cartList.add(cart);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(jsonArrayRequest);
    }
    public void getUserData(){
        userList.clear();

        String url = "https://cnpm-web-be.herokuapp.com/api/customer";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        userList.add(new User(object.getString("idCustomer"),
                                object.getString("phone"),
                                "",object.getString("name"),
                                object.getString("email"),
                                object.getString("address"),
                                "",""
                                ));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);

    }
}