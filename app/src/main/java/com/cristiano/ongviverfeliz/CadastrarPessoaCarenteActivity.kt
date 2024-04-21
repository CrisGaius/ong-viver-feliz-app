package com.cristiano.ongviverfeliz

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cristiano.ongviverfeliz.databinding.ActivityCadastrarPessoaCarenteBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CadastrarPessoaCarenteActivity : AppCompatActivity() {

    private var imageRgURL: String? = null
    private var caminhoRg: String? = null
    private var imageCPFURL: String? = null
    private var caminhoCpf: String? = null
    private var imageComprovResidURL: String? = null
    private var caminhoComprovResid: String? = null
    private var imageAssURL: String? = null
    private var caminhoAss: String? = null

    private val gerenciadorGaleriasRg = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhuma imagem de RG selecionada.", Toast.LENGTH_LONG).show()
        } else {
            uploadImagemStorage(uri, "RgPessoaCarente")
        }
    }

    private val gerenciadorGaleriasCPF = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhuma imagem de CPF selecionada.", Toast.LENGTH_LONG).show()
        } else {
            uploadImagemStorage(uri, "CpfPessoaCarente")
        }
    }

    private val gerenciadorGaleriasComprovResid = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhuma imagem de comprovante de residência selecionada.", Toast.LENGTH_LONG).show()
        } else {
            uploadImagemStorage(uri, "ComprovanteResidenciaPessoaCarente")
        }
    }

    private val gerenciadorGaleriasAss = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhuma imagem de assinatura selecionada.", Toast.LENGTH_LONG).show()
        } else {
            uploadImagemStorage(uri, "AssinaturaPessoaCarente")
        }
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

    private fun uploadImagemStorage(uri: Uri, tipoImagem: String) {
        val refStorage = FirebaseStorage.getInstance().reference
        val nomeArquivo = pegarNomeArquivo(contentResolver, uri)
        val extensao = nomeArquivo?.substringAfterLast(".", "")
        val nomeArquivoUnico = "${UUID.randomUUID().toString().substring(0,8)}.$extensao"

        val imagemRef = refStorage.child("imagensPessoasCarentes/$tipoImagem/$nomeArquivoUnico")
        val caminhoImagem = "imagensPessoasCarentes/$tipoImagem/$nomeArquivoUnico"

        nomeArquivoUnico?.let {
            imagemRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imagemRef.downloadUrl.addOnSuccessListener { url ->
                        when (tipoImagem) {
                            "RgPessoaCarente" -> {
                                imageRgURL = url.toString()
                                caminhoRg = caminhoImagem
                                binding.btnRg.text = "Imagem selecionada"
                            }
                            "CpfPessoaCarente" -> {
                                imageCPFURL = url.toString()
                                caminhoCpf = caminhoImagem
                                binding.btnCPF.text = "Imagem selecionada"
                            }
                            "ComprovanteResidenciaPessoaCarente" -> {
                                imageComprovResidURL = url.toString()
                                caminhoComprovResid = caminhoImagem
                                binding.btnComprovResid.text = "Imagem selecionada"
                            }
                            "AssinaturaPessoaCarente" -> {
                                imageAssURL = url.toString()
                                caminhoAss = caminhoImagem
                                binding.btnAss.text = "Imagem selecionada"
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
        binding.btnCadastrarPessoasCarentes.setOnClickListener {
            if( validarCampos()){
                salvarDadosPessoasCarentes(nome, dataNasc, telefone, rg, cpf, email, rua, numeroResidencia, bairro, cidade, estado)
            }
        }
        binding.btnRg.setOnClickListener {
            gerenciadorGaleriasRg.launch("image/*")
        }
        binding.btnCPF.setOnClickListener {
            gerenciadorGaleriasCPF.launch("image/*")
        }
        binding.btnComprovResid.setOnClickListener {
            gerenciadorGaleriasComprovResid.launch("image/*")
        }
        binding.btnAss.setOnClickListener {
            gerenciadorGaleriasAss.launch("image/*")
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
            "estado" to estado,
            "urlImagemRg" to imageRgURL,
            "caminhoRg" to caminhoRg,
            "urlImagemCPF" to imageCPFURL,
            "caminhoCpf" to caminhoCpf,
            "urlImagemComprovResid" to imageComprovResidURL,
            "caminhoComprovResid" to caminhoComprovResid,
            "urlImagemAss" to imageAssURL,
            "caminhoAss" to caminhoAss
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
        } else {
            return true
        }
    }
}
