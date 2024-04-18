package com.cristiano.ongviverfeliz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class AtributosListaAdapter(
    private val lista: List<AtributosLista>,
    private val clique: (String) -> Unit
): Adapter<AtributosListaAdapter.AtributosListaViewHolder>() {

    inner class AtributosListaViewHolder(val itemView: View) : ViewHolder(itemView) {
        val textId: TextView = itemView.findViewById(R.id.txtId)
        val textNomeArquivo: TextView = itemView.findViewById(R.id.textNomeArquivo)
        val iconeLixeira: ImageButton = itemView.findViewById(R.id.imageButton3)

        fun bind(atributosLista: AtributosLista) { //Conectar com a interface
            textId.text = atributosLista.id
            textNomeArquivo.text = atributosLista.nome

            //Aplicar eventos de clique
            //val context = iconeLixeira.context
            iconeLixeira.setOnClickListener {
                clique(atributosLista.id)
            }
        }
    }

    //Ao criar o View Holder -> Cria a visualização
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtributosListaViewHolder {
        val layoutInflater = LayoutInflater.from(
            parent.context
        )

        val itemView = layoutInflater.inflate(
            R.layout.item_listas, parent, false
        )

        return AtributosListaViewHolder(itemView)
    }

    //Ao vincular o viewHolder
    override fun onBindViewHolder(holder: AtributosListaViewHolder, position: Int) {
        val atributosLista = lista[position]
        holder.bind(atributosLista)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}
