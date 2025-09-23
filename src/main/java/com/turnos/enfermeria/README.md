# 🏥 Sistema de Gestión de Turnos - Hospital Universitario San José

Una aplicación backend desarrollada en **Spring Boot** para la gestión integral de turnos de trabajo del personal del Hospital Universitario San José, incluyendo administración de contratos, equipos, procesos de atención y reportería.

## 📋 Descripción del Proyecto

Este sistema permite administrar de manera eficiente y completa los turnos de trabajo del personal hospitalario, integrando la gestión de contratos, equipos multidisciplinarios, procesos de atención médica y generación de reportes especializados para optimizar la cobertura hospitalaria.

### ✨ Características Principales

- 👥 **Gestión Integral de Personal**: Administración completa de usuarios, personas, roles y títulos académicos
- 📋 **Gestión de Contratos**: Control detallado de contratos con tipos de turno, atención y procesos asociados
- 👨‍⚕️ **Equipos Multidisciplinarios**: Formación automática de equipos por categorías y subcategorías con historial de cambios
- 🏥 **Estructura Hospitalaria**: Gestión jerárquica de macroprocesos, procesos, servicios, secciones y subsecciones
- 📅 **Cuadros de Turnos**: Sistema avanzado de programación mensual con versionado y excepciones
- ⏰ **Gestión de Turnos**: Asignación, modificación y seguimiento de turnos individuales con historial completo
- 📊 **Reportes Especializados**: Generación de reportes por mes, cuadro y persona con análisis detallado
- 🔄 **Historial y Auditoría**: Seguimiento completo de cambios en turnos, cuadros y equipos
- 🔐 **Sistema de Autenticación**: Registro, login y gestión de sesiones seguras
- 📱 **API RESTful Completa**: Más de 150 endpoints especializados para integración

## 🛠️ Tecnologías Utilizadas

### Backend

- **Java 17+** - Lenguaje de programación
- **Spring Boot 3.x** - Framework principal
- **Spring Data JPA** - Persistencia y gestión de entidades
- **Spring Security** - Autenticación y autorización
- **Spring Web** - API REST
- **OpenAPI 3.1** - Documentación automática de API

### Base de Datos

- **MySQL/PostgreSQL** - Base de datos relacional principal

### Herramientas Adicionales

- **Gradle** - Gestión de dependencias
- **Swagger UI** - Interfaz interactiva de documentación API
- **JUnit 5** - Testing unitario e integración
- **Docker** - Contenedorización y despliegue

## 🏗️ Arquitectura del Sistema

```
app_turnos/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hospital/turnos/
│   │   │       ├── controller/           # Controladores REST organizados por módulos
│   │   │       │   ├── UsuariosController
│   │   │       │   ├── TurnosController
│   │   │       │   ├── ContratosController
│   │   │       │   ├── CuadroTurnosController
│   │   │       │   ├── ServiciosController
│   │   │       │   ├── ReportesController
│   │   │       │   └── AuthController
│   │   │       ├── service/              # Lógica de negocio
│   │   │       ├── repository/           # Repositorios JPA
│   │   │       ├── entity/               # Entidades del dominio
│   │   │       ├── dto/                  # Data Transfer Objects
│   │   │       ├── config/               # Configuraciones
│   │   │       └── util/                 # Utilidades
│   │   └── resources/
│   │       ├── application.properties           # Configuración principal
│   │       └── data.sql                  # Datos iniciales
│   └── test/                             # Pruebas unitarias e integración
├── build.gradle.kts                      # Configuración Gradle Kotlin
└── README.md
```

## 📊 Modelo de Datos Principal

### Entidades Core

#### **Persona**

- **Persona**: Datos personales (nombres, apellidos, documento, email, teléfono)
- **TitulosFormacionAcademica**: Títulos académicos del personal

#### **Contratos y Vinculación**

- **Contrato**: Contratos con supervisor, objeto, fechas y estado
- **UsuarioContrato**: Vinculación persona-contrato-rol
- **TipoTurno**: Especialidades y disponibilidad por contrato
- **TipoAtencion**: Tipos de atención médica por contrato

#### **Equipos de Trabajo**

- **Equipo**: Equipos con nombres autogenerados por categoría/subcategoría
- **PersonaEquipo**: Relación many-to-many entre personas y equipos
- **CambiosEquipo**: Historial de modificaciones en equipos
- **CambiosPersonaEquipo**: Historial de cambios de personal en equipos

#### **Estructura Hospitalaria**

