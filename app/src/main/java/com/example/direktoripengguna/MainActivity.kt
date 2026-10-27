package com.example.direktoripengguna

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var listView: ListView
    private lateinit var btnCobaLagi: Button
    private lateinit var btnPost: Button
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        listView = findViewById(R.id.listUser)
        btnCobaLagi = findViewById(R.id.btnCobaLagi)
        btnPost = findViewById(R.id.btnPost)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        listView.adapter = adapter

        btnCobaLagi.setOnClickListener { muatUsers() }
        btnPost.setOnClickListener { kirimPostingan() }

        muatUsers()
    }

    private fun muatUsers() {
        tvStatus.text = "Memuat data..."
        btnCobaLagi.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val daftar = RetrofitClient.api.getUsers()

                adapter.clear()
                adapter.addAll(daftar.map { "${it.name} (@${it.username})" })
                tvStatus.text = "${daftar.size} pengguna dimuat"

            } catch (e: HttpException) {
                tvStatus.text = "Server error: HTTP ${e.code()}"
                btnCobaLagi.visibility = View.VISIBLE
            } catch (e: IOException) {
                tvStatus.text = "Tidak ada koneksi internet"
                btnCobaLagi.visibility = View.VISIBLE
            }
        }
    }

    private fun kirimPostingan() {
        lifecycleScope.launch {
            try {
                val baru = PostBaru(
                    title = "Belajar Retrofit",
                    body = "Contoh POST dari aplikasi",
                    userId = 1
                )
                val hasil = RetrofitClient.api.buatPost(baru)
                tvStatus.text = "Tersimpan dengan id ${hasil.id}"
            } catch (e: HttpException) {
                tvStatus.text = "Gagal menyimpan: HTTP ${e.code()}"
            } catch (e: IOException) {
                tvStatus.text = "Tidak ada koneksi internet"
            }
        }
    }
}