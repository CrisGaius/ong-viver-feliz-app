package com.cristiano.ongviverfeliz

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cristiano.ongviverfeliz.databinding.ActivityEditarPessoaCarenteBinding
import com.cristiano.ongviverfeliz.modelo.PessoaCarente
import com.cristiano.ongviverfeliz.modelo.Voluntario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditarPessoaCarenteActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityEditarPessoaCarenteBinding.inflate(layoutInflater)
    }
    private val firestore by lazy{
        FirebaseFirestore.getInstance()
    }
    private val storage by lazy{
        FirebaseStorage.getInstance()
    }

    private var urlImagemAntigaRg: String? = null
    private var urlImagemAntigaCpf: String? = null
    private var urlImagemAntigaComprovResid: String? = null
    private var urlImagemAntigaAssinatura: String? = null

    private var uuid: String? = null

    private var urlImagemRgAtualizada: String? = null
    private var urlImagemCpfAtualizada: String? = null
    private var urlImagemComprovResidAtualizada: String? = null
    private var urlImagemAssinaturaAtualizada: String? = null

    private var caminhoRgAtualizado: String? = null
    private var caminhoCpfAtualizado: String? = null
    private var caminhoComprovResidAtualizado: String? = null
    private var caminhoAssinaturaAtualizado: String? = null

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

    private lateinit var nomeAtualizado: String
    private lateinit var dataNascAtualizado: String
    private lateinit var telefoneAtualizado: String
    private lateinit var rgAtualizado: String
    private lateinit var cpfAtualizado: String
    private lateinit var emailAtualizado: String
    private lateinit var ruaAtualizado: String
    private lateinit var numeroResidenciaAtualizado: String
    private lateinit var bairroAtualizado: String
    private lateinit var cidadeAtualizado: String
    private lateinit var estadoAtualizado: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()

        uuid = intent.getStringExtra("id")
        uuid?.let { recuperarDados(it) }
    }

    private fun uploadImagemStorage(uri: Uri, tipoImagem: String) {
        val refStorage = FirebaseStorage.getInstance().reference
        val nomeArquivo = pegarNomeArquivo(contentResolver, uri)
        val imagemRef = refStorage.child("imagensPessoasCarentes/$tipoImagem/$nomeArquivo")
        val caminhoImagem = "imagensPessoasCarentes/$tipoImagem/$nomeArquivo"

        nomeArquivo?.let {
            imagemRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imagemRef.downloadUrl.addOnSuccessListener { url ->
                        when (tipoImagem) {
                            "RgPessoaCarente" -> {
                                urlImagemRgAtualizada = url.toString()
                                caminhoRgAtualizado = caminhoImagem
                                binding.buttonCopiaRgEditar.text = "Imagem selecionada"
                                if(urlImagemAntigaRg != null){
                                    val refStorage: StorageReference = storage.getReferenceFromUrl(urlImagemAntigaRg!!)
                                    refStorage.delete().addOnSuccessListener {
                                        Log.i("info_delete", "Imagem deletada")
                                    }
                                }
                            }
                            "CpfPessoaCarente" -> {
                                urlImagemCpfAtualizada = url.toString()
                                caminhoCpfAtualizado = caminhoImagem
                                binding.buttonCopiaCpfEditar.text = "Imagem selecionada"
                                if(urlImagemAntigaCpf != null){
                                    val refStorage: StorageReference = storage.getReferenceFromUrl(urlImagemAntigaCpf!!)
                                    refStorage.delete().addOnSuccessListener {
                                        Log.i("info_delete", "Imagem deletada")
                                    }
                                }
                            }
                            "ComprovanteResidenciaPessoaCarente" -> {
                                urlImagemComprovResidAtualizada = url.toString()
                                caminhoComprovResidAtualizado = caminhoImagem
                                binding.buttonComprovanteResidenciaEditar.text = "Imagem selecionada"
                                if(urlImagemAntigaComprovResid != null){
                                    val refStorage: StorageReference = storage.getReferenceFromUrl(urlImagemAntigaComprovResid!!)
                                    refStorage.delete().addOnSuccessListener {
                                        Log.i("info_delete", "Imagem deletada")
                                    }
                                }
                            }
                            "AssinaturaPessoaCarente" -> {
                                urlImagemAssinaturaAtualizada = url.toString()
                                caminhoAssinaturaAtualizado = caminhoImagem
                                binding.buttonAssinaturaEditar.text = "Imagem selecionada"
                                if(urlImagemAntigaAssinatura != null){
                                    val refStorage: StorageReference = storage.getReferenceFromUrl(urlImagemAntigaAssinatura!!)
                                    refStorage.delete().addOnSuccessListener {
                                        Log.i("info_delete", "Imagem deletada")
                                    }
                                }
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
        binding.buttonEditar.setOnClickListener {
            if(validarCampos()){
                atualizarDados(
                    uuid!!,
                    nomeAtualizado,
                    dataNascAtualizado,
                    telefoneAtualizado,
                    rgAtualizado,
                    cpfAtualizado,
                    emailAtualizado,
                    ruaAtualizado,
                    numeroResidenciaAtualizado,
                    bairroAtualizado,
                    cidadeAtualizado,
                    estadoAtualizado
                )
            }
        }
        binding.buttonCopiaRgEditar.setOnClickListener {
            gerenciadorGaleriasRg.launch("image/*")
        }
        binding.buttonCopiaCpfEditar.setOnClickListener {
            gerenciadorGaleriasCPF.launch("image/*")
        }
        binding.buttonAssinaturaEditar.setOnClickListener {
            gerenciadorGaleriasAss.launch("image/*")
        }
        binding.buttonComprovanteResidenciaEditar.setOnClickListener {
            gerenciadorGaleriasComprovResid.launch("image/*")
        }
    }

    private fun atualizarDados(
        uuid: String,
        nomeAtualizado: String,
        dataNascAtualizado: String,
        telefoneAtualizado: String,
        rgAtualizado: String,
        cpfAtualizado: String,
        emailAtualizado: String,
        ruaAtualizado: String,
        numeroResidenciaAtualizado: String,
        bairroAtualizado: String,
        cidadeAtualizado: String,
        estadoAtualizado: String
    ) {

        val pessoaCarenteAtualizada = mapOf(
            "nome" to nomeAtualizado,
            "dataNasc" to dataNascAtualizado,
            "telefone" to telefoneAtualizado,
            "rg" to rgAtualizado,
            "cpf" to cpfAtualizado,
            "email" to emailAtualizado,
            "rua" to ruaAtualizado,
            "numeroResidencia" to numeroResidenciaAtualizado,
            "bairro" to bairroAtualizado,
            "cidade" to cidadeAtualizado,
            "estado" to estadoAtualizado,
            "urlImagemRg" to urlImagemRgAtualizada,
            "caminhoRg" to caminhoRgAtualizado,
            "urlImagemCPF" to urlImagemCpfAtualizada,
            "caminhoCpf" to caminhoCpfAtualizado,
            "urlImagemComprovResid" to urlImagemComprovResidAtualizada,
            "caminhoComprovResid" to caminhoComprovResidAtualizado,
            "urlImagemAss" to urlImagemAssinaturaAtualizada,
            "caminhoAss" to caminhoAssinaturaAtualizado
        )
        firestore.collection("PessoasCarentes").document(uuid).update(pessoaCarenteAtualizada).addOnSuccessListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }.addOnFailureListener {e ->
            e.printStackTrace()
        }
    }

    private fun recuperarDados(uuid: String?) {
        if (uuid != null) {
            firestore.collection("PessoasCarentes").document(uuid).get().addOnSuccessListener { dados ->
                if(dados != null){
                    urlImagemAntigaAssinatura = dados.getString("urlImagemAss")
                    urlImagemAntigaCpf = dados.getString("urlImagemCPF")
                    urlImagemAntigaRg = dados.getString("urlImagemRg")
                    urlImagemAntigaComprovResid = dados.getString("urlImagemComprovResid")

                    val pessoaCarente = dados.toObject<PessoaCarente>()
                    preencherDados(pessoaCarente)
                }
            }.addOnFailureListener {e ->
                e.printStackTrace()
            }
        }
    }

    private fun preencherDados(pessoaCarente: PessoaCarente?) {
        binding.editTextNomeEditarPessoa.setText(pessoaCarente?.nome ?: "")
        binding.editTextDataNascEditarPessoa.setText(pessoaCarente?.dataNasc ?: "")
        binding.editTextTelefoneEditarPessoa.setText(pessoaCarente?.telefone ?: "")
        binding.editTextRgEditarPessoa.setText(pessoaCarente?.rg ?: "")
        binding.editTextCpfEditarPessoa.setText(pessoaCarente?.cpf ?: "")
        binding.editTextEmailEditarPessoa.setText(pessoaCarente?.email ?: "")
        binding.editTextRuaEditarPessoa.setText(pessoaCarente?.rua ?: "")
        binding.editTextNumResidEditarPessoa.setText(pessoaCarente?.numeroResidencia ?: "")
        binding.editTextBairroEditarPessoa.setText(pessoaCarente?.bairro ?: "")
        binding.editTextCidadeEditarPessoa.setText(pessoaCarente?.cidade ?: "")
        binding.editTextEstadoEditarPessoa.setText(pessoaCarente?.estado ?: "")


    }


    private fun validarCampos(): Boolean {
        nomeAtualizado = binding.editTextNomeEditarPessoa.text.toString()
        dataNascAtualizado = binding.editTextDataNascEditarPessoa.text.toString()
        telefoneAtualizado = binding.editTextTelefoneEditarPessoa.text.toString()
        rgAtualizado = binding.editTextRgEditarPessoa.text.toString()
        cpfAtualizado = binding.editTextCpfEditarPessoa.text.toString()
        emailAtualizado = binding.editTextEmailEditarPessoa.text.toString()
        ruaAtualizado = binding.editTextRuaEditarPessoa.text.toString()
        numeroResidenciaAtualizado = binding.editTextNumResidEditarPessoa.text.toString()
        bairroAtualizado = binding.editTextBairroEditarPessoa.text.toString()
        cidadeAtualizado = binding.editTextCidadeEditarPessoa.text.toString()
        estadoAtualizado = binding.editTextEstadoEditarPessoa.text.toString()

        if (nomeAtualizado.isEmpty() || dataNascAtualizado.isEmpty() || telefoneAtualizado.isEmpty() || rgAtualizado.isEmpty() ||
            cpfAtualizado.isEmpty() || emailAtualizado.isEmpty() || ruaAtualizado.isEmpty() || numeroResidenciaAtualizado.isEmpty() ||
            bairroAtualizado.isEmpty() || cidadeAtualizado.isEmpty() || estadoAtualizado.isEmpty() ) {

            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

}