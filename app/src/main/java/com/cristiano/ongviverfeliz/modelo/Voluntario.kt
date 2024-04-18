package com.cristiano.ongviverfeliz.modelo

data class Voluntario(
     var nome: String? = null,
     var dataNasc: String?  = null,
     var telefone: String?  = null,
     var rg: String?  = null,
     var cpf: String?  = null,
     var email: String?  = null,
     var rua: String?  = null,
     var numeroResidencia: String?  = null,
     var bairro: String?  = null,
     var cidade: String?  = null,
     var estado: String?  = null,
     var atvVoluntaria: String?  = null,
     var diaSemana: String?  = null,
     var formaTrabalho: String?  = null,
     var horario: String?  = null
){
     constructor() : this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
}
