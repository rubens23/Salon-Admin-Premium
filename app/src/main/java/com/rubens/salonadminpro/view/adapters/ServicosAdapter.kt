package com.rubens.salonadminpro.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.databinding.ItemFuncionarioBinding

class ServicosAdapter(val listaServicos: List<Servico>):
/**
 * ItemFuncionario serve para o layout da recycler de serviços
 * apesar de alguns componentes estarem escrito como funcionario eles vão designar itens de serviço
 */
    RecyclerView.Adapter<ServicosAdapter.ViewHolder>(){
    class ViewHolder(val binding: ItemFuncionarioBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(servico: Servico){
            //tvNomeFuncionario == nome do Serviço
            binding.tvNomeFuncionario.text = servico.nomeServico
            //ivFuncionario == ivServico
            Glide.with(binding.root.context).load(servico.linkImagem).circleCrop().into(binding.ivFuncionario)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFuncionarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listaServicos[position])
    }

    override fun getItemCount(): Int = listaServicos.size
}