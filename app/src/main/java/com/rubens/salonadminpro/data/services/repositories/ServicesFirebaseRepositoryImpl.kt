package com.rubens.salonadminpro.data.services.repositories

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rubens.salonadminpro.data.models.Funcionario
import com.rubens.salonadminpro.data.models.Servico

class ServicesFirebaseRepositoryImpl(
    private val servicesDb: DatabaseReference,
    private val employeesDb: DatabaseReference
): ServicesRepository {

    private val tag = "ServicesFirebaseRepositoryImpl"


    override fun updateListaDeServicos(funcionarioKey: String, listaDeServicosAtualizada: (listaServicos: ArrayList<String>) -> Unit) {
        val listaServicosDoFuncionario: ArrayList<String> = arrayListOf()

        employeesDb.orderByChild("childKey").equalTo(funcionarioKey)
            .addListenerForSingleValueEvent(
                object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach {
                            it.children.forEach {
                                if(it.key == "listaServicos"){
                                    it.children.forEach {
                                            servico->
                                        listaServicosDoFuncionario.add(servico.value as String)

                                    }
                                }
                            }
                        }
                        listaDeServicosAtualizada(listaServicosDoFuncionario)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(tag, "error on updateListaDeServicos ${error.message}")

                    }

                }
            )
    }



    override fun updateServicosDoFuncionario(
        childKey: String,
        funcionario: Funcionario,
        atualizouListaDeServicos: (atualizouServicos: Boolean) -> Unit
    ) {
        employeesDb.child(childKey)
            .setValue(funcionario)
            .addOnSuccessListener {
                atualizouListaDeServicos(true)
            }.addOnFailureListener {
                atualizouListaDeServicos(false)

            }
    }



    override fun removerServicoDoFuncionario(
        funcionarioKey: String,
        servicoFuncionario: String,
        deletouServicoDoFuncionario: (deletou: Boolean, servico: String)->Unit
    ) {
        employeesDb.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val servicoSnapshot = snapshot.child("listaServicos")
                val servico = servicoSnapshot.children.find { it.value == servicoFuncionario }

                servico?.let {
                    it.ref.removeValue().addOnCompleteListener {
                            task->
                        if(task.isSuccessful){
                            deletouServicoDoFuncionario(true, servicoFuncionario)
                        }else{
                            deletouServicoDoFuncionario(false, "")
                        }
                    }
                }?:deletouServicoDoFuncionario(false, "")


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "error on removerServicoDoFuncionario ${error.message}")
            }

        })

    }




    override fun saveServicoInDatabase(
        urlFoto: String?,
        nomeServico: String,
        salvouNovoServico: (salvou: Boolean) -> Unit
    ) {
        val servico = Servico(nomeServico = nomeServico, linkImagem = urlFoto!!)
        val servicoId = servicesDb.push().key
        servicesDb.child(servicoId!!).setValue(servico).addOnSuccessListener {
            salvouNovoServico(true)
        }.addOnFailureListener {
            salvouNovoServico(false)
        }
    }


    override fun pegarTodosServicos(pegouTodosOsServicos: (servicos: ArrayList<Servico>) -> Unit) {
        val listaDeServicos: ArrayList<Servico> = arrayListOf()

        servicesDb.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val servico: Servico? = it.getValue(Servico::class.java)
                    listaDeServicos.add(servico!!)

                }
                pegouTodosOsServicos(listaDeServicos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(tag, "error on pegarTodosServicos")

            }

        })
    }
}