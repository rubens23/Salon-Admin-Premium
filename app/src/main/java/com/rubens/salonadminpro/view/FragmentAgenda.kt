package com.rubens.salonadminpro.view

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Appointment
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.databinding.FragmentFragmentAgendaBinding
import com.rubens.salonadminpro.view.adapters.HorariosAgendadosAdapter
import com.rubens.salonadminpro.view.interfaces.EmployeeClickListener
import com.rubens.salonadminpro.view.interfaces.OnAppointmentClickListener
import com.rubens.salonadminpro.viewmodels.FragmentAgendaViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

class FragmentAgenda : Fragment(), OnAppointmentClickListener, EmployeeClickListener {

    private lateinit var binding: FragmentFragmentAgendaBinding
    private lateinit var viewModel: FragmentAgendaViewModel
    private lateinit var adapter: HorariosAgendadosAdapter

    var isFuncionariosFilterArrowUp = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentAgendaBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        initCollectors()
        getAllAppoitments()

        onClickListeners()
    }

    private fun onClickListeners() {
        binding.ivArrowShowFuncionariosFilter.setOnClickListener {
            val animation = if(isFuncionariosFilterArrowUp) AnimationUtils.loadAnimation(requireContext(),
            R.anim.rotate_down) else AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_up)


            animation.fillAfter = true

            binding.ivArrowShowFuncionariosFilter.startAnimation(animation)

            isFuncionariosFilterArrowUp = !isFuncionariosFilterArrowUp

            if(isFuncionariosFilterArrowUp){
                podeMostrarFuncionariosRv(true)
                pegarTodosOsFuncionarios()
            }else{
                podeMostrarFuncionariosRv(false)
            }
        }

        binding.ivIconCalendario.setOnClickListener {
            showCalendarPicker()
        }
    }

    private fun podeMostrarFuncionariosRv(pode: Boolean) {
        if (pode){
            binding.professionalsCircleItemView.visibility = View.VISIBLE
        }else{
            binding.professionalsCircleItemView.visibility = View.GONE

        }
    }

    private fun showCalendarPicker() {


        val c = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->

            val diaEscolhido = "${viewModel.obterDiaFormatado(dayOfMonth.toString())}/${monthOfYear + 1}/$year"

            pegarAppointmentsPorDia(
               diaEscolhido
            )
            },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()


    }

    private fun pegarAppointmentsPorDia(diaEscolhido: String) {
        viewModel.pegarAppointmentsPorDia(diaEscolhido)

    }


    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosAppointments.collectLatest {
                    allAppointments->
                    initRecyclerView(allAppointments)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.appointmentToUpdateErrorMsg.collectLatest {
                        errorMsg->
                    showToast(errorMsg)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosOsFuncionarios.collectLatest {
                        listaFuncionarios->
                    changeEmployeeRecyclerView(listaFuncionarios)





                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    }

    private fun getAllAppoitments() {
        viewModel.getAllAppointments()
    }

    private fun initRecyclerView(appointments: List<Appointment>) {


        if (appointments.isNotEmpty()){
            adapter = HorariosAgendadosAdapter(appointments, this)
            binding.recyclerHorariosMarcados.adapter = adapter
        }else{
            adapter = HorariosAgendadosAdapter(null, this)
            binding.recyclerHorariosMarcados.adapter = adapter
        }

    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentAgendaViewModel::class.java]

    }

    override fun onAppointmentClick(appointment: Appointment, positionToBeChanged: Int) {
        mostrarDialogDeConfirmacaoDeAtendimento(appointment, positionToBeChanged)
    }

    private fun mostrarDialogDeConfirmacaoDeAtendimento(
        appointment: Appointment,
        positionToBeChanged: Int
    ) {

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Confirmar atendimento?")
            .setPositiveButton("Confirmar") { dialog, _ ->
                // Lógica para lidar com o clique em "Sim"
                if(adapter.listaAgendamentos != null){
                    adapter.listaAgendamentos!![positionToBeChanged].confirmacaoAtendimento = "confirmado"
                    val appointmentToUpdate = adapter.listaAgendamentos!![positionToBeChanged]
                    adapter.notifyItemChanged(positionToBeChanged)
                    atualizarAppointmentPorId(appointmentToUpdate)
                }

                dialog.dismiss()
            }
            .setNegativeButton("Negar") { dialog, _ ->
                // Lógica para lidar com o clique em "Não"
                if(adapter.listaAgendamentos != null){
                    adapter.listaAgendamentos!![positionToBeChanged].confirmacaoAtendimento = "negado"
                    val appointmentToUpdate = adapter.listaAgendamentos!![positionToBeChanged]
                    adapter.notifyItemChanged(positionToBeChanged)
                    atualizarAppointmentPorId(appointmentToUpdate)
                }

                dialog.dismiss()
            }
            .setNeutralButton("Decidir depois") { dialog, _ ->
                // Lógica para lidar com o clique em "Cancelar"
                dialog.dismiss()
            }

        // Exibe o AlertDialog
        builder.create().show()

    }

    private fun atualizarAppointmentPorId(appointmentToUpdate: Appointment) {
        viewModel.updateAppointment(appointmentToUpdate)
    }

    private fun pegarTodosOsFuncionarios() {
        viewModel.getAllEmployees()

    }

    private fun changeEmployeeRecyclerView(listaFuncionarios: List<Funcionario>) {
        binding.professionalsCircleItemView.setProfessionalsList(listaFuncionarios, this)

    }

    override fun onEmployeeClick(funcionario: Funcionario) {
        viewModel.pegarAppointmentsPorFuncionario(funcionario)
    }


}