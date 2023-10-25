package com.rubens.salonadminpro.view.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.salonadminpro.databinding.ItemServicosFuncionarioEspecificoBinding
import com.rubens.salonadminpro.view.interfaces.ComunicacaoComFragment

class ServicosFuncionarioAdapter(val myInterface: ComunicacaoComFragment, val context: Context, private val listaServicosFuncionario: MutableList<String>):
    RecyclerView.Adapter<ServicosFuncionarioAdapter.ViewHolder>() {





    inner class ViewHolder(private val binding: ItemServicosFuncionarioEspecificoBinding): RecyclerView.ViewHolder(binding.root){
            fun bindData(servicoFuncionario: String){
                binding.nomeServico.text = servicoFuncionario

                binding.removeIcon.setOnClickListener {

                    myInterface.removerServico(servicoFuncionario)

                }


            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicosFuncionarioAdapter.ViewHolder {
        val binding = ItemServicosFuncionarioEspecificoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicosFuncionarioAdapter.ViewHolder, position: Int) {


        if(listaServicosFuncionario.size > 0){
            holder.bindData(listaServicosFuncionario[position])
        }


    }

    override fun getItemCount(): Int = listaServicosFuncionario.size

}