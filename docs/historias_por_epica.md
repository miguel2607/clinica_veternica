🧩 Especificación Funcional — Sistema de Gestión Veterinaria
EPICA 1: Gestión de Usuarios y Mascotas
H1: Consultar Mascotas

Descripción:
Como Veterinario, quiero ver todas las mascotas registradas para acceder a su información antes de una cita.

Interacción de roles:

Recepcionista: puede buscar mascotas.

Administrador: tiene acceso completo.

Atributos / Entradas:

Texto: nombre de mascota, nombre de propietario (≥2 caracteres).

IDs: idMascota, idPropietario.

Clasificación: especie, raza, sexo, esterilizado.

Estado/fechas: activa, fechaRegistro (desde–hasta).

Paginación/orden: page, size {10,20,50}, sortBy {Nombre, ID, UltimaVisita}, sortDir.

Validación: rangos de fecha válidos; page ≥ 0.

H2: Registrar Mascota

Descripción:
Como Recepcionista, quiero ingresar los datos de una nueva mascota y su propietario para que pueda acceder a los servicios veterinarios.

Interacción de roles:

Usuario: entrega los datos al recepcionista.

Veterinario: consulta el registro para atención.

Administrador: valida y audita el registro.

Atributos / Entradas:

Mascota: nombre, especie, raza, sexo, estado (Activo/Inactivo), fechaNacimiento, observaciones.

Propietario: nombre, documento, teléfono, correo.

Sistema: genera automáticamente idMascota y asocia idPropietario.

Validaciones: campos obligatorios, sin duplicados de nombre+propietario.

H3: Actualizar Información de Mascota

Descripción:
Como Administrador, quiero editar datos de mascotas registradas para corregir errores o actualizar información.

Interacción de roles:

Recepcionista: solicita corrección si es necesario.

Veterinario: consulta el registro actualizado.

Atributos / Entradas:

Datos editables: nombre, especie, raza, sexo, estado, fechaNacimiento, observaciones.

Identificadores: idMascota.

Control del sistema: fechaModificación, usuarioEditor.

Validaciones: no dejar campos vacíos; evitar duplicados.

H4: Eliminar Registro de Mascota Inactiva

Descripción:
Como Administrador, quiero eliminar registros de mascotas sin actividad por más de 2 años para mantener limpia la base de datos.

Interacción de roles:

Recepcionista: reporta mascotas inactivas.

Veterinario: no puede acceder a registros eliminados.

Atributos / Entradas:

idMascota, fechaÚltimaActividad, estado (Activo/Inactivo).

Sistema: fechaEliminación, usuarioEliminó.

Validaciones: solo eliminar si lleva >2 años inactiva.

H5: Registrar Propietario

Descripción:
Como Recepcionista, quiero registrar un nuevo propietario para vincularlo con sus mascotas.

Interacción de roles:

Usuario: proporciona datos personales.

Administrador: revisa y aprueba el registro.

Atributos / Entradas:

Datos: nombre, documento, teléfono, correo.

Sistema: idPropietario automático, fechaRegistro.

Validaciones: documento único, correo válido.

EPICA 2: Gestión de Servicios
H6: Crear Tipo de Servicio

Descripción:
Como Administrador, quiero registrar nuevos tipos de servicios veterinarios (consulta, cirugía, vacunación, etc.) para que puedan seleccionarse al agendar una cita o generar una factura.

Interacción de roles:

Recepcionista: selecciona el servicio.

Veterinario: aplica el servicio y registra resultados.

Usuario: visualiza el servicio en cita/factura.

Atributos / Entradas:

nombre, descripción, costoBase, duraciónEstimada.

Sistema: idServicio automático, fechaCreación.

Validaciones: nombre único, campos obligatorios.

H7: Registrar Prestación del Servicio

Descripción:
Como Veterinario, quiero registrar los detalles del servicio (observaciones, diagnóstico, resultados) para mantener el historial médico completo.

Interacción de roles:

Recepcionista: actualiza estado de cita a “Atendida”.

Usuario: recibe notificación.

Administrador: audita registros clínicos.

Atributos / Entradas:

diagnóstico, observaciones, tratamiento, resultados.

idCita, idMascota, idServicio, fechaAtención, veterinarioResponsable.

Validaciones: campos completos; cita activa.

H8: Asignar Servicio a Cita

Descripción:
Como Recepcionista, quiero asociar un servicio a cada cita para definir el propósito de la atención.