- **Macroprocesos**: Procesos principales del hospital
- **Procesos**: Procesos específicos dentro de macroprocesos
- **BloqueServicio**: Bloques de servicios hospitalarios
- **Servicio**: Servicios específicos por bloque y proceso
- **SeccionesServicio**: Secciones dentro de servicios
- **SubseccionesServicio**: Subsecciones específicas
- **ProcesosAtencion**: Procesos de atención por cuadro de turno

#### **Gestión de Turnos**

- **CuadroTurno**: Programación mensual con versionado y estado
- **Turno**: Turnos individuales con fechas, horas y jornadas
- **CambiosTurno**: Historial completo de modificaciones
- **CambiosCuadroTurno**: Historial de cambios en cuadros

## 🚀 Instalación y Configuración

### Prerrequisitos

- **Java 17 o superior**
- **Gradle 7.5+**
- **PostgreSQL 14+**
- **Git**

### 1. Clonar el Repositorio

```bash
git clone https://github.com/jorgeibarra87/app_turnos.git
cd app_turnos
```

### 2. Configurar Base de Datos

```sql
-- Para MySQL
CREATE DATABASE app_turnos_husj CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'turnos_user'@'localhost' IDENTIFIED BY 'turnos_password_2024';
GRANT ALL PRIVILEGES ON app_turnos_husj.* TO 'turnos_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuración de Variables de Entorno

```properties
# application.properties o variables de entorno
spring.datasource.url=jdbc:mysql://localhost:3306/app_turnos_husj
spring.datasource.username=turnos_user
spring.datasource.password=turnos_password_2024
spring.jpa.hibernate.ddl-auto=update
server.port=8081
```

### 4. Ejecutar la Aplicación

```bash
# Desarrollo
./gradlew bootRun

# Producción
./gradlew build
java -jar build/libs/app-turnos-husj-1.0.0.jar
```

### 5. Verificar Instalación

- **Aplicación**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Documentación API**: http://localhost:8081/v3/api-docs

## 🔌 API Endpoints Principales

### 👥 Gestión de Usuarios (`/usuario`)

```
GET    /usuario                 # Listar usuarios
POST   /usuario                 # Crear usuario
GET    /usuario/{id}            # Obtener usuario por ID
PUT    /usuario/{id}            # Actualizar usuario
GET    /usuario/{id}/equipos    # Equipos del usuario
GET    /usuario/{id}/titulos    # Títulos del usuario
GET    /usuario/{id}/roles      # Roles del usuario
```

### 📋 Gestión de Personas (`/persona`)

```
GET    /persona                 # Listar personas
POST   /persona                 # Crear persona
GET    /persona/{id}            # Obtener persona
PUT    /persona/{id}            # Actualizar persona
```

### 📄 Contratos (`/contrato`)

```
GET    /contrato                        # Listar contratos
POST   /contrato                        # Crear contrato
GET    /contrato/{id}                  # Obtener contrato
PUT    /contrato/{id}                  # Actualizar contrato
POST   /contrato/guardar               # Guardar contrato completo
GET    /contrato/contratoTotal/{id}    # Obtener contrato completo
```

### 👨‍⚕️ Equipos (`/equipo`)

```
GET    /equipo                          # Listar equipos
POST   /equipo                          # Crear equipo
POST   /equipo/with-generated-name      # Crear con nombre autogenerado
GET    /equipo/{id}                    # Obtener equipo
PUT    /equipo/{id}                    # Actualizar equipo
GET    /equipo/{id}/miembros-perfil    # Miembros con perfil completo
```

### 📅 Cuadros de Turnos (`/cuadro-turnos`)

```
GET    /cuadro-turnos                   # Listar cuadros
POST   /cuadro-turnos                   # Crear cuadro
GET    /cuadro-turnos/{id}             # Obtener cuadro
PUT    /cuadro-turnos/{id}             # Actualizar cuadro
GET    /cuadro-turnos/{id}/historial   # Historial de cambios
POST   /cuadro-turnos/restaurar/{id}   # Restaurar versión anterior
```

### ⏰ Turnos (`/turnos`)

```
GET    /turnos                          # Listar turnos
POST   /turnos                          # Crear turno
GET    /turnos/{id}                    # Obtener turno
PUT    /turnos/{id}                    # Actualizar turno
GET    /turnos/{id}/historial          # Historial del turno
GET    /turnos/cuadro/{id}             # Turnos por cuadro
PUT    /turnos/cambiar-estado          # Cambio masivo de estado
```

### 🏥 Servicios y Estructura (`/servicio`, `/macroprocesos`, `/procesos`)

```
GET    /macroprocesos                   # Listar macroprocesos
GET    /procesos                        # Listar procesos
GET    /servicio                        # Listar servicios
GET    /seccionesServicio              # Listar secciones
GET    /subseccionesServicio           # Listar subsecciones
```

### 📊 Reportes (`/reportes`)

```
GET    /reportes/{anio}/{mes}/{cuadroId}    # Reporte de turnos por filtros
```

### 📋 Títulos Académicos (`/titulosFormacionAcademica`)

```
GET    /titulosFormacionAcademica       # Listar títulos
POST   /titulosFormacionAcademica       # Crear título
GET    /titulosFormacionAcademica/{id}  # Obtener título
PUT    /titulosFormacionAcademica/{id}  # Actualizar título
DELETE /titulosFormacionAcademica/{id}  # Eliminar título
```

## 📈 Funcionalidades Adicionales

### Sistema de Versionado

- **Versionado automático** de cuadros de turnos y turnos individuales
- **Restauración** de versiones anteriores por ID de cambio
- **Historial** de modificaciones con fecha, usuario y detalles

### Generación Automática de Equipos

- **Nombres automáticos** basados en categoría y subcategoría
- **Gestión de subcategorías** dinámicas por categoría
- **Historial de cambios** en equipos y membresía

### Sistema de Estados

- **Estados de cuadros**: Ejemplo:(BORRADOR, APROBADO, PUBLICADO, ARCHIVADO)
- **Estados de turnos**: Ejemplo:(PROGRAMADO, CONFIRMADO, COMPLETADO, CANCELADO)

### Reportería

- **Reportes por período** (año/mes) y cuadro específico
- **Análisis de carga** de trabajo por persona
- **Métricas de cumplimiento** y cobertura
- **Exportación** en múltiples formatos

### Gestión de Excepciones

- **Turnos de excepción** para situaciones especiales
- **Validaciones** de disponibilidad y conflictos

## 🧪 Testing y Validación

### Ejecutar Pruebas

```bash
# Todas las pruebas
./gradlew test

