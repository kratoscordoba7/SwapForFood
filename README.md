# SwapForFood
<h1 align="center">🌟 Aplicación final de curso </h1>

<img align="left" width="160" height="160" src="img/octo_cat.png"></a>
Se ha realizado una aplicación que se basa en la decisión, por un grupo de amigos, de un restaurante para ir a comer, cercano a ellos. 

*Trabajo realizado por*:
- [![GitHub](https://img.shields.io/badge/GitHub-Heliot%20J.%20Segura%20Gonzalez-purple?style=flat-square&logo=github)](https://github.com/kratoscordoba7)
- [![GitHub](https://img.shields.io/badge/GitHub-Marcos%20V%C3%A1zquez%20Tasc%C3%B3n-blue?style=flat-square&logo=github)](https://github.com/DerKom)


## 🛠️ Librerías Utilizadas

![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-%2300C4B3?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Ktor](https://img.shields.io/badge/Ktor-%23621FE8?style=for-the-badge&logo=ktor&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-%230095D5?style=for-the-badge&logo=kotlin&logoColor=white)

---

## 🚀 Cómo ejecutar el proyecto

Sigue estos pasos para configurar y ejecutar la aplicación en tu entorno local:

1. Clona este repositorio:
   ```bash
   git clone https://github.com/kratoscordoba7/SwapForFood.git
   ```
2. Abre el proyecto en Android Studio.
3. Asegúrate de que tienes un emulador configurado o un dispositivo físico conectado.
4. Ejecuta el proyecto presionando el botón **Run**.
---

### Introducción
 Se abarcarán las siguientes partes:

### Objetivos 📖
La apliación está diseñada bajo el siguiente concepto:
Imagínese que ha quedado con un grupo de amigos para ver un película o cualquier evento social. Durante el desarrollo de dicho evento en el espectro común surge la idea de comer algo pero no se consigue consensuar algo fácilmente, en este contexto entra nuestra apliación, SwapForFood. Mediante la aplicación se podrá elegir mayoritariamente qué restaurante es el más aceptado en la zona y al cuál se irá. La aplicación opta por un diseño ágil, rápido y divertido para evitar la larga discusión que puede tomar llegar a un punto común entre un grupo de personas.

### Diseño 🎨
Este apartado se puede definir en dos subapartados. Por un lado se tiene la distribución de los paquetes y por otro la experiencia de usuario (UX):
- Paquetes:
  - Server:
    - Archivo connectionManager: Maneja la vida de la conexión, el envío, recibimiento y clasificación de mesajes con el servidor.
    - Archivo client: Aporta utilidades usadas por el connectionManager (crear conexión, eliminar conexión, recibir mensajes).
  - UI:
    - Paquete Screens: Se definen todas las pantallas de la aplicación. Estas pantallas observa el cambio en los StateFlow del connectionManager para cambiar la vista a tiempo real.
    - Paquete Componets: Se definen segmentos de código usado por las vistas (Screens) para manejar mejor la responsabilidad única y la modularidad del código.
  - Utils:
    - Se definen archivos que aportan utilidades usadas tanto en las vistas como en el manejo de la conexión con el servidor:
      - Conseguir la ip del dispositivo.
      - Conseguir la ubicación del dispositivo.
      - Mostrar diálogos informativos o que piden input (Desconexiones de la sala, introducción del username al unirte a la sala).
  - DataStructures:
    - Message: Convenio de información para la comunicación con el servidor.
    - Restaurant: Modelo que es usado para el juego, usado en la vista startGameScreen     

### Arquitectura 🏢
La arquitectura usada en la aplicación es MVVM, la única mayormente aceptada por la comunidad de devs de Kotlin, aquí se explica su uso:
- Los modelos (Restaurant) son usados en las vistas. (M)
- Las vistas observan los StateFlow del ViewModel. (V)
- El ViewModel, que es el connectionManager, obtiene información del servidor y cambia ciertos StateFlow, usando los modelos de Restaurant y Message. (VM)

### Funcionalidades 📱

---

## 📚 Bibliografía

1. [Jetpack Compose](https://developer.android.com/compose)
2. [Ktor](https://ktor.io)
3. [Google APIs](https://cloud.google.com/apis)
4. [Kotlin](https://kotlinlang.org)

---

**Universidad de Las Palmas de Gran Canaria**  

EII - Grado de Ingeniería Informática

---
