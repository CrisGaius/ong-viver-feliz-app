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
import com.cristiano.ongviverfeliz.databinding.ActivityListaPessoasCarentesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class   ListaPessoasCarentesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityListaPessoasCarentesBinding.inflate(layoutInflater)
    }

    private val bancoDados by lazy {
        FirebaseFirestore.getInstance()
    }

    private val storage by lazy {
        FirebaseStorage.getInstance()
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
                    val listaCaminhos = mutableListOf<String>()
                    listaCaminhos.add(dados["caminhoAss"].toString())
                    listaCaminhos.add(dados["caminhoComprovResid"].toString())
                    listaCaminhos.add(dados["caminhoCpf"].toString())
                    listaCaminhos.add(dados["caminhoRg"].toString())

                    lista.add(AtributosLista(id,nome, listaCaminhos))
                }
            }
            configurarRecyclerView(lista)
        }

    }

    private fun configurarRecyclerView(lista: MutableList<AtributosLista>) {
        rvLista = findViewById(R.id.rvListaCarentes)

        rvLista.adapter = AtributosListaAdapter(lista) { id, nome, listaCaminhos ->

            if (nome != "") {
                AlertDialog.Builder(this)
                    .setTitle("Confirmar exclusão de $nome?")
                    .setMessage("Tem certeza disso?")
                    .setNegativeButton("Cancelar") { dialog, posicao ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Remover") { dialog, posicao ->
                        removerPessoaCarente(id, listaCaminhos)
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            } else {
                val intent = Intent(this, EditarPessoaCarenteActivity::class.java)
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

    private fun removerPessoaCarente(id: String, listaCaminhos: MutableList<String>) {
        val referenciaPessoaCarente = bancoDados
            .collection("PessoasCarentes")
            .document(id)

        referenciaPessoaCarente
            .delete()
            .addOnSuccessListener {
                removerImagens(listaCaminhos)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao deletar a pessoa carente. $it", Toast.LENGTH_LONG).show()
            }
    }

    private fun removerImagens(listaCaminhos: MutableList<String>) {
        val referenciaAss = storage.reference.child(listaCaminhos[0])
        val referenciaComprovResid = storage.reference.child(listaCaminhos[1])
        val referenciaCpf = storage.reference.child(listaCaminhos[2])
        val referenciaRg = storage.reference.child(listaCaminhos[3])

        referenciaAss
            .delete()
            .addOnSuccessListener {
                referenciaComprovResid
                    .delete()
                    .addOnSuccessListener {
                        referenciaCpf
                            .delete()
                            .addOnSuccessListener {
                                referenciaRg
                                    .delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Pessoa carente excluída com sucesso.", Toast.LENGTH_LONG).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Erro ao excluir a imagem do Rg. $it",
                                            Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao excluir a imagem do cpf. $it",
                                    Toast.LENGTH_SHORT)
                                    .show()
                            }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao excluir a imagem do comprovante de residência. $it",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao excluir a imagem da assinatura. $it", Toast.LENGTH_SHORT).show()
            }
    }
}