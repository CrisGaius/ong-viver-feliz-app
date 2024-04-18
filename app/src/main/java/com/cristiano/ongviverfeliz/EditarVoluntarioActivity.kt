package com.cristiano.ongviverfeliz

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cristiano.ongviverfeliz.databinding.ActivityEditarVoluntarioBinding
import com.cristiano.ongviverfeliz.modelo.Voluntario
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class EditarVoluntarioActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityEditarVoluntarioBinding.inflate(layoutInflater)
    }
    private val firestore by lazy{
        FirebaseFirestore.getInstance()
    }
    private val uuid = "fd868c4d-d85f-4b8c-9326-239f53abf2c3"

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
    private lateinit var atvVoluntariaAtualizado: String
    private lateinit var diaSemanaAtualizado: String
    private lateinit var formaTrabalhoAtualizado: String
    private lateinit var horarioAtualizado: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()

        recuperarDados(uuid)

        //val uuid = intent.getStringExtra("uuid")
        /*if(uuid != null){
            preencherDados(uuid)
        }*/
    }

    private fun inicializarEventosClique() {
        binding.btnEditarVoluntario.setOnClickListener {
            if(validarCampos()){
                atualizarDados(
                    uuid,
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
                    estadoAtualizado,
                    atvVoluntariaAtualizado,
                    diaSemanaAtualizado,
                    formaTrabalhoAtualizado,
                    horarioAtualizado
                )
            }
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
        estadoAtualizado: String,
        atvVoluntariaAtualizado: String,
        diaSemanaAtualizado: String,
        formaTrabalhoAtualizado: String,
        horarioAtualizado: String
    ) {

        val voluntarioAtualizado = mapOf(
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
            "atividadeVoluntaria" to atvVoluntariaAtualizado,
            "horario" to horarioAtualizado,
            "diaSemana" to diaSemanaAtualizado,
            "formaTrabalho" to formaTrabalhoAtualizado
        )

        firestore.collection("Voluntários").document(uuid).update(voluntarioAtualizado).addOnSuccessListener {
        }.addOnFailureListener {e ->
            e.printStackTrace()
        }
    }
    private fun recuperarDados(uuid: String) {
         firestore.collection("Voluntários").document(uuid).get().addOnSuccessListener { dados ->
            if(dados != null){
                val voluntario = dados.toObject<Voluntario>()
                preencherDados(voluntario)
            }
        }.addOnFailureListener {e ->
            e.printStackTrace()
        }
    }

    private fun preencherDados(voluntario: Voluntario?) {
        binding.idTextNomeEditarVoluntario.setText(voluntario?.nome ?: "")
        binding.idTextDataNascEditarVoluntario.setText(voluntario?.dataNasc ?: "")
        binding.idTextTelefoneEditarVoluntario.setText(voluntario?.telefone ?: "")
        binding.idTextRgEditarVoluntario.setText(voluntario?.rg ?: "")
        binding.idTextCpfEditarVoluntario.setText(voluntario?.cpf ?: "")
        binding.idTextEmailEditarVoluntario.setText(voluntario?.email ?: "")
        binding.idTextRuaEditarVoluntario.setText(voluntario?.rua ?: "")
        binding.idTextNumResidEditarVoluntario.setText(voluntario?.numeroResidencia ?: "")
        binding.idTextBairroEditarVoluntario.setText(voluntario?.bairro ?: "")
        binding.idTextCidadeEditarVoluntario.setText(voluntario?.cidade ?: "")
        binding.idTextEstadoEditarVoluntario.setText(voluntario?.estado ?: "")
        binding.idTextAtividadeVEditarVoluntario.setText(voluntario?.atvVoluntaria ?: "")
        binding.idTextDiasSemanaEditarVoluntario.setText(voluntario?.diaSemana ?: "")
        binding.idTextFormaTrabalhoEditarVoluntario.setText(voluntario?.formaTrabalho ?: "")
        binding.idTextHorarioEditarVoluntario.setText(voluntario?.horario ?: "")
    }

    private fun validarCampos(): Boolean {
        nomeAtualizado = binding.idTextNomeEditarVoluntario.text.toString()
        dataNascAtualizado = binding.idTextDataNascEditarVoluntario.text.toString()
        telefoneAtualizado = binding.idTextTelefoneEditarVoluntario.text.toString()
        rgAtualizado = binding.idTextRgEditarVoluntario.text.toString()
        cpfAtualizado = binding.idTextCpfEditarVoluntario.text.toString()
        emailAtualizado = binding.idTextEmailEditarVoluntario.text.toString()
        ruaAtualizado = binding.idTextRuaEditarVoluntario.text.toString()
        numeroResidenciaAtualizado = binding.idTextNumResidEditarVoluntario.text.toString()
        bairroAtualizado = binding.idTextBairroEditarVoluntario.text.toString()
        cidadeAtualizado = binding.idTextCidadeEditarVoluntario.text.toString()
        estadoAtualizado = binding.idTextEstadoEditarVoluntario.text.toString()
        atvVoluntariaAtualizado = binding.idTextAtividadeVEditarVoluntario.text.toString()
        diaSemanaAtualizado = binding.idTextDiasSemanaEditarVoluntario.text.toString()
        formaTrabalhoAtualizado = binding.idTextFormaTrabalhoEditarVoluntario.text.toString()
        horarioAtualizado = binding.idTextHorarioEditarVoluntario.text.toString()

        if (nomeAtualizado.isEmpty() || dataNascAtualizado.isEmpty() || telefoneAtualizado.isEmpty() || rgAtualizado.isEmpty() ||
            cpfAtualizado.isEmpty() || emailAtualizado.isEmpty() || ruaAtualizado.isEmpty() || numeroResidenciaAtualizado.isEmpty() ||
            bairroAtualizado.isEmpty() || cidadeAtualizado.isEmpty() || estadoAtualizado.isEmpty() || atvVoluntariaAtualizado.isEmpty() ||
            diaSemanaAtualizado.isEmpty() || formaTrabalhoAtualizado.isEmpty() || horarioAtualizado.isEmpty() ) {

            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_LONG).show()
            return false
        } else {
            return true
        }
    }

}