Interacción de roles:

Veterinario: revisa el tipo de servicio.

Usuario: elige el servicio.

Administrador: verifica asignación.

Atributos / Entradas:

idCita, idServicio, fechaCita, mascota, propietario.

Estado del servicio (activo/inactivo).

Validaciones: un servicio por cita, servicio activo.

H9: Calificar Atención del Servicio

Descripción:
Como Usuario, quiero calificar la atención recibida para mejorar la calidad del servicio.

Interacción de roles:

Recepcionista: ve promedios.

Administrador: analiza satisfacción.

Veterinario: recibe retroalimentación.

Atributos / Entradas:

idServicio, idCita, calificación (1–5), comentario, fecha, usuarioCalifica.

Validaciones: solo servicios “Atendidos”.

H10: Generar Informe de Servicios Prestados

Descripción:
Como Administrador, quiero generar reportes de servicios realizados para analizar productividad y demanda.

Interacción de roles:

Veterinario: aparece como responsable.

Recepcionista: filtra por fecha.

Usuario: sin acceso.

Atributos / Entradas:

filtros: fechaInicio, fechaFin, veterinario, tipoServicio.

Salida: total, ingresos, promedios.

Formato: PDF o Excel.

EPICA 3: Gestión de Citas
H11: Solicitar Cita

Descripción:
Como Usuario, quiero solicitar una cita seleccionando veterinario, fecha, hora y servicio para agendar atención.

Interacción de roles:

Recepcionista: valida y confirma.

Veterinario: recibe asignación.

Administrador: supervisa.

Atributos / Entradas:

mascota, servicio, fecha, hora, veterinario.

Sistema: idCita y confirmación automática.

Validaciones: disponibilidad; una cita activa por mascota.

H12: Reprogramar Cita

Descripción:
Como Recepcionista, quiero modificar fecha/hora de cita para evitar conflictos.

Interacción de roles:

Usuario: puede solicitar.

Veterinario: recibe notificación.

Administrador: supervisa.

Atributos / Entradas:

idCita, nuevaFecha, nuevaHora, motivo.

Validaciones: solo citas pendientes.

H13: Cancelar Cita

Descripción:
Como Usuario, quiero cancelar una cita para liberar espacio en la agenda.

Interacción de roles:

Recepcionista: procesa cancelación.

Veterinario: recibe aviso.

Administrador: mantiene registro.

Atributos / Entradas:

idCita, motivoCancelación.

Sistema: fechaCancelación, usuario, estado.

Validaciones: antes de la hora de inicio.

H14: Confirmar Asistencia

Descripción:
Como Recepcionista, quiero confirmar asistencia del propietario y mascota antes de la cita.

Interacción de roles:

Usuario: recibe recordatorio.

Veterinario: visualiza confirmadas.

Administrador: controla estadísticas.

Atributos / Entradas:

idCita, estadoAsistencia, fechaConfirmación, usuarioConfirmador.

Validaciones: cita activa.

H15: Registrar Atención de Cita

Descripción:
Como Veterinario, quiero marcar cita como “Atendida” para generar historia clínica y factura.

Interacción de roles:

Recepcionista: cambia estado.

Administrador: controla finalizadas.

Usuario: recibe resumen.

Atributos / Entradas:

idCita, idMascota, idVeterinario, servicioRealizado, observaciones, fecha, hora.

Validaciones: solo veterinarios; cita activa.

EPICA 4: Gestión Historia Clínica
H16: Crear Historia Clínica

Descripción:
Como Veterinario, quiero crear historia base para registrar estado general de la mascota.

Interacción de roles:

Recepcionista: asocia con cita.

Usuario: solo visualiza.

Atributos / Entradas:

motivo, diagnóstico, tratamiento, observaciones, peso, temperatura.

idMascota, idVeterinario, fechaRegistro.

Validaciones: todos los campos completos.

H17: Consultar Historia Clínica

Descripción:
Como Usuario, quiero ver la historia clínica de mi mascota.

Interacción de roles:

Veterinario: autoriza visualización.

Administrador: controla permisos.

Atributos / Entradas:

idMascota, idPropietario.

Datos visibles: motivo, diagnóstico, tratamiento, observaciones, fecha, veterinario.

Validaciones: solo propietario registrado.

H18: Registrar Vacunación

Descripción:
Como Veterinario, quiero añadir información de vacunas aplicadas.

