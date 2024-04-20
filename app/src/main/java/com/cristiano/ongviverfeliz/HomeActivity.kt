package com.cristiano.ongviverfeliz

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cristiano.ongviverfeliz.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class HomeActivity : AppCompatActivity() {

    var temPermissaoGaleria = false

    private val auth by lazy{
        FirebaseAuth.getInstance()
    }
    private val binding by lazy{
        ActivityHomeBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val idUsuarioLogado = auth.currentUser?.uid
        if(idUsuarioLogado != null) {
            setContentView(binding.root)
            inicializarEventosClique()
            solicitarPermissoes()
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            Toast.makeText(this, "Você deve estar logado, primeiro.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inicializarEventosClique() {
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

    private fun solicitarPermissoes() {
        temPermissaoGaleria = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED

        val listaPermissoesNegadas = mutableListOf<String>()

        if(!temPermissaoGaleria ){
            listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (listaPermissoesNegadas.isNotEmpty()){
            val gerenciarPermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ){permissoes ->
                temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria
            }
            gerenciarPermissoes.launch(listaPermissoesNegadas.toTypedArray())
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