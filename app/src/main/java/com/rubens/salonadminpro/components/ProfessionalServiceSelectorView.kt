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

class ProfessionalServiceSelectorView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {

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

    fun setProfessionalsList(listaDeProfissionais: List<Funcionario>, employeeClickListener: EmployeeClickListener){
        professionalsRv = CircularItemRvAdapter(employeeList = listaDeProfissionais, servicesList = null, employeeClickListener = employeeClickListener)
        binding.professionalsRecyclerView.adapter = professionalsRv
    }

    fun setServicesList(listaDeServicos: ArrayList<Servico>, serviceClickListener: ServiceClickListener){
        servicesRvAdapter = CircularItemRvAdapter(employeeList = null, servicesList = listaDeServicos, serviceClickListener = serviceClickListener)
        binding.professionalsRecyclerView.adapter = servicesRvAdapter
    }

    fun getProfessionalsRecyclerviewItems(): List<Funcionario>? {
        return professionalsRv.employeeList
    }

    fun getServicesRecyclerviewItems(): List<Servico>?{
        return servicesRvAdapter.servicesList
    }

    //todo implementar a remocao de funcionarios
    fun removeEmployeesFromList(todosOsFuncionariosASeremRetirados: List<String>) {
        val mutableList = professionalsRv.employeeList?.toMutableList()
        todosOsFuncionariosASeremRetirados.forEach {
                funcionarioKey->
            val index = mutableList?.indexOfFirst { it.childKey == funcionarioKey }
            index?.let {
                idx->
                mutableList.removeAt(idx)
                professionalsRv.employeeList = mutableList
                professionalsRv.notifyItemRemoved(idx)
            }


        }



    }

    //todo implementar a remoção do servico
    fun removeServicesFromList(servicosQuePrecisamRetirados: List<Servico>) {
        val mutableList = servicesRvAdapter.servicesList?.toMutableList()
        servicosQuePrecisamRetirados.forEach {
            service->
            val index = mutableList?.indexOf(service)
            index?.let {
                mutableList.remove(service)
                servicesRvAdapter.servicesList = mutableList
                servicesRvAdapter.notifyItemRemoved(index)
            }



        }


    }

    //todo implemntar a adicao de funcionarios
//    fun addEmployees(funcionariosASeremRecolocadas: List<Funcionario>) {
//        professionalsRv.employeeList?.addAll(funcionariosASeremRecolocadas)
//        professionalsRv.notifyDataSetChanged()
//
//    }

    //todo implemntar a adicao de servicos

//    fun addServices(servicosASeremRecolocadas: List<Servico>) {
//        servicesRvAdapter.servicesList?.addAll(servicosASeremRecolocadas)
//        servicesRvAdapter.notifyDataSetChanged()
//
//    }
}