package com.cristiano.ongviverfeliz


import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.cristiano.ongviverfeliz.databinding.ActivityAdicionarContratoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class AdicionarContratoActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityAdicionarContratoBinding.inflate(layoutInflater)
    }

    private var imageContratoUri: Uri? = null

    private val firestore by lazy{
        FirebaseFirestore.getInstance()
    }

    private fun adicionarContratoNoFirestore(imageContratoURL: String, caminhoImagem: String, nomeImagem: String) {
        val contratoData = mapOf(
            "imageURL" to imageContratoURL,
            "caminhoImagem" to caminhoImagem,
            "nomeImagem" to nomeImagem,
        )

        firestore.collection("contratos")
            .add(contratoData)
            .addOnSuccessListener {
                Toast.makeText(this, "Contrato adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Falha ao adicionar contrato: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }


    private val gerenciadorGaleriasContrato = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri == null) {
            Toast.makeText(this, "Nenhum contrato selecionado.", Toast.LENGTH_LONG).show()
        } else {
            imageContratoUri = uri
            binding.btnEscolhaContrato.text = "Imagem selecionada"
        }
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

        val imagemRef = refStorage.child("contratos/$tipoImagem/$nomeArquivoUnico")
        val caminhoImagem = "contratos/$tipoImagem/$nomeArquivoUnico"

        nomeArquivoUnico?.let {
            imagemRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imagemRef.downloadUrl.addOnSuccessListener { url ->
                        adicionarContratoNoFirestore(url.toString(), caminhoImagem, nomeArquivoUnico)
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
        binding.btnEscolhaContrato.setOnClickListener {
            gerenciadorGaleriasContrato.launch("image/*")
        }

        binding.btnAdicionarContrato.setOnClickListener {
            if (imageContratoUri != null) {
                uploadImagemStorage(imageContratoUri!!, "Contrato")
            } else {
                Toast.makeText(this, "Nenhum contrato selecionado.", Toast.LENGTH_LONG).show()
            }
        }
    }
}