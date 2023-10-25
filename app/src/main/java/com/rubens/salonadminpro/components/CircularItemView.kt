package com.rubens.salonadminpro.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.databinding.CircularItemsViewBinding
import com.rubens.salonadminpro.view.adapters.CircularItemRvAdapter
import com.rubens.salonadminpro.view.interfaces.EmployeeClickListener
import com.rubens.salonadminpro.view.interfaces.ServiceClickListener

class CircularItemsView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

    private lateinit var servicesRvAdapter: CircularItemRvAdapter
    private lateinit var professionalsRv: CircularItemRvAdapter
    private val binding: CircularItemsViewBinding = CircularItemsViewBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularItemsView)
        setLabelText(attributes.getString(R.styleable.CircularItemsView_labelText))


    }

    fun setLabelText(text: String?){
        binding.tvLabelChooseProfessional2.text = text
    }

    fun setProfessionalsRecyclerView(listaDeProfissionais: ArrayList<Funcionario>, employeeClickListener: EmployeeClickListener){
        professionalsRv = CircularItemRvAdapter(employeeList = listaDeProfissionais, servicesList = null, employeeClickListener = employeeClickListener)
        binding.professionalsRecyclerView.adapter = professionalsRv
    }

    fun setServicesRecyclerView(listaDeServicos: ArrayList<Servico>, serviceClickListener: ServiceClickListener){
        servicesRvAdapter = CircularItemRvAdapter(employeeList = null, servicesList = listaDeServicos, serviceClickListener = serviceClickListener)
        binding.professionalsRecyclerView.adapter = servicesRvAdapter
    }

    fun getProfessionalsRecyclerviewItems(): ArrayList<Funcionario>? {
        return professionalsRv.employeeList
    }

    fun getServicesRecyclerviewItems(): ArrayList<Servico>?{
        return servicesRvAdapter.servicesList
    }

    fun removerFuncionariosDaRecyclerView(todosOsFuncionariosASeremRetirados: ArrayList<String>) {
        todosOsFuncionariosASeremRetirados.forEach {
                funcionarioKey->
            val index = professionalsRv.employeeList?.indexOfFirst { it.childKey == funcionarioKey }

            if(index != -1){
                if (index != null) {
                    professionalsRv.employeeList?.removeAt(index)
                    professionalsRv.notifyItemRemoved(index)
                }
            }

        }


    }

    fun removerServicosDaRecyclerview(servicosQuePrecisamRetirados: List<Servico>) {
        servicosQuePrecisamRetirados.forEach {
            servicesRvAdapter.servicesList?.remove(it)
            servicesRvAdapter.notifyDataSetChanged()

        }


    }

    fun addNewEmployeesToRv(funcionariosASeremRecolocadas: ArrayList<Funcionario>) {
        professionalsRv.employeeList?.addAll(funcionariosASeremRecolocadas)
        professionalsRv.notifyDataSetChanged()

    }

    fun addNewServicesToRv(servicosASeremRecolocadas: ArrayList<Servico>) {
        servicesRvAdapter.servicesList?.addAll(servicosASeremRecolocadas)
        servicesRvAdapter.notifyDataSetChanged()

    }
}