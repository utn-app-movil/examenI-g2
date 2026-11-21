 examenI - Gestión de Citas

 Errores encontrados y soluciones

  No se guardaba la fecha y hora correctamente

 Al guardar una cita con fecha y hora, la operación fallaba.

 Sin `util.kt` no se puede usar `parseStringToDateTimeModern` para convertir el texto del `EditText` (`yyyy-MM-dd HH:mm`) a `LocalDateTime`.

Solucion Agregar `util.kt` con `parseStringToDateTimeModern` y `formatDateTime`. En el XML asegurar que el `EditText` de fecha tenga hint `2025-12-31 14:00`.

. Error duplicados

Al pulsar Guardar, la app mostraba error o no completaba la operación.

Falta `util.showDialogCondition()` para confirmar guardado/eliminación. Falta validación de teléfono/correo y especialidad seleccionada.

 Implementar `requestSave()` que valide campos, construya el `AppAppointment` y llame a `viewModel.addAppointment()` 


 Algunas listas no se llamaban bien / no se mostraban

 `RecyclerView` aparecía vacío o con nombres de variables inconsistentes.

En `AppListActivity.kt` la variable se llamó `recyclerView` en lugar de `appointmentList`. El adapter no se reasignaba en `onResume()`.

Renombrar a `appointmentList` y en `onResume()` hacer:
```kotlin
appointmentList.adapter = AppListAdapter(viewModel.getAppointments(), this)
```

