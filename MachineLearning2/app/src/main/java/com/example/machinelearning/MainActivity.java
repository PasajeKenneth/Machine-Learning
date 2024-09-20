package com.example.machinelearning;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText cgpa, iq, profileScore; // Changed variable name for consistency
    private Button predict;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        cgpa = findViewById(R.id.cgpa);
        iq = findViewById(R.id.iq);
        profileScore = findViewById(R.id.profile_score); // Consistent naming
        predict = findViewById(R.id.predict);
        result = findViewById(R.id.result);

        String url = "http://192.168.10.83:5000/predict";  // API endpoint

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    makePredictionRequest(url);
                }
            }
        });
    }

    private boolean validateInputs() {
        if (cgpa.getText().toString().isEmpty() || iq.getText().toString().isEmpty() || profileScore.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void makePredictionRequest(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        handleResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("cgpa", cgpa.getText().toString());
                params.put("iq", iq.getText().toString());
                params.put("profile_score", profileScore.getText().toString()); // Consistent naming
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String data = jsonObject.getString("placement");

            if ("1".equals(data)) {
                result.setText("Placement Accepted");
            } else {
                result.setText("Placement Not Accepted");
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing response", Toast.LENGTH_SHORT).show();
            Log.d("ResponseError", "JSONException: " + e.getMessage());
        }
    }
}
