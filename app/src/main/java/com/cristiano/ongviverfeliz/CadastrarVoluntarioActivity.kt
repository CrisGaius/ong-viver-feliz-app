package com.cristiano.ongviverfeliz

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.cristiano.ongviverfeliz.databinding.ActivityCadastrarPessoaCarenteBinding
import com.cristiano.ongviverfeliz.databinding.ActivityCadastrarVoluntarioBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CadastrarVoluntarioActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityCadastrarVoluntarioBinding.inflate(layoutInflater)
    }

    private var imageAssinaturaVoluntarioURL: String? = null

    private val gerenciadorGaleriasAssinaturaVoluntario = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhuma assinatura selecionada.", Toast.LENGTH_LONG).show()
        } else {
            uploadImagemStorage(uri, "AssinaturaVol")
        }
    }

    private val firestore by lazy{
        FirebaseFirestore.getInstance()
    }

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
    private lateinit var atvVoluntaria: String
    private lateinit var diaSemana: String
    private lateinit var formaTrabalho: String
    private lateinit var horario: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()

    }

    private fun uploadImagemStorage(uri: Uri, tipoImagem: String) {
        val refStorage = FirebaseStorage.getInstance().reference
        val nomeArquivo = pegarNomeArquivo(contentResolver, uri)
        val imagemRef = refStorage.child("Imagens/$tipoImagem/$nomeArquivo")

        nomeArquivo?.let {
            imagemRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imagemRef.downloadUrl.addOnSuccessListener { url ->
                        when (tipoImagem) {
                            "AssinaturaVol" -> {
                                imageAssinaturaVoluntarioURL = url.toString()
                                binding.btnAssinaturaVoluntario.text = "Imagem selecionada"
                            }
                        }
                        Toast.makeText(this, "Imagem carregada com sucesso!", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Falha ao obter URL da imagem.", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Falha ao carregar imagem.", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }
        } ?: run {
            Toast.makeText(this, "Falha ao obter nome do arquivo.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pegarNomeArquivo(contentResolver: ContentResolver, uri: Uri): String?{
        var fileName: String? = null
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = cursor.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }

    private fun inicializarEventosClique() {
        binding.btnCadastrarVoluntario.setOnClickListener {
            if( validarCampos()){
                salvarDadosPessoasCarentes(nome, dataNasc, telefone, rg, cpf, email, rua, numeroResidencia, bairro, cidade, atvVoluntaria, diaSemana, estado, formaTrabalho, horario)
            }
        }
        binding.btnAssinaturaVoluntario.setOnClickListener {
            gerenciadorGaleriasAssinaturaVoluntario.launch("image/*")
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
        estado: String,
        atividadeVoluntaria: String,
        horario: String,
        diaSemana: String,
        formaTrabalho: String
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
            "estado" to estado,
            "urlImagemAss" to imageAssinaturaVoluntarioURL,
            "atividadeVoluntaria" to atividadeVoluntaria,
            "horario" to horario,
            "diaSemana" to diaSemana,
            "formaTrabalho" to formaTrabalho

        )
        firestore.collection("Voluntários").document(uuid).set(pessoaCarente).addOnSuccessListener {
            Toast.makeText(this, "Voluntário cadastrado com sucesso!", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener {e ->
            e.printStackTrace()
            Toast.makeText(this, "Erro ao cadastrar voluntário.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validarCampos(): Boolean {
        nome = binding.editTextNomeVoluntario.text.toString()
        dataNasc = binding.editTextDataNascVoluntario.text.toString()
        telefone = binding.editTextTelefoneVoluntario.text.toString()
        rg = binding.editTextRGVoluntario.text.toString()
        cpf = binding.editTextCPFVoluntario.text.toString()
        email = binding.editTextEmailVoluntario.text.toString()
        rua = binding.editTextRuaVoluntario.text.toString()
        numeroResidencia = binding.editTextNumResidVoluntario.text.toString()
        bairro = binding.editTextBairroVoluntario.text.toString()
        cidade = binding.editTextCidadeVoluntario.text.toString()
        estado = binding.editTextEstadoVoluntario.text.toString()
        atvVoluntaria = binding.editTextAtividadeVVoluntario.text.toString()
        diaSemana = binding.editTextDiasSemanaVoluntario.text.toString()
        formaTrabalho = binding.editTextFormaTrabalhoVoluntario.text.toString()
        horario = binding.editTextHorarioVoluntario.text.toString()

        if (nome.isEmpty() || dataNasc.isEmpty() || telefone.isEmpty() || rg.isEmpty() ||
            cpf.isEmpty() || email.isEmpty() || rua.isEmpty() || numeroResidencia.isEmpty() ||
            bairro.isEmpty() || cidade.isEmpty() || estado.isEmpty() || atvVoluntaria.isEmpty() ||
            diaSemana.isEmpty() || formaTrabalho.isEmpty() || horario.isEmpty() ) {

            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }
}