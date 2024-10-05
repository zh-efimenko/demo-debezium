## Included transformations

| **Transformation**     | **Description**                                                                                              |
|------------------------|--------------------------------------------------------------------------------------------------------------|
| **Cast**               | Приведение полей или всего ключа или значения к определенному типу                                           |
| **DropHeaders**        | Удаление заголовков по имени                                                                                 |
| **ExtractField**       | Извлечение определенного поля из Struct и Map и включение только этого поля в результаты                     |
| **Filter**             | Удаляет сообщения из дальнейшей обработки. Используется с предикатом для селективной фильтрации сообщений    |
| **Flatten**            | Преобразование вложенной структуры данных в плоскую                                                          |
| **HeaderFrom**         | Копирование или перемещение полей в ключе или значении в заголовки записи                                    |
| **HoistField**         | Обертывание всего события как одного поля внутри Struct или Map                                              |
| **InsertField**        | Добавление поля с использованием статических данных или метаданных записи                                    |
| **InsertHeader**       | Добавление заголовка с использованием статических данных                                                     |
| **MaskField**          | Замена поля на допустимое нулевое значение для типа (0, пустая строка и т.д.) или на пользовательскую замену |
| **RegexRouter**        | Изменение темы записи на основе исходной темы, строки замены и регулярного выражения                         |
| **ReplaceField**       | Фильтрация или переименование полей                                                                          |
| **SetSchemaMetadata**  | Изменение имени или версии схемы                                                                             |
| **TimestampConverter** | Преобразование меток времени между различными форматами                                                      |
| **TimestampRouter**    | Изменение темы записи на основе исходной темы и временной метки. Полезно для записи в разные таблицы         |
| **ValueToKey**         | Замена ключа записи новым ключом, сформированным из подмножества полей значения записи                       |

### Examples

```text
// TIMEZONE
transforms=convertTimezone
transforms.convertTimezone.type=io.debezium.transforms.TimezoneConverter
transforms.convertTimezone.converted.timezone=Pacific/Easter

"created_at": "2011-01-11T16:40:30.123456789+00:00"
```

```text
// FILTERING
transforms=filter
transforms.filter.type=io.debezium.transforms.Filter
transforms.filter.language=jsr223.groovy
transforms.filter.condition=value.op == 'u' && value.before.id == 2
```

## Snapshots

1. always
2. initial - default
3. initial_only
4. no_data
5. never - deprecated see no_data.
6. when_needed
7. configuration_based
8. custom

## Publications

1. all_tables - default
2. disabled
3. filtered
4. no_tables

### Полезные SQL запросы

```sql
SELECT *
FROM pg_replication_slots;


SELECT pg_drop_replication_slot('debezium');
SELECT pg_drop_replication_slot('debezium1');


SELECT *
FROM pg_publication;

SELECT c.relname AS table_name
FROM pg_publication p
         JOIN
     pg_publication_rel pr ON p.oid = pr.prpubid
         JOIN
     pg_class c ON pr.prrelid = c.oid
WHERE p.pubname = 'debezium_demo';


INSERT INTO public.dbz_signal
VALUES ('signal-1', 'execute-snapshot', '{"data-collections": ["custom.category"]}');

INSERT INTO public.dbz_signal
VALUES ('signal-2', 'execute-snapshot',
        '{"data-collections": ["custom.category"], "additional-conditions":[{"data-collection": "custom.category", "filter": "status=''OPENED''"}]}');
```

### Полезный JSON для сигнального топика

key: pgsql.demo

```json
{
	"type": "execute-snapshot",
	"data": {
		"data-collections": [
			"custom.category"
		],
		"type": "incremental"
	}
}
```
