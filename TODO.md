# Pendientes

## Balance histórico por mes

El dashboard muestra el saldo actual de la cuenta, pero cuando navegas a meses anteriores
debería mostrar el saldo que tenía la cuenta al cierre de ese mes.

**Solución propuesta:**
Endpoint `GET /accounts/{accountId}/balance?asOf=<ISO date>` que calcule server-side:
`initialBalance + SUM(income) - SUM(expense)` con `occurredOn <= asOf`.
En el frontend usar `dateTo` del mes seleccionado como parámetro.

---

## Gráfica de tendencia multi-mes

Vista de ingresos vs gastos de los últimos 6-12 meses en una sola gráfica,
para ver tendencias sin navegar mes a mes. Barras agrupadas o líneas superpuestas.

---

## Metas de ahorro

Definir una meta con monto objetivo y fecha límite, asociarla a una cuenta,
y mostrar progreso visual. Ej: "Quiero ahorrar $2M para diciembre".

---

## Reglas automáticas de categorización

Si la descripción contiene cierta palabra → asignar categoría automáticamente.
Ej: "Netflix" → Suscripciones, "Rappi" → Domicilios.
Muy útil para usuarios que importan transacciones desde el banco.

---

## Deudas entre personas

Registrar "le presté a Juan $50k" o "le debo a María $30k".
Ver saldo pendiente por persona y marcar como saldado.
Diferente a una transferencia — no mueve saldo de cuenta.

---

## Split de transacciones

Dividir una transacción entre varias categorías.
Ej: compra en supermercado de $150k → $80k Alimentación + $40k Aseo + $30k Hogar.

---

## Adjuntos en transacciones

Subir foto de factura o recibo asociada a una transacción.

---

## Comparar mes actual vs mes anterior

En el dashboard mostrar la variación porcentual de gastos/ingresos
respecto al mes anterior, por categoría y en total. Ej: "Gastos +12% vs mes anterior".

---

## Historial de actividad (audit log)

Registro de todas las acciones relevantes del usuario: crear/editar/eliminar transacciones,
compartir una cuenta, agregar o quitar miembros, cambiar configuración, etc.

Cada entrada tendría: acción, usuario que la ejecutó, fecha, y detalle (ej. monto anterior → nuevo).
Útil especialmente en cuentas compartidas para saber quién hizo qué.

**Casos de uso:**
- "¿Quién eliminó esa transacción?"
- "¿Cuándo me agregaron a esta cuenta?"
- "¿Qué cambió en esta transacción?"

**Solución propuesta:**
Tabla `activity_logs(id, user_id, account_id, entity_type, entity_id, action, metadata JSONB, occurred_at)`.
`metadata` guarda el diff o contexto relevante (ej. `{"from": 50000, "to": 80000}`).
Vista en el perfil del usuario y dentro de cada cuenta compartida.

---

## Reportes por email

Envío automático mensual de un resumen de gastos/ingresos al correo del usuario.
