package com.cristiano.ongviverfeliz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class AtributosListaContratoAdapter(
    private val lista: List<AtributosListaContrato>,
    private val clique: (String, String, String) -> Unit
): Adapter<AtributosListaContratoAdapter.AtributosListaContratoViewHolder>() {

    inner class AtributosListaContratoViewHolder(val itemView: View) : ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.txtId)
        val textNomeArquivo: TextView = itemView.findViewById(R.id.textNomeArquivo)
        val iconeLixeira: ImageButton = itemView.findViewById(R.id.imageButton3)
        val iconeVisualizar: ImageButton = itemView.findViewById(R.id.imageButton2)

        fun bind(atributosListaContrato: AtributosListaContrato) { //Conectar com a interface
            textId.text = atributosListaContrato.id
            textNomeArquivo.text = atributosListaContrato.nome

            iconeVisualizar.setOnClickListener {
                clique("", atributosListaContrato.caminho, atributosListaContrato.imagemUrl)
            }

            //Aplicar eventos de clique
            //val context = iconeLixeira.context
            iconeLixeira.setOnClickListener {
                clique(atributosListaContrato.id, atributosListaContrato.caminho, atributosListaContrato.imagemUrl)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AtributosListaContratoViewHolder {
        val layoutInflater = LayoutInflater.from(
            parent.context
        )

        val itemView = layoutInflater.inflate(
            R.layout.item_contrato, parent, false
        )

        return AtributosListaContratoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AtributosListaContratoViewHolder, position: Int) {
        val atributosListaContrato = lista[position]
        holder.bind(atributosListaContrato)
    }

    override fun getItemCount(): Int {
        return lista.size
    }

}
