# Trabajo Práctico Especial - Grupo 7

## Integrantes

* Kessy Leonardo
* Julian Fidel Yañez
* Marcos David Tami
* Agustina Magali Fennema
* Nicolás Valentín Gómez

---

## Consignas

### 1) Creación del esquema de la base de datos

Crear un programa utilizando **JDBC** que permita crear el esquema de la base de datos mediante sentencias SQL.

El programa deberá establecer una conexión con la base de datos y ejecutar las sentencias necesarias para crear las tablas, claves primarias, claves extranjeras y demás restricciones correspondientes al modelo.

### 2) Carga de datos desde archivos CSV

Considerar los archivos **CSV proporcionados** y desarrollar un programa utilizando **JDBC** que permita cargar los datos de los archivos a la base de datos.

Para la lectura de los archivos CSV se recomienda utilizar la biblioteca **Apache Commons CSV**, disponible en Maven Central.

Ejemplo:

```java
CSVParser parser = CSVFormat.DEFAULT
        .withHeader()
        .parse(new FileReader("productos.csv"));

for (CSVRecord row : parser) {
    System.out.println(row.get("idProducto"));
    System.out.println(row.get("nombre"));
    System.out.println(row.get("valor"));
}
```

El programa deberá leer los registros de los archivos CSV y realizar las inserciones correspondientes en las tablas de la base de datos mediante JDBC.

### 3) Producto que más recaudó

Escribir un programa utilizando **JDBC** que retorne el producto que obtuvo la mayor recaudación.

Se define **recaudación** como:

> **Cantidad de productos vendidos × valor del producto**

El programa deberá realizar la consulta correspondiente sobre la base de datos y mostrar el producto que haya generado la mayor recaudación.

### 4) Clientes ordenados por facturación

Escribir un programa utilizando **JDBC** que imprima una lista de clientes, ordenada de manera descendente según el monto total que se les facturó.

La lista deberá mostrar los clientes comenzando por aquel al que se le haya facturado el mayor importe.
