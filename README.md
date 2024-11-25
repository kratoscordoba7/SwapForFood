# SwapFood

SwapFood es una aplicación móvil diseñada para ayudar a un grupo de amigos a decidir en qué restaurante comer. A través de un juego, los usuarios pueden deslizar entre opciones de restaurantes y votar por sus favoritos. El restaurante con más votos será el ganador.

## 🛠️ Tecnologías utilizadas

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose
- **Entorno de desarrollo:** Android Studio

## 🚀 Cómo ejecutar el proyecto

Sigue estos pasos para configurar y ejecutar la aplicación en tu entorno local:

1. Clona este repositorio:
   ```bash
   git clone https://github.com/kratoscordoba7/SwapForFood/tree/codelabs
   ```
2. Cambia al branch `codelabs`:
   ```bash
   git checkout codelabs
   ```
3. Abre el proyecto en Android Studio.
4. Asegúrate de que tienes un emulador configurado o un dispositivo físico conectado.
5. Ejecuta el proyecto presionando el botón **Run**.

## 📱 Funcionalidades principales

### Pantallas

1. **MainScreen**: 
   - Permite al usuario crear una sala o unirse a una mediante un código único.
   - Utiliza el componente `NumericInputField` para capturar códigos de entrada con un diseño estilizado.
      - `NumericInputField`hace uso del glassmorfismo con ayuda de una librería externa.

2. **CreateRoomScreen**:
   - Sala de espera para los jugadores antes de iniciar el juego.
   - Esta pantalla se reutiliza dinámicamente para mostrar una vista diferente al creador de la sala y a quienes se unen a ella.
      - Según se haya creado la sala o se esté uniendo a una existente se modificará el front con una variable de control

3. **startGameScreen**:
   - Pantalla principal del juego donde los jugadores deslizan tarjetas de restaurantes hacia la derecha (me gusta) o izquierda (no me gusta).
   - Muestra restaurantes como tarjetas utilizando el componente personalizado `RestaurantCard`.
      - `RestaurantCard` hace uso de la estructura de datos `Restaurant, la cual se compone de un nombre, una foto y una carta. 
   - Los restaurantes acumulan puntos dependiendo de las acciones tomadas por los usuarios durante el juego. El restaurante más votado es el ganador. Actualmente en esta parte del desarrollo está a un nivel muy simbólico y no funciona como lo hará en el mínimo viable.

#### Ejemplo del flujo en `StartGameScreen`

La pantalla utiliza una lista predefinida de restaurantes. Los jugadores interactúan con las tarjetas arrastrándolas hacia la izquierda o derecha, con un cambio dinámico de color de fondo para indicar sus elecciones.

```kotlin
val restaurants = listOf(
    Restaurant(R.drawable.restaurant_1, "McDonald's", "Hamburguesas"),
    Restaurant(R.drawable.restaurant_2, "Telepizza", "Pizza"),
    Restaurant(R.drawable.restaurant_3, "ADK", "Kebab")
)
```

Al finalizar las elecciones, la aplicación muestra un mensaje con la puntuación obtenida.

## 🤝 Colaboradores

- [![GitHub](https://img.shields.io/badge/GitHub-Heliot%20J.%20Segura%20Gonzalez-purple?style=flat-square&logo=github)](https://github.com/kratoscordoba7)
- [![GitHub](https://img.shields.io/badge/GitHub-Marcos%20V%C3%A1zquez%20Tasc%C3%B3n-blue?style=flat-square&logo=github)](https://github.com/DerKom)

---

¡Gracias por revisar SwapFood! Si tienes sugerencias o deseas contribuir, no dudes en enviar un pull request.
