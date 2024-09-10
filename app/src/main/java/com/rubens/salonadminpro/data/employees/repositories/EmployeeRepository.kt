package com.rubens.salonadminpro.data.employees.repositories

import com.rubens.salonadminpro.data.models.Funcionario

interface EmployeeRepository {
    fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: List<Funcionario>)->Unit)

    fun saveFuncionarioInDatabase(urlFoto: String, salvouFuncionarioNoDatabase: (salvou: Boolean)->Unit, corteCabelo: String,
                                  pinturaCabelo: String,
                                  manicure: String,
                                  pedicure: String,
                                  nomeFuncionario: String,
                                  cargoFuncionario: String)

    fun pegarFolgas(funcionarioKey: String, pegouFolga: (folgaDoFuncionario: String)->Unit)

    fun salvarNovaFolga(funcionarioKey: String, dataFolga: String, salvouNovaFolga: (salvou: Boolean)->Unit)


}