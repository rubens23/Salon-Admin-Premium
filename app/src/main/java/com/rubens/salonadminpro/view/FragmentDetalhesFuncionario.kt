package com.rubens.salonadminpro.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.databinding.FragmentFragmentDetalhesFuncionarioBinding
import com.rubens.salonadminpro.view.adapters.FuncionariosAdapter
import com.rubens.salonadminpro.view.adapters.ServicosFuncionarioAdapter
import com.rubens.salonadminpro.view.interfaces.ComunicacaoComFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentDetalhesFuncionario : Fragment(), ComunicacaoComFragment {

    private lateinit var binding: FragmentFragmentDetalhesFuncionarioBinding
    private lateinit var viewModel: FragmentDetalhesFuncionarioViewModel
    private var funcionarioFromExtra: Funcionario? = null
    private lateinit var adapter: ServicosFuncionarioAdapter


    companion object{
        fun newInstance(func: Funcionario): FragmentDetalhesFuncionario{
            val fragment = FragmentDetalhesFuncionario()
            val args = Bundle()
            args.putSerializable("func", func)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            funcionarioFromExtra = it.getSerializable("func") as Funcionario?
            funcionarioFromExtra
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentDetalhesFuncionarioBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        setDadosFuncionariosInLayout()
        createSpinnerList()
        onClickListeners()

    }

    private fun onClickListeners() {
        binding.btnDarFolga.setOnClickListener {
            binding.cvAdicionarFolga.visibility = View.VISIBLE
            binding.root.setBackgroundColor(resources.getColor(R.color.alt_black3))
            binding.imgFuncionarioDetalhe.visibility = View.GONE
            binding.mainCardview.visibility = View.GONE
            binding.detalheBackground.setBackgroundColor(resources.getColor(R.color.alt_black3))
        }

        binding.btnConfirmarFolga.setOnClickListener {
            if (binding.etDataFolga.isDone){
                viewModel.checarSeDiaValidoParaFolga(binding.etDataFolga)
            }else{
                binding.etDataFolga.error = "Digite uma data!"
            }

        }


        binding.ivCloseAddFolga.setOnClickListener {
            changeBackgroundColor()
            changeCardVisibility()
        }

        binding.addServicoFuncionario.setOnClickListener {
            //updateListaDeServicos()

            binding.cvAdicionarServico.visibility = View.VISIBLE
            binding.root.setBackgroundColor(resources.getColor(R.color.alt_black3))
            binding.imgFuncionarioDetalhe.visibility = View.GONE
            binding.mainCardview.visibility = View.GONE
            binding.detalheBackground.setBackgroundColor(resources.getColor(R.color.alt_black3))
            if(funcionarioFromExtra != null){
                viewModel.carregarServicosDoFuncionario(funcionarioFromExtra!!.childKey)

            }

        }


        binding.ivCloseAddServico.setOnClickListener {
            changeBackgroundColor()
            changeServicesCardVisibility()
            if (funcionarioFromExtra != null ){
                viewModel.updateListaDeServicos(funcionarioFromExtra!!.childKey)

            }

        }


        binding.ivCloseVerFolga.setOnClickListener {
            changeBackgroundColor()
            changeCardVisibility()

        }


        binding.btnConfirmarNovoServico.setOnClickListener {
            if(funcionarioFromExtra != null){
                viewModel.updateServicosDoFuncionario(viewModel.servicoSelecionado, funcionarioFromExtra as Funcionario)

            }
        }

        binding.btnVerFolgas.setOnClickListener {
            binding.cvVerFolga.visibility = View.VISIBLE
            binding.root.setBackgroundColor(resources.getColor(R.color.alt_black3))
            binding.imgFuncionarioDetalhe.visibility = View.GONE
            binding.mainCardview.visibility = View.GONE
            binding.detalheBackground.setBackgroundColor(resources.getColor(R.color.alt_black3))
            if(funcionarioFromExtra != null){
                viewModel.pegarFolgas(funcionarioFromExtra!!.childKey)

            }
        }
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentDetalhesFuncionarioViewModel::class.java]
        initCollectors()
    }

    private fun createSpinnerList() {
        viewModel.listaSpinnerServicos = arrayOf("Corte de Cabelo", "Pintura de Cabelo",
            "Manicure", "Pedicure")

        createAdapterDoSpinnerServicos()
    }

    private fun createAdapterDoSpinnerServicos() {
        val adapterSpinnerServicos = ArrayAdapter(
            requireActivity(),
            R.layout.drop_down_item,
            viewModel.listaSpinnerServicos
        )

        setSpinnerAdapter(adapterSpinnerServicos)
    }

    private fun setSpinnerAdapter(adapterSpinnerServicos: ArrayAdapter<String>) {
        binding.tvSpinnerServicos.setAdapter(adapterSpinnerServicos)

        setServicosSpinnerClickListener()
    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.etDataFolgaSharedFlow.collectLatest {
                    msg->
                    if(msg == "Essa data já passou, escolha outra!"){
                        binding.etDataFolga.error = "Essa data já passou, escolha outra!"
                    }
                    if(msg == "updateFolga"){
                        if(funcionarioFromExtra != null){
                            viewModel.salvarFolgaNova(funcionarioFromExtra!!.childKey, binding.etDataFolga.masked)

                        }


                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouFolgaDoFuncionario.collectLatest {
                        dataFolga->
                    viewModel.folgaDoFuncionario = dataFolga
                    setarFolgaNaTextView(viewModel.folgaDoFuncionario)



                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.salvouNovaFolga.collectLatest {
                        salvou->
                    if(salvou){
                        showToast("Nova folga adicionada!")
                        changeBackgroundColor()
                        changeCardVisibility()

                    }else{
                        showToast("Ocorreu um problema ao salvar a folga. verifique os dados e tente novamente!")
                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.atualizouListaDeServicos.collectLatest {
                        atualizouServicos->
                    if(atualizouServicos){
                        if(viewModel.listaDeServicosDoFuncionario.isNotEmpty()){
                            setupServicosAdapterWithNewList(viewModel.listaDeServicosDoFuncionario)
                        }
                    }


                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouListaDeServicos.collectLatest {
                        listaDeServicos->

                            setupServicosAdapterWithNewList(listaDeServicos)




                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.msgDeErro.collectLatest {
                        msg->
                    showToast(msg)


                }
            }
        }
    }

    private fun setupServicosAdapterWithNewList(listaDeServicosDoFuncionario: ArrayList<String>) {
        adapter = ServicosFuncionarioAdapter(this, requireContext(), listaDeServicosDoFuncionario)
        binding.rvServicosFuncionarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvServicosFuncionarios.adapter = adapter
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun changeServicesCardVisibility() {
        binding.imgFuncionarioDetalhe.visibility = View.VISIBLE
        binding.mainCardview.visibility = View.VISIBLE
        binding.cvAdicionarServico.visibility = View.GONE
    }

    private fun changeBackgroundColor() {
        binding.detalheBackground.setBackgroundColor(resources.getColor(R.color.alt_black))
        binding.root.setBackgroundColor(resources.getColor(R.color.white))
    }

    private fun changeCardVisibility() {
        binding.imgFuncionarioDetalhe.visibility = View.VISIBLE
        binding.mainCardview.visibility = View.VISIBLE
        binding.cvAdicionarFolga.visibility = View.GONE
        binding.cvVerFolga.visibility = View.GONE

    }

    private fun setDadosFuncionariosInLayout() {
        binding.tvNomeFuncionarioDetalhe.text = funcionarioFromExtra?.nome
        binding.tvCargoFuncionario.text = funcionarioFromExtra?.cargo
        Glide.with(requireContext()).load(funcionarioFromExtra?.linkImagem).circleCrop().into(binding.imgFuncionarioDetalhe)
    }



    override fun removerServico(servicoFuncionario: String) {
        if(funcionarioFromExtra != null){
            viewModel.removerServicoDoFuncionario(funcionarioFromExtra!!.childKey, servicoFuncionario)

        }
    }

    private fun setarFolgaNaTextView(dataFolga: String) {
        if (dataFolga == ""){
            binding.tvDataProximaFolga.text = "-"
        }else{
            binding.tvDataProximaFolga.text = dataFolga
        }

    }

    private fun setServicosSpinnerClickListener() {

        binding.tvSpinnerServicos.setOnItemClickListener { _, _, pos, _ ->
            when(pos){
                0->{
                    binding.btnConfirmarNovoServico.visibility = View.VISIBLE
                    //aqui eu tenho que adicionar o nome do serviço a string
                    viewModel.servicoSelecionado = "Corte de Cabelo"
                }
                1->{
                    binding.btnConfirmarNovoServico.visibility = View.VISIBLE
                    viewModel.servicoSelecionado = "Pintura de Cabelo"

                }
                2->{
                    binding.btnConfirmarNovoServico.visibility = View.VISIBLE
                    viewModel.servicoSelecionado = "Manicure"
                }
                3->{
                    binding.btnConfirmarNovoServico.visibility = View.VISIBLE
                    viewModel.servicoSelecionado = "Pedicure"
                }
            }
        }


    }


}