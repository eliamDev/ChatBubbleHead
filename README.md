# DCChatBubbles

Un plugin para Minecraft que transforma los mensajes del chat en **burbujas de texto** que flotan directamente sobre la cabeza del jugador.

## ✨ Características Principales
* **Totalmente Personalizable:** Cuenta con un `config.yml` muy completo donde puedes cambiar:
  * Colores del fondo y del texto (soporta Códigos Hexadecimales `#FFFFFF`).
  * Opacidad del fondo (transparencia).
  * Límite de globos en pantalla y la distancia entre ellos.
  * Duración, escala, y altura de la burbuja.

## ⚙️ Comandos

El plugin utiliza el comando principal `/chatbubble` (o sus alias si se configuran).

* `/chatbubble` - Activa o desactiva las burbujas para ti mismo (útil si a un jugador no le gusta verlas).
* `/chatbubble reload` - Recarga instantáneamente el archivo `config.yml` sin necesidad de reiniciar el servidor.

## 🛠️ Requisitos
* Servidor con software **Paper** (o derivados como Purpur) en la versión **1.21**.
* Java 21.

## 📜 Instalación
1. Compila el plugin o descarga el archivo `.jar`.
2. Colócalo en la carpeta `plugins/` de tu servidor.
3. Inicia el servidor.
4. (Opcional) Edita el archivo `config.yml` generado dentro de `plugins/DCChatBubbles/` a tu gusto y usa `/chatbubble reload` para aplicar cambios.
