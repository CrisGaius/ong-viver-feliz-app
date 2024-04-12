package com.cristiano.ongviverfeliz

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cristiano.ongviverfeliz.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var senha: String

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val auth by lazy{
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = auth.currentUser
        if(usuarioAtual != null){
            startActivity(Intent(this, HomeActivity::class.java))
        }
    }

    private fun inicializarEventosClique() {
        binding.btnLogar.setOnClickListener {
            if ( validarCampos()){
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        auth.signInWithEmailAndPassword(email, senha).addOnSuccessListener {
            Toast.makeText(this, "Logado com sucesso!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener {erro ->
            try {
                throw erro
            }catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException){
                erroUsuarioInvalido.printStackTrace()
                Toast.makeText(this, "E-mail não cadastrado", Toast.LENGTH_LONG).show()
            }catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                erroCredenciaisInvalidas.printStackTrace()
                Toast.makeText(this, "E-mail ou senha estão incorretos!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editLoginSenha.text.toString()

        if(email.isNotEmpty()){
            binding.textInputLayoutLoginEmail.error = null
            if (senha.isNotEmpty()){
                binding.textInputLayoutLoginSenha.error = null
                return true
            }else{
                binding.textInputLayoutLoginSenha.error = "Preencha a senha"
                return false
            }
        }else{
            binding.textInputLayoutLoginEmail.error = "Preencha o email"
            return false
        }
    }
}