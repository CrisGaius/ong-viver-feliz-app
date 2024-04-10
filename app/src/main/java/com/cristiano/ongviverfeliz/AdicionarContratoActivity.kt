package com.cristiano.ongviverfeliz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AdicionarContratoActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adicionar_contrato)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botaoSelecionarArquivo = findViewById<Button>(R.id.btn_add_contrato)
        botaoSelecionarArquivo.setOnClickListener {
            mostrarSeletorDeArquivo()
        }
    }

    private fun mostrarSeletorDeArquivo() {
        val intentSelecionarArquivo = Intent(Intent.ACTION_GET_CONTENT)
        intentSelecionarArquivo.type = "image/*"
        startActivityForResult(intentSelecionarArquivo, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            // Arquivo selecionado
            val uriArquivoSelecionado = data.data
            // Faça o que desejar com a URI do arquivo selecionado
        }
    }

}