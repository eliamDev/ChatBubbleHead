# DCChatBubbles

Un plugin "Premium" para Minecraft que transforma los aburridos mensajes del chat en **burbujas de texto animadas** que flotan directamente sobre la cabeza de los jugadores.

## ✨ Características Principales

* **Animaciones Suaves (Premium Feel):** Las burbujas nacen con un efecto de crecimiento (*Pop-in*) y desaparece encogiéndose suavemente (*Pop-out*).
* **Burbujas Apilables (Stacking):** Si un jugador envía múltiples mensajes rápidos, las burbujas anteriores no desaparecen bruscamente. En su lugar, se deslizan suavemente hacia arriba para dejar espacio al nuevo mensaje.
* **Soporte para Monturas:** La altura del globo se ajusta dinámicamente. Si el jugador se sube a un caballo, camello o bote, el globo aparecerá correctamente sobre su cabeza sin quedarse atrapado en el cuerpo del animal.
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
4. (Opcional) Edita el archivo `config.yml` generado dentro de `plugins/DCChatBubbles/` a tu gusto y usa `/chatbubble reload`.
