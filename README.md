# TarjetasMaiz - Plugin Minecraft ⚽

[![Download latest release](https://img.shields.io/github/v/release/MaizXD-jar/TarjetasMaiz?style=for-the-badge)](https://github.com/MaizXD-jar/TarjetasMaiz/releases/)
[![License](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Spigot](https://img.shields.io/badge/Spigot-Compatible-orange)](https://www.spigotmc.org/resources/tarjetasmaiz-%E2%9A%BD.117647/)

Un plugin para Minecraft que simula el sistema de tarjetas amarillas y rojas, diseñado para servidores Spigot y Paper.  Ideal para servidores de temática de fútbol o roleplay de árbitros.

## Descripción

TarjetasMaiz añade un sistema de sanciones mediante tarjetas amarillas y rojas, permitiendo a administradores y moderadores del servidor penalizar a los jugadores. El plugin es configurable, personalizable y compatible con servidores Spigot y Paper.

## Características

*   **Compatibilidad:**
    *   Diseñado para servidores Spigot y Paper.
    *   (Indica aquí las versiones específicas compatibles de Minecraft)
*   **Tarjetas Amarillas:**
    *   Emite tarjetas amarillas a jugadores.
    *   Configura el número de tarjetas amarillas antes de una tarjeta roja.
    *   Define un tiempo de espera (cooldown) entre la aplicación de tarjetas amarillas.
    *   Guarda la información de los jugadores con tarjetas amarillas en un archivo JSON.
*   **Tarjetas Rojas:**
    *   Emite tarjetas rojas a jugadores.
    *   Configura un baneo temporal al recibir una tarjeta roja.
    *   Define un tiempo de espera (cooldown) entre la aplicación de tarjetas rojas.
*   **Personalización:**
    *   Elige el idioma del plugin (Español, Inglés, Alemán).
    *   Personaliza el modelo y la textura de las tarjetas.
    *   Modifica los mensajes del plugin.
*   **Protección:**
    *   Impide que las tarjetas sean colocadas en el suelo.

## Descargas

*   **SpigotMC:** [TarjetasMaiz en SpigotMC](https://www.spigotmc.org/resources/tarjetasmaiz-%E2%9A%BD.117647/)
*   **GitHub Releases:** [Releases en GitHub](https://github.com/MaizXD-jar/TarjetasMaiz/releases/)

## Instalación

1.  Descarga el plugin desde [SpigotMC](https://www.spigotmc.org/resources/tarjetasmaiz-%E2%9A%BD.117647/) o [GitHub Releases](https://github.com/MaizXD-jar/TarjetasMaiz/releases/).
2.  Coloca el archivo `.jar` en la carpeta `plugins` de tu servidor Spigot o Paper.
3.  Reinicia o recarga el servidor.
4.  Ajusta la configuración en el archivo `config.yml` dentro de la carpeta `plugins/TarjetasMaiz`.

## Configuración

El archivo `config.yml` permite personalizar el plugin:

*   **`language`**: Idioma del plugin. Opciones: `es`, `en`, `de`.
*   **`yellowCardModel`**:  ID del material para la tarjeta amarilla.
*   **`redCardModel`**: ID del material para la tarjeta roja.
*   **`maxYellowCards`**: Número de tarjetas amarillas antes de una tarjeta roja.
*   **`yellowCardCooldown`**: Tiempo entre la aplicación de tarjetas amarillas (en milisegundos).
*   **`redCardCooldown`**: Tiempo entre la aplicación de tarjetas rojas (en milisegundos).
*   **`banDuration`**: Duración del baneo temporal por tarjeta roja (en milisegundos).

## Comandos

*   `/tarjeta give <amarilla|roja>`: Otorga una tarjeta (amarilla o roja) al jugador que ejecuta el comando.  *Requiere el permiso: `tarjetasmaiz.give`*

## Permisos

*   `tarjetasmaiz.give`: Permite dar tarjetas a otros jugadores.
*   `tarjetasmaiz.execute`: Permite usar las tarjetas en otros jugadores.

## Imágenes

![Captura Tarjeta Amarilla](https://www.mediafire.com/file/n47a2u4wazq7yu8/Captura+desde+2024-06-28+09-06-01.png/file)
![Captura Tarjeta Roja](https://www.mediafire.com/file/t4208q9i4na1bbt/2024-06-28_09.05.01.png/file)

## Limitaciones y Bugs Conocidos

*   [ ] Algunos aspectos de la configuración podrían no funcionar correctamente. Consulta la sección de "Issues" para más detalles.
*   [ ] El sistema de almacenamiento JSON para las tarjetas amarillas podría presentar errores.
*   [ ] Integración limitada con otros plugins.

## Contribuciones

¡Las contribuciones son bienvenidas!

*   Reporta bugs y sugiere mejoras en la sección [Issues](https://github.com/MaizXD-jar/TarjetasMaiz/issues).
*   Envía Pull Requests con correcciones y nuevas funciones.

## Licencia

Este plugin está licenciado bajo la [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0).  Consulta el archivo `LICENSE` para obtener los detalles completos.

## Soporte

Si necesitas ayuda o tienes preguntas, crea un nuevo "Issue" en el repositorio.