@startuml
title Clínica Veterinaria - Modelo de Clases (con Proxy, Observer y Factory)


' 1. PAQUETE: USUARIOS Y PERSONAL
' ============================================================
package "UsuariosPersonal" {

  abstract class Personal {
    +idPersonal: Long
    +nombres: String
    +apellidos: String
    +correo: String
    +telefono: String
  }

  class Administrador
  class AuxiliarVeterinario
  class Recepcionista
  class Veterinario {
    +especialidad: String
    +registroProfesional: String
  }

  class Usuario {
    +idUsuario: Long
    +username: String
    +password: String
    +estado: boolean
  }

  enum RolUsuario {
    ADMIN
    VETERINARIO
    AUXILIAR
    RECEPCIONISTA
    PROPIETARIO
    ESTUDIANTE
  }

  Personal <|-- Administrador
  Personal <|-- AuxiliarVeterinario
  Personal <|-- Recepcionista
  Personal <|-- Veterinario

  Usuario --> RolUsuario : rol
}

' ============================================================
' 2. PAQUETE: PACIENTES
' ============================================================
package "Pacientes" {

  class Propietario {
    +idPropietario: Long
    +nombre: String
    +telefono: String
    +correo: String
    +direccion: String
  }

  class Mascota {
    +idMascota: Long
    +nombre: String
    +fechaNac: Date
    +sexo: String
    +color: String
    +observaciones: String
  }

  class Especie {
    +idEspecie: Long
    +nombre: String
  }

  class Raza {
    +idRaza: Long
    +nombre: String
  }

  Propietario "1" -- "0..*" Mascota : posee
  Especie "1" -- "0..*" Raza : clasifica
  Mascota "0..1" -- "1" Raza : pertenece
}

' ============================================================
' 3. PAQUETE: PRÁCTICAS
' ============================================================
package "Practicas" {

  class SupervisorPractica {
    +idSupervisor: Long
    +nombre: String
    +correo: String
  }

  class Estudiante {
    +idEstudiante: Long
    +nombre: String
    +universidad: String
    +semestre: int
    +correoInstitucional: String
  }

  class EvaluacionEstudiante {
    +idEvaluacion: Long
    +fecha: Date
    +nota: Double
    +observaciones: String
  }

  class Bitacora {
    +idBitacora: Long
    +fecha: Date
    +actividad: String
  }

  SupervisorPractica "1" -- "0..*" EvaluacionEstudiante : evalúa
  Estudiante "1" -- "0..*" Bitacora : registra
  Estudiante "1" -- "0..*" EvaluacionEstudiante : tiene
}

' Estudiante y Supervisor pueden tener usuario
Estudiante "0..1" -- "1" Usuario : credenciales
SupervisorPractica "0..1" -- "1" Usuario : credenciales

' ============================================================
' 4. PAQUETE: AGENDA Y SERVICIOS
' ============================================================
package "AgendaServicios" {

  class Horario {
    +idHorario: Long
    +diaSemana: int
    +horaInicio: String
    +horaFin: String
  }

  class Cita {
    +idCita: Long
    +fechaHora: Date
    +motivo: String
    +observaciones: String
  }

  class Servicio {
    +idServicio: Long
    +nombre: String
    +descripcion: String
    +precioBase: Double
    +duracionMinutos: int
    +requiereInsumos: boolean
    +esEmergencia: boolean
  }

  enum EstadoCita {
    PROGRAMADA
    CANCELADA
    ATENDIDA
  }

  enum CategoriaServicio {
    CLINICO
    QUIRURGICO
    ESTETICO
    EMERGENCIA
  }

  enum TipoServicio {
    CONSULTA_GENERAL
    VACUNACION
    CIRUGIA
    DESPARASITACION
    BAÑO
    PELUQUERIA
  }

  Cita "1" -- "1" Servicio : solicita
  Cita "1" -- "1" EstadoCita : estado
  Servicio "1" -- "1" CategoriaServicio
  Servicio "1" -- "1" TipoServicio
  Veterinario "1" -- "0..*" Cita : atiende
  Veterinario "1" -- "0..*" Horario : disponibilidad
  Horario "1" -- "0..*" Cita : agenda
}

' Cita sobre una mascota
Cita "1" -- "1" Mascota : paciente

' ============================================================
' 5. PAQUETE: FACTURACIÓN
' ============================================================
package "Facturacion" {

  class Factura {
    +idFactura: Long
    +fecha: Date
    +total: Double
  }

  class DetalleFactura {
    +idDetalle: Long
    +cantidad: int
    +precioUnitario: Double
    +subtotal: Double
  }

  enum MetodoPago {
    EFECTIVO
    TARJETA
    TRANSFERENCIA
  }

  Factura "1" -- "0..*" DetalleFactura : contiene
  Factura "1" -- "1" MetodoPago : pago
}

' Factura al propietario
Factura "1" -- "1" Propietario : factura_a
Cita "0..1" -- "0..1" Factura : origen

