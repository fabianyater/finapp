# Pendientes

## Suscripciones

Feature separada sobre las transacciones recurrentes, orientada a servicios que se pagan periódicamente.

**Qué agrega vs una recurrente normal:**
- Flag `isSubscription` en la txn recurrente (o entidad propia si crece mucho)
- **Alerta previa al cobro** — notificación X días antes de que se renueve (ej. "En 3 días se cobra Netflix $X")
- **Tracking de trial** — fecha en que termina el período gratis y pasa a cobrar
- **Vista consolidada** — panel con todas las suscripciones activas, total mensual y total anual proyectado
- **Estado** — activa / cancelada / pausada (para no perder el historial al cancelar)

**Notificaciones específicas:**
- `SUBSCRIPTION_REMINDER` — N días antes del próximo cobro
- `TRIAL_ENDING` — cuando el trial está por vencer

---

## Balance histórico por mes

El dashboard muestra el saldo actual de la cuenta, pero cuando navegas a meses anteriores
debería mostrar el saldo que tenía la cuenta al cierre de ese mes.

**Solución propuesta:**
Endpoint `GET /accounts/{accountId}/balance?asOf=<ISO date>` que calcule server-side:
`initialBalance + SUM(income) - SUM(expense)` con `occurredOn <= asOf`.
En el frontend usar `dateTo` del mes seleccionado como parámetro.

---

## Comparar mes actual vs mes anterior

En el dashboard mostrar la variación porcentual de gastos/ingresos
respecto al mes anterior, por categoría y en total. Ej: "Gastos +12% vs mes anterior".

---

## Gráfica de tendencia multi-mes

Vista de ingresos vs gastos de los últimos 6-12 meses en una sola gráfica,
para ver tendencias sin navegar mes a mes. Barras agrupadas o líneas superpuestas.

---

## Metas de ahorro

Definir una meta con monto objetivo y fecha límite, asociarla a una cuenta,
y mostrar progreso visual. Ej: "Quiero ahorrar $2M para diciembre".

**Notificaciones relacionadas:**
- `GOAL_MILESTONE` — al alcanzar 50%, 80% y 100% de la meta
- `GOAL_AT_RISK` — si el ritmo de ahorro no alcanza para cumplir la fecha límite

---

## Transferencias entre cuentas

Mover saldo de una cuenta propia a otra (ej. de Bancolombia a Nequi).
Registrar como dos transacciones vinculadas: egreso en origen + ingreso en destino.
Que no distorsione los reportes de gastos/ingresos.

---

## Exportar transacciones

Descargar el historial de transacciones de un período en CSV o Excel.
Filtros: rango de fechas, cuenta, categoría.

---

## Notificaciones adicionales

- **Gasto inusual** — cuando una txn supera X veces el promedio histórico de esa categoría (`UNUSUAL_EXPENSE`)
- **Resumen mensual** — al cierre de cada mes, resumen de gastos vs ingresos vs mes anterior (`MONTHLY_SUMMARY`)
- **Presupuesto con saldo al final del mes** — "Te quedan $X en Comida y 5 días del mes" (`BUDGET_REMAINING`)

---

## Historial de actividad (audit log)

Registro de todas las acciones relevantes: crear/editar/eliminar transacciones,
compartir una cuenta, agregar o quitar miembros, etc.

Cada entrada: acción, usuario, fecha, y diff (ej. monto anterior → nuevo).
Útil en cuentas compartidas para saber quién hizo qué.

**Solución propuesta:**
Tabla `activity_logs(id, user_id, account_id, entity_type, entity_id, action, metadata JSONB, occurred_at)`.
Vista dentro de cada cuenta compartida y en el perfil del usuario.