# Pruebas de integración
./gradlew integrationTest

# Con perfil de testing
./gradlew test -Ptest
```

### Pruebas de API

```bash
# Verificar salud de la aplicación
curl http://localhost:8081/actuator/health

# Probar autenticación
curl -X POST http://localhost:8081/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{"documento":"12345678","password":"test123"}'

# Listar usuarios
curl http://localhost:8081/usuario
```

## 📚 Documentación Técnica

### Patrones de Diseño Implementados

- **Repository Pattern**: Para acceso a datos
- **DTO Pattern**: Para transferencia de datos
- **Builder Pattern**: Para construcción de entidades complejas
- **Strategy Pattern**: Para diferentes tipos de turnos y reportes

### Validaciones de Negocio

- **Validación de disponibilidad** antes de asignar turnos
- **Verificación de títulos** requeridos por proceso
- **Control de solapamiento** de horarios
- **Validación de estados** en transiciones

### Optimizaciones

- **Lazy loading** para relaciones complejas
- **Caché** de consultas frecuentes
- **Paginación** en listados extensos
- **Índices** optimizados en base de datos

## 🤝 Contribución y Desarrollo

### Estructura de Commits

```
feat: agregar endpoint para reportes mensuales
fix: corregir validación de turnos solapados
docs: actualizar documentación de API
refactor: optimizar consultas de equipos
```

### Estándares de Código

- **Java Code Conventions** de Oracle
- **Spring Boot Best Practices**
- **RESTful API Design** principles
- **Cobertura mínima** del 80% en pruebas

### Guía de Desarrollo

1. **Fork** el repositorio
2. **Crear rama** feature: `git checkout -b feature/nueva-funcionalidad`
3. **Implementar** con pruebas correspondientes
4. **Documentar** endpoints en OpenAPI
5. **Commit** siguiendo convenciones
6. **Push** y crear **Pull Request**

## 📞 Contacto y Soporte

### Información del Proyecto

- **Desarrollador Principal**: Jorge Ibarra
- **Email**: jorgeibarra87@gmail.com
- **Hospital**: Hospital Universitario San José
- **GitHub**: [@jorgeibarra87](https://github.com/jorgeibarra87)

### URLs Importantes

- **Hospital**: https://hospitalsanjose.gov.co/
- **API Base**: http://localhost:8081
- **Swagger**: http://localhost:8081/swagger-ui.html

## 📄 Información Legal

Este proyecto está desarrollado para el **Hospital Universitario San José** como parte del sistema integral de gestión hospitalaria.

**Versión**: 1.0.0  
**Última Actualización**: 2025  
**Licencia**: Propiedad del Hospital Universitario San José

---

**Hospital Universitario San José** - Sistema de Gestión de Turnos  
_Optimizando la gestión del talento humano en salud_
