package com.cristiano.ongviverfeliz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cristiano.ongviverfeliz.databinding.ActivityListaContratosBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListaContratosActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaContratosBinding.inflate(layoutInflater)
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var rvLista: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        listarDados()
    }

    private fun listarDados() {
        val referenciaContrato = bancoDados
            .collection("contratos")

        referenciaContrato.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosLista>()
            val listaDocuments = querySnapshot?.documents

            listaDocuments?.forEach {documentSnapshot ->
                val dados = documentSnapshot?.data

                if (dados != null) {
                    val id = documentSnapshot.id
                    val imageUrl = dados["imageURL"].toString()
                    lista.add(AtributosLista(id, imageUrl))
                }
            }
            configurarRecyclerView(lista)
        }
    }

    private fun configurarRecyclerView(lista: MutableList<AtributosLista>) {
        rvLista = findViewById(R.id.rvListaContratos)

        rvLista.adapter = AtributosListaContratoAdapter(lista) { id ->
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

            intent.putExtra("id", id)
            startActivity(Intent(this, AdicionarContratoActivity::class.java))
        }

        rvLista.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )

    }
}