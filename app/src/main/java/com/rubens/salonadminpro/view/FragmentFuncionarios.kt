package com.rubens.salonadminpro.view

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.databinding.FragmentFragmentFuncionariosBinding
import com.rubens.salonadminpro.view.adapters.FuncionariosAdapter
import com.rubens.salonadminpro.view.interfaces.OnFuncionarioClickListener
import com.rubens.salonadminpro.viewmodels.FragmentFuncionariosViewModel
import com.rubens.salonadminpro.viewmodels.SharedViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentFuncionarios : Fragment(), OnFuncionarioClickListener {



    private lateinit var binding: FragmentFragmentFuncionariosBinding
    private lateinit var viewModel: FragmentFuncionariosViewModel
    private lateinit var adapter: FuncionariosAdapter
    private val sharedViewModel by activityViewModels<SharedViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFragmentFuncionariosBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        getListaDeFuncionarios()
        onClickListeners()
    }

    private fun onClickListeners() {
        binding.btnOpenCvCadastroFuncionario.setOnClickListener {
            binding.btnOpenCvCadastroFuncionario.visibility = View.GONE
            binding.root.setBackgroundColor(resources.getColor(R.color.alt_black3))
            binding.cvCadastroFuncionario.visibility = View.VISIBLE
        }

        binding.ivCloseCadastroFuncionario.setOnClickListener {
            binding.cvCadastroFuncionario.visibility = View.GONE
            changeBackGroundColor()
            changeCardVisibility()

        }

        binding.btnSelecionarFoto.setOnClickListener {
            openFileChooser()
        }

        binding.cbCorteCabelo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    requireContext(),
                    "cliquei no serviço de corte de cabelo",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.corteCabelo = "Corte de Cabelo"
                viewModel.contadorServicos++
            } else {
                Toast.makeText(
                    requireContext(),
                    "desmarquei o serviço de corte de cabelo",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.corteCabelo = ""
                viewModel.contadorServicos--


            }
        }
        binding.cbPinturaCabelo.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    requireContext(),
                    "cliquei no serviço de pintura de cabelo",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.pinturaCabelo = "Pintura de Cabelo"
                viewModel.contadorServicos++

            } else {
                Toast.makeText(
                    requireContext(),
                    "desmarquei o serviço de pintura de cabelo",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.pinturaCabelo = ""
                viewModel.contadorServicos--


            }
        }

        binding.cbManicure.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    requireContext(),
                    "cliquei no serviço de manicure",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.manicure = "Manicure"
                viewModel.contadorServicos++

            } else {
                Toast.makeText(
                    requireContext(),
                    "desmarquei o serviço de manicure",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.manicure = ""
                viewModel.contadorServicos--

            }
        }

        binding.cbPedicure.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(
                    requireContext(),
                    "cliquei no serviço de pedicure",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.pedicure = "Pedicure"
                viewModel.contadorServicos++

            } else {
                Toast.makeText(
                    requireContext(),
                    "desmarquei o serviço de pedicure",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.pedicure = ""
                viewModel.contadorServicos--


            }
        }

        binding.btnCadastrarFuncionRio.setOnClickListener {
           //aqui eu vou cadastrar o novo funcionario
            if (viewModel.contadorServicos > 0) {
                //aqui vai iniciar o save da foto do funcionario e
                //informacoes sobre o funcionario
                sendPhotoToStorage()
            }
            if (viewModel.contadorServicos == 0) {
                Toast.makeText(
                    requireContext(),
                    "Marque pelo menos 1 serviço para esse funcionário",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }

    private fun sendPhotoToStorage() {
        viewModel.imageUri?.let { viewModel.sendPhotoToStorage(it, getFileExtension(viewModel.imageUri!!)) }

    }

    private fun getListaDeFuncionarios() {
        viewModel.getListaDeFuncionarios()
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 1000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
            && data.data != null
        ) {

            viewModel.imageUri = data.data!!
            Picasso.get().load(viewModel.imageUri).into(binding.ivFuncionario)
            binding.ivFuncionario.visibility = View.VISIBLE
            binding.btnCadastrarFuncionRio.visibility = View.VISIBLE
        }
    }


    private fun iniciarNovaListaNaRecyclerView(listaFuncionarios: List<Funcionario>) {

        adapter = FuncionariosAdapter(listaFuncionarios, this)
        binding.recyclerFuncionarios.adapter = adapter
    }


    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentFuncionariosViewModel::class.java]
        initCollectors()

    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.enviouFotoParaOStorage.collectLatest {
                    urlFoto->
                    if(urlFoto != ""){
                        viewModel.saveFuncionarioInDatabase(urlFoto,
                        binding.etNomeFuncionario.text.toString(),
                        binding.etCargoFuncionario.text.toString())
                    }else{
                        showToast("Houve um erro no salvamento da foto!")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.salvouFuncionarioNoFirebase.collectLatest {
                        salvou->
                    if(salvou){
                        showToast("novo funcionário salvo com sucesso")
                        binding.cvCadastroFuncionario.visibility = View.GONE
                        changeBackGroundColor()
                        changeCardVisibility()
                    }else{
                        showToast("ocorreu uma falha ao salvar o funcionário")

                    }

                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosOsFuncionarios.collectLatest {
                        listaFuncionarios->


                    iniciarNovaListaNaRecyclerView(listaFuncionarios)



                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onFuncionarioClick(func: Funcionario) {
        sharedViewModel.chamarMetodoDeMudarFragmento(func)
    }

    private fun changeBackGroundColor() {
        binding.root.setBackgroundColor(resources.getColor(R.color.white))

    }

    private fun changeCardVisibility() {
        binding.btnOpenCvCadastroFuncionario.visibility = View.VISIBLE
    }


}