package com.rubens.salonadminpro.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.databinding.ItemFuncionarioBinding
import com.rubens.salonadminpro.view.interfaces.OnFuncionarioClickListener

class FuncionariosAdapter(private val listaFuncionarios: List<Funcionario>, val onFuncionarioClickListener: OnFuncionarioClickListener): RecyclerView.Adapter<FuncionariosAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemFuncionarioBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(funcionario: Funcionario, position: Int){
                binding.tvNomeFuncionario.text = funcionario.nome
                binding.tvCargoFuncionario.text = funcionario.cargo
                Glide.with(binding.root.context).load(funcionario.linkImagem).circleCrop().into(binding.ivFuncionario)







            binding.containerFuncionario.setOnClickListener {

                onFuncionarioClickListener.onFuncionarioClick(funcionario)
            }



        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFuncionarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listaFuncionarios[position], position)
    }

    override fun getItemCount(): Int = listaFuncionarios.size


}