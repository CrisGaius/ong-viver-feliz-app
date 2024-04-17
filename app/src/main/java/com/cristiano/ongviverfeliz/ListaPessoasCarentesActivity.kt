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
import com.cristiano.ongviverfeliz.databinding.ActivityListaPessoasCarentesBinding
import com.google.firebase.firestore.FirebaseFirestore

class ListaPessoasCarentesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaPessoasCarentesBinding.inflate(layoutInflater)
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private lateinit var rvLista: RecyclerView
    //private lateinit var pessoaCarenteAdapter: PessoaCarenteAdapter

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

        /*val lista = mutableListOf(
            PessoaCarente("1", "Cristiano Gomes dos Santos"),
            PessoaCarente("2", "Pitomba José dos Santos"),
            PessoaCarente("3", "Lívia Gomes dos Santos"),
            PessoaCarente("4", "Júlia Gomes dos Santos"),
            PessoaCarente("5", "Cristiane Gomes dos Santos"),
        )*/

    }

    private fun listarDados() {
        val referenciaPessoaCarente = bancoDados
            .collection("PessoasCarentes")

        referenciaPessoaCarente.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosLista>()
            val listaDocuments = querySnapshot?.documents

            listaDocuments?.forEach { documentSnapshot ->
                val dados = documentSnapshot?.data

                if (dados != null) {
                    val id = documentSnapshot.id
                    val nome = dados["nome"].toString()
                    lista.add(AtributosLista(id,nome))
                }
            }
            configurarRecyclerView(lista)
        }

    }

    private fun configurarRecyclerView(lista: MutableList<AtributosLista>) {
        rvLista = findViewById(R.id.rvListaCarentes)

        rvLista.adapter = AtributosListaAdapter(lista) { id ->
            Toast.makeText(this, id, Toast.LENGTH_SHORT).show()

            intent.putExtra("id", id)
            startActivity(
                Intent(this, EditarPessoaCarenteActivity::class.java)
            )
        }

        rvLista.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
    }
}