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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rubens.salonadminpro.R
import com.rubens.salonadminpro.data.models.Servico
import com.rubens.salonadminpro.databinding.FragmentFragmentServicosBinding
import com.rubens.salonadminpro.view.adapters.ServicosAdapter
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FragmentServicos : Fragment() {

    private lateinit var binding: FragmentFragmentServicosBinding
    private lateinit var viewModel: FragmentServicosViewModel
    private lateinit var adapter: ServicosAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentFragmentServicosBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewModel()
        pegarTodosOsServicos()
        onClickListeners()

    }

    private fun pegarTodosOsServicos() {
        viewModel.pegarTodosOsServicos()
    }

    private fun onClickListeners() {
        binding.btnOpenCvCadastroServico.setOnClickListener {
            binding.btnOpenCvCadastroServico.visibility = View.GONE
            binding.root.setBackgroundColor(resources.getColor(R.color.alt_black3))
            binding.cvCadastroServico.visibility = View.VISIBLE
        }
        binding.ivCloseCadastroServico.setOnClickListener {
            binding.cvCadastroServico.visibility = View.GONE
            changeBackGroundColor()
            changeCardVisibility()

        }
        binding.btnSelecionarFoto.setOnClickListener {
            openFileChooser()
        }
        binding.btnCadastrarServico.setOnClickListener {
            sendServicePhotoToFirebaseStorage()
        }
    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1001
            && resultCode == AppCompatActivity.RESULT_OK
            && data != null
            && data.data != null){

            viewModel.imgUri = data.data!!
            Picasso.get().load(viewModel.imgUri).into(binding.ivServico)
            binding.ivServico.visibility = View.VISIBLE
            binding.btnCadastrarServico.visibility = View.VISIBLE

        }
    }

    private fun sendServicePhotoToFirebaseStorage() {
        if (viewModel.imgUri != null) {
            var fileExt = getFileExtension(viewModel.imgUri!!)
            if (fileExt != null) {
                viewModel.sendPhotoToStorage(viewModel.imgUri!!, fileExt)
            } else {
                showToast("nenhuma foto foi selecionada")
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val cr = requireActivity().contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cr.getType(uri))
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun changeBackGroundColor() {
        binding.root.setBackgroundColor(resources.getColor(R.color.white))

    }

    private fun changeCardVisibility() {
        binding.btnOpenCvCadastroServico.visibility = View.VISIBLE
    }

    private fun initRecyclerViewComNovaListaDeServicos(listaDeServicos: ArrayList<Servico>) {

        adapter = ServicosAdapter(listaDeServicos)
        binding.recyclerServicos.adapter = adapter
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(requireActivity())[FragmentServicosViewModel::class.java]
        initCollectors()

    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.enviouFotoParaOStorage.collectLatest {
                        urlFoto->
                    var nomeServico = binding.etNomeServico.text.toString()
                    if(urlFoto != "" && nomeServico.isNotEmpty()){
                        viewModel.saveServicoInDatabase(urlFoto, nomeServico)
                    }else{
                        showToast("Houve um erro no salvamento da foto!")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.salvouNovoServico.collectLatest {
                        salvou->
                    if(salvou){
                        showToast("Serviço salvo com sucesso!")
                        binding.cvCadastroServico.visibility = View.GONE
                        changeBackGroundColor()
                        changeCardVisibility()
                    }else{
                        showToast("Ocorreu uma falha ao salvar o serviço. Verifique o preenchimento dos dados e tente novamente!")
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.pegouTodosOsServicos.collectLatest {
                        listaDeServicos->
                    initRecyclerViewComNovaListaDeServicos(listaDeServicos)

                }
            }
        }
    }


}