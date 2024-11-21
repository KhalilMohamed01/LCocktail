package com.example.lcocktail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.ApiService.CocktailAdapter
import com.example.lcocktail.ApiService.CocktailApi
import com.example.lcocktail.ApiService.RetrofitClient
import com.example.lcocktail.Model.CocktailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val api = RetrofitClient.instance.create(CocktailApi::class.java)
        api.getCocktails().enqueue(object : Callback<CocktailResponse> {
            override fun onResponse(
                call: Call<CocktailResponse>,
                response: Response<CocktailResponse>
            ) {
                if (response.isSuccessful) {
                    val cocktails = response.body()?.drinks ?: emptyList()
                    recyclerView.adapter = CocktailAdapter(cocktails)
                }
            }

            override fun onFailure(call: Call<CocktailResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
