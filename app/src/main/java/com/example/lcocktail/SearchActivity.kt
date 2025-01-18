package com.example.lcocktail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lcocktail.Adapters.CocktailAdapter
import com.example.lcocktail.ApiService.CocktailApi
import com.example.lcocktail.ApiService.RetrofitClient
import com.example.lcocktail.Model.Cocktail
import com.example.lcocktail.Model.CocktailResponse
import androidx.appcompat.widget.SearchView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize RecyclerView and SearchView
        searchRecyclerView = findViewById(R.id.searchRecyclerView)
        searchView = findViewById(R.id.searchView)

        searchRecyclerView.layoutManager = GridLayoutManager(this, 3)

        // Fetch all cocktails initially
        fetchAllCocktails("")

        // Setup search filtering
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { fetchAllCocktails(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { fetchAllCocktails(it) }
                return true
            }
        })
    }

    private fun fetchAllCocktails(query: String) {
        val api = RetrofitClient.instance.create(CocktailApi::class.java)
        api.searchCocktailsByName(query).enqueue(object : Callback<CocktailResponse> {
            override fun onResponse(call: Call<CocktailResponse>, response: Response<CocktailResponse>) {
                if (response.isSuccessful) {
                    val cocktails = response.body()?.drinks ?: emptyList()
                    searchRecyclerView.adapter = CocktailAdapter(this@SearchActivity, cocktails) { cocktail ->
                        openCocktailDetails(cocktail)
                    }
                } else {
                    showToast("Failed to fetch cocktails.")
                }
            }

            override fun onFailure(call: Call<CocktailResponse>, t: Throwable) {
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun openCocktailDetails(cocktail: Cocktail) {
        val intent = Intent(this, CocktailDetailActivity::class.java)
        intent.putExtra("idDrink", cocktail.idDrink)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
