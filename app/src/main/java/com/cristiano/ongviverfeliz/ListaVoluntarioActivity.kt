package com.cristiano.ongviverfeliz

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
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
    private lateinit var botaoPesquisar: ImageButton
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

        botaoPesquisar = binding.btnPesquisarVoluntario

        pesquisarDados()
    }

    private fun pesquisarDados() {
        botaoPesquisar.setOnClickListener {
            val textoPesquisa = binding.editPesquisarVoluntario.text.toString().trim()

            val referenciaColecao = bancoDados.collection("Voluntários")

            if (textoPesquisa.isEmpty()) {
                // Se o campo de pesquisa estiver vazio, mostrar todos os registros
                Toast.makeText(this, "Por favor, digite algo.", Toast.LENGTH_SHORT).show()
                listarDados()
                return@setOnClickListener
            }

            val lista = mutableListOf<AtributosLista>() // Lista para armazenar os resultados das consultas

            referenciaColecao.whereGreaterThanOrEqualTo("nome", textoPesquisa)
                .whereLessThanOrEqualTo("nome", textoPesquisa + "\uf8ff")
                .get()
                .addOnSuccessListener { documents ->
                    lista.clear() // Limpar a lista antes de adicionar novos resultados
                    for (document in documents) {
                        if (document != null) {
                            val id = document.id
                            val nome = document.getString("nome").toString()
                            val listaCaminhos = mutableListOf<String>()
                            listaCaminhos.add(document.getString("caminhoAss").toString())

                            val listaUrls = mutableListOf<String>()
                            listaUrls.add(document.getString("urlImagemAss").toString())
                            lista.add(AtributosLista(id, nome, listaCaminhos, listaUrls))
                        }
                    }

                    // Se a consulta por nome retornar resultados, configurar o RecyclerView
                    // Caso contrário, realizar a consulta por CPF
                    if (lista.isNotEmpty()) {
                        configurarRecyclerView(lista)
                    } else {
                        referenciaColecao.whereEqualTo("cpf", textoPesquisa)
                            .get()
                            .addOnSuccessListener { documents ->
                                lista.clear() // Limpar a lista antes de adicionar novos resultados
                                for (document in documents) {
                                    if (document != null) {
                                        val id = document.id
                                        val nome = document.getString("nome").toString()
                                        val listaCaminhos = mutableListOf<String>()
                                        listaCaminhos.add(document.getString("caminhoAss").toString())

                                        val listaUrls = mutableListOf<String>()
                                        listaUrls.add(document.getString("urlImagemAss").toString())

                                        lista.add(AtributosLista(id, nome, listaCaminhos, listaUrls))
                                    }
                                }
                                configurarRecyclerView(lista) // Configurar o RecyclerView após o término da consulta
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Falhou ao buscar por CPF: $it", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Falhou ao buscar por nome: $it", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun listarDados() {
        val referenciaVoluntario = bancoDados
            .collection("Voluntários")

        referenciaVoluntario.addSnapshotListener { querySnapshot, erro ->
            val lista = mutableListOf<AtributosLista>()
            val listaDocuments = querySnapshot?.documents

            binding.editPesquisarVoluntario.text.clear()
            listaDocuments?.forEach { documentSnapshot ->
                val dados = documentSnapshot?.data

                if (dados != null) {
                    val id = documentSnapshot.id
                    val nome = dados["nome"].toString()
                    val listaCaminhos = mutableListOf<String>()
                    listaCaminhos.add(dados["caminhoAss"].toString())

                    val listaUrls = mutableListOf<String>()
                    listaUrls.add(dados["urlImagemAss"].toString())

                    lista.add(AtributosLista(id, nome, listaCaminhos, listaUrls))
                }
            }
            configurarRecyclerView(lista)
        }
    }

    private fun configurarRecyclerView(lista: MutableList<AtributosLista>) {
        rvLista = findViewById(R.id.rvListaVoluntario)

        rvLista.adapter = AtributosListaAdapter(lista) { id, nome, listaCaminhos, listaUrls ->
            if (nome != "" && nome != "viewImages") {
                abrirAlertDialogExclusao(id, nome, listaCaminhos)
            } else if(nome == "viewImages") {
                abrirAlertDialogViewImages(listaUrls)
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

    private fun abrirAlertDialogViewImages(listaUrls: MutableList<String>) {
        val urlAssinatura = listaUrls[0]

        val images = arrayOf("Imagem Assinatura")
        AlertDialog.Builder(this)
            .setTitle("Selecione uma imagem")
            .setItems(images) { _, which ->
                val selectedImage = images[which]

                if(selectedImage == "Imagem Assinatura") {
                    abrirNavegador(urlAssinatura)
                } else {
                    Toast.makeText(this, "Algo deu errado na seleção de imagens.", Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun abrirNavegador(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    private fun abrirAlertDialogExclusao(
        id: String,
        nome: String,
        listaCaminhos: MutableList<String>
    ) {
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