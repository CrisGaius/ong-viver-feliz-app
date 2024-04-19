package com.cristiano.ongviverfeliz

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cristiano.ongviverfeliz.databinding.ActivityListaVoluntarioBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ListaVoluntarioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaVoluntarioBinding.inflate(layoutInflater)
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
        val referenciaVoluntario = bancoDados
            .collection("Voluntários")

        referenciaVoluntario.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosLista>()
            val listaDocuments = querySnapshot?.documents

            listaDocuments?.forEach { documentSnapshot ->
                val dados = documentSnapshot?.data

                if (dados != null) {
                    val id = documentSnapshot.id
                    val nome = dados["nome"].toString()
                    val listaCaminhos = mutableListOf<String>()
                    listaCaminhos.add(dados["caminhoAss"].toString())
                    lista.add(AtributosLista(id, nome, listaCaminhos))
                }
            }
            configurarRecyclerView(lista)
        }
    }

    private fun configurarRecyclerView(lista: MutableList<AtributosLista>) {
        rvLista = findViewById(R.id.rvListaVoluntario)

        rvLista.adapter = AtributosListaAdapter(lista) { id, nome, listaCaminhos ->
            if (nome != "") {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão de $nome?")
                    .setMessage("Tem certeza disso?")
                    .setNegativeButton("Cancelar") { dialog, posicao ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Remover") { dialog, posicao ->
                        removerVoluntario(id, listaCaminhos)
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            } else {
                val intent = Intent(this, EditarVoluntarioActivity::class.java)
                intent.putExtra("id", id)
                Toast.makeText(this, id, Toast.LENGTH_SHORT).show()
                startActivity(intent)
            }
        }

        rvLista.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )
    }

    private fun removerVoluntario(id: String, listaCaminhos: MutableList<String>) {
        val referenciaVoluntario = bancoDados
            .collection("Voluntários")
            .document(id)

        referenciaVoluntario
            .delete()
            .addOnSuccessListener {
                val storage = FirebaseStorage.getInstance()

                val referenciaAss = storage.reference.child(listaCaminhos[0])

                referenciaAss
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Voluntário excluído como sucesso.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao excluir a imagem da assinatura. $it",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao deletar o voluntário. $it", Toast.LENGTH_LONG).show()
            }
    }
}