' ============================================================
' 6. PAQUETE: INVENTARIO
' ============================================================
package "Inventario" {

  class Proveedor {
    +idProveedor: Long
    +nombre: String
    +telefono: String
    +correo: String
  }

  class TipoInsumo {
    +idTipo: Long
    +nombre: String
  }

  class Insumo {
    +idInsumo: Long
    +nombre: String
    +descripcion: String
    +stockMinimo: int
    +stockActual: int
    +costoUnitario: Double
  }

  class Inventario {
    +idInventario: Long
    +fechaActualizacion: Date
  }

  enum EstadoInsumo {
    DISPONIBLE
    AGOTADO
    EN_PEDIDO
  }

  Proveedor "1" -- "0..*" Insumo : suministra
  TipoInsumo "1" -- "0..*" Insumo : clasifica
  Inventario "1" -- "0..*" Insumo : contiene
  Insumo "1" -- "1" EstadoInsumo : estado
  Servicio "0..*" -- "0..*" Insumo : usa
}

' ============================================================
' 7. PAQUETE: CLÍNICO
' ============================================================
package "Clinico" {

  class HistoriaClinica {
    +idHistoria: Long
    +fechaApertura: Date
  }

  class EvolucionClinica {
    +idEvolucion: Long
    +fecha: Date
    +descripcion: String
    +signosVitales: String
  }

  class Tratamiento {
    +idTratamiento: Long
    +descripcion: String
    +duracionDias: int
  }

  class RecetaMedica {
    +idReceta: Long
    +indicaciones: String
  }

  HistoriaClinica "1" -- "0..*" EvolucionClinica : registra
  EvolucionClinica "0..*" -- "0..*" Tratamiento : para
  EvolucionClinica "0..*" -- "0..*" RecetaMedica : prescribe
  Mascota "1" -- "0..*" HistoriaClinica : tiene
}

Cita "1" -- "0..1" HistoriaClinica : solicita

' ============================================================
' 8. PAQUETE: COMUNICACIONES
' ============================================================
package "Comunicaciones" {

  class RecordatorioCita {
    +idRecordatorio: Long
    +fechaEnvio: Date
  }

  class Correo {
    +idCorreo: Long
    +asunto: String
    +contenido: String
  }

  class Notificacion {
    +idNotificacion: Long
    +mensaje: String
    +fechaEnvio: Date
    +canal: String
  }

  RecordatorioCita "1" -- "1" Cita : recuerda
  Notificacion "1" -- "0..1" Cita : notifica
  Correo "1" -- "0..1" Propietario : envia_a
}

' ============================================================
' 9. PATRONES DE DISEÑO
' ============================================================
package "Patrones de Diseño" {

  ' -------- PROXY (para Inventario) --------
  interface IInventario {
    +obtenerInsumos()
    +actualizarStock(idInsumo: Long, cantidad: int)
  }

  class InventarioReal {
    +obtenerInsumos()
    +actualizarStock(idInsumo: Long, cantidad: int)
  }

  class InventarioProxy {
    -usuarioActual: Usuario
    -real: InventarioReal
    +obtenerInsumos()
    +actualizarStock(idInsumo: Long, cantidad: int)
  }

  IInventario <|.. InventarioReal
  IInventario <|.. InventarioProxy
  InventarioProxy --> InventarioReal : delega

  note right of InventarioProxy
    PROXY
    - Verifica rol de Usuario
    - Puede hacer caché
    - Protege InventarioReal
  end note

  ' -------- OBSERVER (para Cita) --------
  interface CitaSubject {
    +agregarObserver(o: CitaObserver)
    +removerObserver(o: CitaObserver)
    +notificar()
  }

  interface CitaObserver {
    +actualizar(cita: Cita)
  }

  class NotificacionObserver {
    +actualizar(cita: Cita)
  }

  class RecordatorioObserver {
    +actualizar(cita: Cita)
  }

  CitaSubject <|.. Cita
  CitaObserver <|.. NotificacionObserver
  CitaObserver <|.. RecordatorioObserver

  Cita ..> CitaObserver : notifica >
  NotificacionObserver ..> Notificacion
  RecordatorioObserver ..> RecordatorioCita

  note bottom of Cita
    OBSERVER
    Cuando cambia el estado de la cita
    se notifica a los observadores
    que generan notificaciones o recordatorios.
  end note

  ' -------- FACTORY (para Servicios) --------
  abstract class ServicioFactory {
    +crearServicio(nombre: String): Servicio
  }

  class ServicioClinicoFactory {
    +crearServicio(nombre: String): Servicio
  }

  class ServicioEsteticoFactory {
    +crearServicio(nombre: String): Servicio
  }

  ServicioFactory <|-- ServicioClinicoFactory
  ServicioFactory <|-- ServicioEsteticoFactory

  ServicioClinicoFactory ..> Servicio : crea
  ServicioEsteticoFactory ..> Servicio : crea

  note right of ServicioFactory
    FACTORY METHOD
    Centraliza la creación de Servicios
    según la categoría.
  end note
}

@enduml
