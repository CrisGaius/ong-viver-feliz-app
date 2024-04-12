package com.cristiano.ongviverfeliz

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cristiano.ongviverfeliz.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class HomeActivity : AppCompatActivity() {

    private val auth by lazy{
        FirebaseAuth.getInstance()
    }
    private val binding by lazy{
        ActivityHomeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogout.setOnClickListener {
            deslogarUsuario()
        }
        binding.btnCadastrarVoluntario.setOnClickListener {
            startActivity(Intent(this, CadastrarVoluntarioActivity::class.java))
        }
        binding.btnListarVoluntario.setOnClickListener {
            startActivity(Intent(this, ListaVoluntarioActivity::class.java))
        }
        binding.btnCadastrarPessoaCarente.setOnClickListener {
            startActivity(Intent(this, CadastrarPessoaCarenteActivity::class.java))
        }
        binding.btnListarPessoaCarente.setOnClickListener {
            startActivity(Intent(this, ListaPessoasCarentesActivity::class.java))
        }
        binding.btnAdicionarContratos.setOnClickListener {
            startActivity(Intent(this, AdicionarContratoActivity::class.java))
        }
        binding.btnListarContratos.setOnClickListener {
            startActivity(Intent(this, ListaContratosActivity::class.java))
        }
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setTitle("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar"){dialog, posicao -> }
            .setPositiveButton("Sim"){dialog, posicao ->
                auth.signOut()
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }.create()
            .show()
    }
}