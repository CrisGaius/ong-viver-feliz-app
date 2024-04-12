package com.cristiano.ongviverfeliz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cristiano.ongviverfeliz.databinding.ActivityCadastrarPessoaCarenteBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class CadastrarPessoaCarenteActivity : AppCompatActivity() {

    private lateinit var nome: String
    private lateinit var dataNasc: String
    private lateinit var telefone: String
    private lateinit var rg: String
    private lateinit var cpf: String
    private lateinit var email: String
    private lateinit var rua: String
    private lateinit var numeroResidencia: String
    private lateinit var bairro: String
    private lateinit var cidade: String
    private lateinit var estado: String

    private val binding by lazy{
        ActivityCadastrarPessoaCarenteBinding.inflate(layoutInflater)
    }
    private val firestore by lazy{
        FirebaseFirestore.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
    }

    private fun inicializarEventosClique() {
        binding.btnCadastrarPessoasCarentes.setOnClickListener {
            if( validarCampos()){
                salvarDadosPessoasCarentes(nome, dataNasc, telefone, rg, cpf, email, rua, numeroResidencia, bairro, cidade, estado)
            }
        }
    }

    private fun salvarDadosPessoasCarentes(
        nome: String,
        dataNasc: String,
        telefone: String,
        rg: String,
        cpf: String,
        email: String,
        rua: String,
        numeroResidencia: String,
        bairro: String,
        cidade: String,
        estado: String
    ) {
        val uuid = UUID.randomUUID().toString()

        val pessoaCarente = mapOf(
            "nome" to nome,
            "dataNasc" to dataNasc,
            "telefone" to telefone,
            "rg" to rg,
            "cpf" to cpf,
            "email" to email,
            "rua" to rua,
            "numeroResidencia" to numeroResidencia,
            "bairro" to bairro,
            "cidade" to cidade,
            "estado" to estado
        )
        firestore.collection("PessoasCarentes").document(uuid).set(pessoaCarente).addOnSuccessListener {
            Toast.makeText(this, "Pessoa cadastrada com sucesso!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener {e ->
            e.printStackTrace()
            Toast.makeText(this, "Erro ao cadastrar pessoa carente.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarCampos(): Boolean {
        nome = binding.editTextNomePessoaCarente.text.toString()
        dataNasc = binding.editTextDataNascPessoaCarente.text.toString()
        telefone = binding.editTextTelefonePessoaCarente.text.toString()
        rg = binding.editTextRgPessoaCarente.text.toString()
        cpf = binding.editTextCpfPessoaCarente.text.toString()
        email = binding.editTextEmailPessoaCarente.text.toString()
        rua = binding.editTextRuaPessoaCarente.text.toString()
        numeroResidencia = binding.editTextNumResidPessoaCarente.text.toString()
        bairro = binding.editTextBairroPessoaCarente.text.toString()
        cidade = binding.editTextCidadePessoaCarente.text.toString()
        estado = binding.editTextEstadoPessoaCarente.text.toString()

        if (nome.isEmpty() || dataNasc.isEmpty() || telefone.isEmpty() || rg.isEmpty() ||
            cpf.isEmpty() || email.isEmpty() || rua.isEmpty() || numeroResidencia.isEmpty() ||
            bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty()) {

            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            return false
        }else{
            return true
        }
    }


}