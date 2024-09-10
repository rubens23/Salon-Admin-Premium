package com.rubens.salonadminpro.data.services.repositories

import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico

interface ServicesRepository {
    fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>)->Unit)


    fun updateServicosDoFuncionario(childKey: String, funcionario: Funcionario, atualizouListaDeServicos: (atualizouServicos: Boolean)->Unit)


    fun removerServicoDoFuncionario(funcionarioKey: String, servicoFuncionario: String, deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit)


    fun saveServicoInDatabase(urlFoto: String?, nomeServico: String, salvouNovoServico: (salvou: Boolean)->Unit)


    fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Servico>)->Unit)

}