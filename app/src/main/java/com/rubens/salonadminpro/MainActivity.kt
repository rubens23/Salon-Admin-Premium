package com.rubens.salonadminpro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.databinding.ActivityMainBinding
import com.rubens.salonadminpro.view.FragmentAgenda
import com.rubens.salonadminpro.view.FragmentDetalhesFuncionario
import com.rubens.salonadminpro.view.FragmentFuncionarios
import com.rubens.salonadminpro.view.FragmentServicos
import com.rubens.salonadminpro.view.adapters.AdapterViewPager
import com.rubens.salonadminpro.viewmodels.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var contents: ArrayList<Fragment> = ArrayList()
    private lateinit var adapterViewPager: AdapterViewPager
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupFragmentsList()
        initViewModel()

        setupViewPager()
    }

    private fun initViewModel() {
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        initCollectors()
    }

    private fun initCollectors() {
        lifecycleScope.launch {
            sharedViewModel.precisaMudarParaFragmentoDetalhesFuncionario.collectLatest {
                func->
                mudarParaFragmentoDetalhesFuncionario(func)
            }
        }
    }



    override fun onStart() {
        super.onStart()
        onClickListeners()
    }

    private fun onClickListeners() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager){
                tab, position->
            if(position == 0){
                tab.text = "Funcionários"
            }
            if(position == 1){
                tab.text = "Agenda"
            }
            if(position == 2){
                tab.text = "Serviços"
            }
        }.attach()
    }

    private fun setupViewPager(){
        adapterViewPager = AdapterViewPager(this, contents)
        binding.viewPager.adapter = adapterViewPager
        binding.viewPager.isUserInputEnabled = false
    }

    private fun setupFragmentsList(){
        contents.add(FragmentFuncionarios())
        contents.add(FragmentAgenda())
        contents.add(FragmentServicos())
    }

    private fun mudarParaFragmentoDetalhesFuncionario(func: Funcionario) {
        contents.clear()
        contents.add(FragmentDetalhesFuncionario.newInstance(func))
        contents.add(FragmentAgenda())
        contents.add(FragmentServicos())

        setupViewPager()
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0){
            supportFragmentManager.popBackStack()
        }else{
            //aqui eu vou ter que colocar um when para quando
            //estiver em outros fragmentos que nao seja a tela
            //de detalhes do funcionario
            mudarParaFragmentoFuncionario()


        }
    }

    private fun mudarParaFragmentoFuncionario() {
        contents.clear()
        contents.add(FragmentFuncionarios())
        contents.add(FragmentAgenda())
        contents.add(FragmentServicos())

        setupViewPager()
    }


}