Interacción de roles:

Usuario: puede ver.

Administrador: genera reportes.

Atributos / Entradas:

fechaAplicación, tipoVacuna, lote, dosis, veterinarioResponsable, idMascota.

Validaciones: campos completos.

H19: Adjuntar Exámenes Clínicos

Descripción:
Como Veterinario, quiero adjuntar documentos o imágenes de exámenes.

Interacción de roles:

Usuario: puede descargar resultados.

Atributos / Entradas:

tipoExamen, archivoAdjunto, fechaExamen, veterinarioResponsable.

Formatos: PDF, JPG, PNG.

Validaciones: tamaño y tipo correcto.

H20: Generar Resumen Médico

Descripción:
Como Administrador, quiero generar reportes consolidados de historias clínicas.

Interacción de roles:

Veterinario: valida información.

Recepcionista: genera reportes.

Atributos / Entradas:

especie, veterinario, fechas, tratamiento.

Salida: total de atenciones, diagnósticos, resultados.

Formato: PDF/Excel.

EPICA 5: Gestión de Inventario
H21: Registrar Insumo

Descripción:
Como Administrador, quiero registrar nuevos insumos.

Interacción de roles:

Recepcionista: consulta stock.

Veterinario: usa insumos.

Atributos / Entradas:

nombre, tipo, cantidad, proveedor, fechaIngreso, fechaVencimiento.

idInsumo, idAdministrador.

Validaciones: no duplicados, fechaVencimiento obligatoria.

H22: Actualizar Stock

Descripción:
Como Recepcionista, quiero registrar entradas/salidas de insumos.

Interacción de roles:

Administrador: aprueba ajustes.

Atributos / Entradas:

idInsumo, tipoMovimiento, cantidad, motivo, fechaRegistro.

idMovimiento, idRecepcionista.

Validaciones: cantidad ≥ 0, motivo obligatorio.

H23: Descontar Insumos Usados

Descripción:
Como Veterinario, quiero descontar insumos al realizar procedimientos.

Interacción de roles:

Administrador: audita movimientos.

Atributos / Entradas:

idInsumo, idServicio, cantidadUsada, fechaUso, idVeterinario.

Validaciones: cantidad ≤ stock disponible.

H24: Alertar Vencimientos

Descripción:
Como Administrador, quiero recibir alertas de insumos próximos a vencer.

Atributos / Entradas:

idInsumo, nombre, fechaVencimiento, estadoAlerta.

Validaciones: revisión automática cada 24 h.

H25: Generar Reporte de Inventario

Descripción:
Como Administrador, quiero generar reportes del stock disponible.

Atributos / Entradas:

idInsumo, nombre, tipo, cantidadActual, fechaActualización.

Filtros: por fecha, tipo, estado.

Salida: PDF/Excel.

EPICA 6: Gestión de Pagos
H26: Generar Factura

Descripción:
Como Recepcionista, quiero generar factura después de la cita atendida.

Interacción de roles:

Veterinario: confirma servicio.

Usuario: recibe factura.

Administrador: consolida.

Atributos / Entradas:

númeroFactura, fecha, idCita, idUsuario, subtotal, IVA, total.

Validaciones: solo citas “Atendidas”.

H27: Registrar Pago

Descripción:
Como Usuario, quiero pagar en efectivo o tarjeta.

Atributos / Entradas:

idPago, idFactura, métodoPago, monto, fechaPago.

Métodos: efectivo, tarjeta, transferencia.

Validaciones: monto = total factura.

H28: Consultar Facturas

Descripción:
Como Administrador, quiero buscar facturas por fecha o cliente.

Atributos / Entradas:

idFactura, fecha, cliente, montoTotal, estado, métodoPago.

Filtros: rangoFechas, veterinario, cliente, estado.

Salida: listado exportable.

H29: Reemitir Factura

Descripción:
Como Recepcionista, quiero reimprimir facturas anteriores.

Atributos / Entradas:

idFactura, fechaEmisión, cliente, estado, montoTotal.

Condición: factura “Pagada”.

Salida: PDF o físico.

H30: Generar Reporte de Ingresos

Descripción:
Como Administrador, quiero generar un reporte de ingresos diarios.

Atributos / Entradas:

idReporte, fechaInicio, fechaFin, totalIngresos, métodoPago, usuario.

Filtros: fechas, servicio, veterinario.

Formato: PDF/Excel.