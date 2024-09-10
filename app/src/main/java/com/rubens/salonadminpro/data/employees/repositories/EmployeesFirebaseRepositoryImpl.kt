package com.rubens.salonadminpro.data.employees.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rubens.salonadminpro.data.models.Funcionario


class EmployeesFirebaseRepositoryImpl(
    private val employeesDb: DatabaseReference
): EmployeeRepository {

    private val tag = "EmployeesFirebaseRepositoryImpl"



    override fun pegarTodosFuncionarios(pegouTodosOsFuncionarios: (todosOsFuncionarios: List<Funcionario>) -> Unit) {
        val employeesSet = mutableSetOf<Funcionario>()
        employeesDb.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val funcionarioMap = it.value as Map<*, *>
                    val funcionario = Funcionario(
                        nome = if(funcionarioMap["nome"] != null)funcionarioMap["nome"] as String else "",
                        cargo = if(funcionarioMap["cargo"] != null)funcionarioMap["cargo"] as String else "",
                        linkImagem = if(funcionarioMap["linkImagem"] != null)funcionarioMap["linkImagem"] as String else "",
                        funcionarioDisponivel = if(funcionarioMap["funcionarioDisponivel"] != null)funcionarioMap["funcionarioDisponivel"] as Boolean else true,
                        dataFolga = if(funcionarioMap["dataFolga"] != null)funcionarioMap["dataFolga"] as String else "",
                        childKey = if(funcionarioMap["childKey"] != null)funcionarioMap["childKey"] as String else "",
                        listaServicos = when (val listaServicosObj = funcionarioMap["listaServicos"]) {
                            is Map<*, *> -> listaServicosObj.values.map { it as String }  // Caso seja um Map
                            is List<*> -> listaServicosObj.map { it as String }  // Caso seja uma List
                            else -> emptyList()  // Se for nulo ou outro tipo
                        },
                        isSelected = if(funcionarioMap["isSelected"] != null)funcionarioMap["isSelected"] as Boolean else false
                    )

                    Log.d("resolvendoHM", "funcionario obtido: $funcionario")
                    employeesSet.add(funcionario)




                }





                pegouTodosOsFuncionarios(employeesSet.toList())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "error on pegarTodosFuncionarios: ${error.message}")
            }

        })
    }




    override fun saveFuncionarioInDatabase(
        urlFoto: String,
        salvouFuncionarioNoDatabase: (salvou: Boolean) -> Unit,
        corteCabelo: String,
        pinturaCabelo: String,
        manicure: String,
        pedicure: String,
        nomeFuncionario: String,
        cargoFuncionario: String

    ) {
        val listaDeServicos = fazerListaDeServicos(corteCabelo,
            pinturaCabelo,
            manicure,
            pedicure)

        val uploadId = employeesDb.push().key

        val funcionario = Funcionario(
            nome = nomeFuncionario,
            cargo = cargoFuncionario,
            linkImagem = urlFoto,
            childKey = uploadId!!,
            listaServicos = listaDeServicos
        )

        employeesDb.child(uploadId).setValue(funcionario)
            .addOnSuccessListener {
                salvouFuncionarioNoDatabase(true)

            }.addOnFailureListener {
                salvouFuncionarioNoDatabase(false)
            }


    }



    override fun pegarFolgas(
        funcionarioKey: String,
        pegouFolga: (folgaDoFuncionario: String) -> Unit
    ) {
        employeesDb.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val folga = obterFolgaDoSnapshot(snapshot)
                        pegouFolga(folga?:"Nenhuma folga definida")

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "error on pegarFolgas ${error.message}")
                    }

                }
            )
    }

    private fun obterFolgaDoSnapshot(snapshot: DataSnapshot): String? {
        val folga = snapshot.children.firstOrNull{it.key == "dataFolga"}
        return folga?.value as? String

    }


    override fun salvarNovaFolga(
        funcionarioKey: String,
        dataFolga: String,
        salvouNovaFolga: (salvou: Boolean) -> Unit
    ) {
        employeesDb.child(funcionarioKey).child("dataFolga")
            .setValue(dataFolga)
            .addOnSuccessListener {
                salvouNovaFolga(true)
            }.addOnFailureListener {
                salvouNovaFolga(false)
            }
    }

    private fun fazerListaDeServicos(corteCabelo: String, pinturaCabelo: String, manicure: String, pedicure: String): List<String> {
        val listaServicos = mutableListOf<String>()
        if (corteCabelo == "Corte de Cabelo") listaServicos.add(corteCabelo)
        if (pinturaCabelo == "Pintura de Cabelo") listaServicos.add(pinturaCabelo)
        if (manicure == "Manicure") listaServicos.add(manicure)
        if (pedicure == "Pedicure") listaServicos.add(pedicure)
        return listaServicos
    }
}