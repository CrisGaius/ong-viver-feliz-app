package com.cristiano.ongviverfeliz

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        listarDados()
    }

    private fun listarDados() {
        val referenciaContrato = bancoDados
            .collection("contratos")

        referenciaContrato.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosListaContrato>()
            val listaDocuments = querySnapshot?.documents

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