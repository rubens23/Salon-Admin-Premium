package com.rubens.salonadminpro.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Appointment
import com.rubens.salonadminpro.databinding.HorarioMarcadoItemBinding
import com.rubens.salonadminpro.view.interfaces.OnAppointmentClickListener

class HorariosAgendadosAdapter(val listaAgendamentos: List<Appointment>?, val appointmentClickListener: OnAppointmentClickListener): RecyclerView.Adapter<HorariosAgendadosAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: HorarioMarcadoItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(horarioMarcado: Appointment, position: Int){
            binding.nomeServico.text = horarioMarcado.service
            binding.tvNomeCliente.text = horarioMarcado.clientName
            binding.tvNomeFuncionario.text = horarioMarcado.employee
            setDate(horarioMarcado)
            binding.tvHorarioMarcado.text = horarioMarcado.hour
            when(horarioMarcado.confirmacaoAtendimento){
                "aguardando confirmacao"->{binding.statusAtendimento.setBackgroundResource(R.color.light_yellow)}
                "negado"->{binding.statusAtendimento.setBackgroundResource(R.color.vermelho)}
                "confirmado"->{binding.statusAtendimento.setBackgroundResource(R.color.verde)}
            }

            binding.root.setOnClickListener {
                appointmentClickListener.onAppointmentClick(horarioMarcado, position)
            }


        }

        private fun setDate(horarioMarcado: Appointment) {
            when(horarioMarcado.month){
                "Janeiro"->{binding.tvData.text = horarioMarcado.day+" Jan"}
                "Fevereiro"->{binding.tvData.text = horarioMarcado.day+" Fev"}
                "Março"->{binding.tvData.text = horarioMarcado.day+" Mar"}
                "Abril"->{binding.tvData.text = horarioMarcado.day+" Abr"}
                "Maio"->{binding.tvData.text = horarioMarcado.day+" Mai"}
                "Junho"->{binding.tvData.text = horarioMarcado.day+" Jun"}
                "Julho"->{binding.tvData.text = horarioMarcado.day+" Jul"}
                "Agosto"->{binding.tvData.text = horarioMarcado.day+" Ago"}
                "Setembro"->{binding.tvData.text = horarioMarcado.day+" Set"}
                "Outubro"->{binding.tvData.text = horarioMarcado.day+" Out"}
                "Novembro"->{binding.tvData.text = horarioMarcado.day+" Nov"}
                "Dezembro"->{binding.tvData.text = horarioMarcado.day+" Dez"}
                "January"->{binding.tvData.text = horarioMarcado.day+" Jan"}
                "February"->{binding.tvData.text = horarioMarcado.day+" Feb"}
                "March"->{binding.tvData.text = horarioMarcado.day+" Mar"}
                "April"->{binding.tvData.text = horarioMarcado.day+" Apr"}
                "May"->{binding.tvData.text = horarioMarcado.day+" May"}
                "June"->{binding.tvData.text = horarioMarcado.day+" Jun"}
                "July"->{binding.tvData.text = horarioMarcado.day+" Jul"}
                "August"->{binding.tvData.text = horarioMarcado.day+" Aug"}
                "September"->{binding.tvData.text = horarioMarcado.day+" Sep"}
                "october"->{binding.tvData.text = horarioMarcado.day+" Oct"}
                "November"->{binding.tvData.text = horarioMarcado.day+" Nov"}
                "December"->{binding.tvData.text = horarioMarcado.day+" Dec"}

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = HorarioMarcadoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listaAgendamentos!![position], position)
    }

    override fun getItemCount(): Int{
        return listaAgendamentos?.size ?: 0
    }
}