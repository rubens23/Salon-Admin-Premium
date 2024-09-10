package com.rubens.salonadminpro.view.adapters


import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.databinding.ProfessionalItemBinding
import com.rubens.salonadminpro.view.interfaces.EmployeeClickListener
import com.rubens.salonadminpro.view.interfaces.ServiceClickListener


class CircularItemRvAdapter(
    var employeeList: List<Funcionario>? = null,
    var servicesList: List<Servico>? = null,
    val serviceClickListener: ServiceClickListener? = null,
    val employeeClickListener: EmployeeClickListener? = null
) : RecyclerView.Adapter<CircularItemRvAdapter.ViewHolder>() {


    inner class ViewHolder(val binding: ProfessionalItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bindProfessionalItem(emp: Funcionario) {
            setProfessionalView(emp)
            modifyProfessionalBackgroundIfSelected(emp)


            binding.ivProfessional.setOnClickListener {
                lidarComCliqueNoProfissional(emp)
                clicouNumProfissional(emp)
            }

            binding.tvLabelProfessionalName.setOnClickListener {
                lidarComCliqueNoProfissional(emp)
                clicouNumProfissional(emp)

            }



        }

        private fun clicouNumProfissional(emp: Funcionario) {
            employeeClickListener?.onEmployeeClick(emp)
        }

        private fun lidarComCliqueNoProfissional(emp: Funcionario) {
            employeeList?.forEach {
                    employee ->
                if(employee != emp){
                    if(employee.isSelected){
                        employee.isSelected = false
                        notifyDataSetChanged()
                    }
                }
            }


            //faz a nova seleção
            darkenNameTextFont()
            colocarBordaNoItemSelecionado()
            emp.isSelected = true
        }

        private fun lidarComCliqueNoService(service: Servico) {
            //se ja tem um servico selecioando
            //tira a selecao desse item
            servicesList?.forEach {
                    servico ->
                if(servico != service){
                    if(servico.isSelected){
                        servico.isSelected = false
                        notifyDataSetChanged()
                    }
                }
            }


            //faz a nova seleção
            darkenNameTextFont()
            colocarBordaNoItemSelecionado()
            service.isSelected = true
        }

        private fun colocarBordaNoItemSelecionado() {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(1.4f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            binding.ivProfessional.colorFilter = filter
            binding.ivProfessional.borderWidth = 14
            binding.ivProfessional.borderColor = binding.root.context.resources.getColor(R.color.alt_black2)
        }

        private fun modifyProfessionalBackgroundIfSelected(emp: Funcionario) {
            if(emp.isSelected){
                darkenNameTextFont()
                colocarBordaNoItemSelecionado()
            }else{
                lightenNameTextFont()
                tirarBordaDoItem()
            }

        }

        private fun modifyServiceBackgroundIfSelected(service: Servico) {
            if(service.isSelected){
                darkenNameTextFont()
                colocarBordaNoItemSelecionado()
            }else{
                lightenNameTextFont()
                tirarBordaDoItem()
            }
        }


        private fun tirarBordaDoItem() {
            binding.ivProfessional.clearColorFilter()
            binding.ivProfessional.borderWidth = 0
            binding.ivProfessional.borderColor = binding.root.context.resources.getColor(R.color.alt_black)
        }

        private fun setProfessionalView(emp: Funcionario) {
            binding.tvLabelProfessionalName.text = emp.nome
            Glide.with(binding.root.context).load(emp.linkImagem)
                .circleCrop().into(binding.ivProfessional)

        }

        fun bindServiceItem(service: Servico) {
            setServicesViews(service)
            modifyServiceBackgroundIfSelected(service)
            binding.ivProfessional.setOnClickListener {
                lidarComCliqueNoService(service)
                clicouNumServico(service)
            }

            binding.tvLabelProfessionalName.setOnClickListener {
                lidarComCliqueNoService(service)
                clicouNumServico(service)

            }
        }

        private fun clicouNumServico(service: Servico) {
            serviceClickListener?.onServiceClick(service)
        }


        private fun setServicesViews(service: Servico) {
            binding.tvLabelProfessionalName.text = service.nomeServico
            Glide.with(binding.root.context).load(service.linkImagem)
                .circleCrop().into(binding.ivProfessional)
        }

        private fun darkenNameTextFont() {
            binding.tvLabelProfessionalName.setTextColor(binding.root.resources.getColor(R.color.alt_black2))
        }

        private fun lightenNameTextFont() {
            binding.tvLabelProfessionalName.setTextColor(binding.root.resources.getColor(R.color.alt_black))
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        return ViewHolder(
            ProfessionalItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(employeeList != null){
            holder.bindProfessionalItem(employeeList!![position])

        }
        if(servicesList != null){
            holder.bindServiceItem(servicesList!![position])

        }
    }

    override fun getItemCount(): Int{
        if(employeeList != null){
            return employeeList!!.size
        }

        if(servicesList != null){
            return servicesList!!.size
        }
        return 0

    }
}