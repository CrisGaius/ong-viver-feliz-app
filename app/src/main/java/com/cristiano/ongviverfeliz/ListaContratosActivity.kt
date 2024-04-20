package com.cristiano.ongviverfeliz

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cristiano.ongviverfeliz.databinding.ActivityListaContratosBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.File

class ListaContratosActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaContratosBinding.inflate(layoutInflater)
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
    }

    private lateinit var rvLista: RecyclerView
    private lateinit var botaoPesquisar: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        listarDados()

        botaoPesquisar = binding.btnPesquisarContrato

        pesquisarDados()
    }

    private fun pesquisarDados() {
        botaoPesquisar.setOnClickListener {
            val textoPesquisa = binding.editPesquisarContrato.text.toString()

            val referenciaColecao = bancoDados.collection("contratos")

            if (textoPesquisa.isEmpty()) {
                Toast.makeText(this, "Por favor, digite algo.", Toast.LENGTH_SHORT).show()
                listarDados()
                return@setOnClickListener
            }

            val lista = mutableListOf<AtributosListaContrato>()

            referenciaColecao
                .whereGreaterThanOrEqualTo("nomeImagem", textoPesquisa)
                .whereLessThanOrEqualTo("nomeImagem", textoPesquisa + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    lista.clear()
                    for (document in documents) {
                        if (document != null) {
                            val id = document.id
                            val nomeImagem = document.getString("nomeImagem").toString()
                            val caminho = document.getString("caminhoImagem").toString()
                            val imagemUrl = document.getString("imageURL").toString()
                            lista.add(AtributosListaContrato(id, nomeImagem, caminho, imagemUrl))
                        }
                    }
                    configurarRecyclerView(lista)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falhou ao buscar pelo nome da imagem: $it", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun listarDados() {
        val referenciaContrato = bancoDados
            .collection("contratos")

        referenciaContrato.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosListaContrato>()
            val listaDocuments = querySnapshot?.documents

            binding.editPesquisarContrato.text.clear()
            listaDocuments?.forEach {documentSnapshot ->
                val dados = documentSnapshot?.data

                if (dados != null) {
                    val id = documentSnapshot.id
                    val nomeImagem = dados["nomeImagem"].toString()
                    val caminho = dados["caminhoImagem"].toString()
                    val imagemUrl = dados["imageURL"].toString()
                    lista.add(AtributosListaContrato(id, nomeImagem, caminho, imagemUrl))
                }
            }
            configurarRecyclerView(lista)
        }
    }

    private fun abrirNavegador(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun configurarRecyclerView(lista: MutableList<AtributosListaContrato>) {
        rvLista = findViewById(R.id.rvListaContratos)

        rvLista.adapter = AtributosListaContratoAdapter(lista) { id, caminho, url ->
            if (id != "") {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão?")
                    .setMessage("Tem certeza disso?")
                    .setNegativeButton("Cancelar") { dialog, posicao ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Remover") { dialog, posicao ->
                        removerContrato(id, caminho)
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            } else {
                abrirNavegador(url)
            }
        }

        rvLista.layoutManager = LinearLayoutManager(
            this,
            RecyclerView.VERTICAL,
            false
        )

    }

    private fun removerContrato(id: String, caminho: String) {
        val referenciaContrato = bancoDados
            .collection("contratos")
            .document(id)

        referenciaContrato
            .delete()
            .addOnSuccessListener {
                val referenciaImagem = storage.reference.child(caminho)

                referenciaImagem
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Contrato excluído com sucesso.", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Erro ao deletar a imagem do contrato. $exception", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener {exception ->
                Toast.makeText(this, "Erro ao deletar o contrato. $exception", Toast.LENGTH_LONG).show()
            }
    }
}