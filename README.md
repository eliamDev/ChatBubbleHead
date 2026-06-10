<div align="center">

# 💬 ChatBubbleHead

**Transforma los mensajes del chat en burbujas de texto flotantes sobre la cabeza de cada jugador.**

[![Version](https://img.shields.io/badge/version-1.0.2-blue?style=for-the-badge)](https://github.com/eliamDev/ChatBubblesHead/releases)
[![Paper](https://img.shields.io/badge/Paper-1.21+-brightgreen?style=for-the-badge&logo=minecraft)](https://papermc.io)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://adoptium.net)
[![License](https://img.shields.io/badge/license-MIT-purple?style=for-the-badge)](LICENSE)

</div>

---

## ✨ Características

- 🗨️ **Burbujas flotantes** — Los mensajes del chat aparecen directamente sobre la cabeza del jugador en tiempo real.
- 🎨 **Totalmente personalizable** — Colores en HEX, opacidad, escala, duración y más desde el `config.yml`.
- 🔁 **Animaciones suaves** — Efecto pop-in y pop-out con interpolación nativa de Minecraft.
- 📦 **Apilamiento inteligente** — Varios mensajes seguidos se apilan ordenadamente sin superponerse.
- 🙈 **Toggle personal** — Cada jugador puede desactivar las burbujas para sí mismo con `/chatbubble`.
- 🛡️ **Control de admins** — Apagado global de emergencia y recarga en caliente de la config sin reiniciar.
- 💾 **Persistencia** — La preferencia de cada jugador se guarda en `data.yml` y sobrevive reinicios.
- 🚗 **Soporte de monturas** — Las burbujas se posicionan correctamente cuando el jugador está montado.

---

## ⚙️ Comandos

| Comando | Descripción | Permiso |
|---|---|---|
| `/chatbubble` | Activa o desactiva tus propias burbujas | `dc.chatbubble.toggle` |
| `/chatbubble reload` | Recarga el `config.yml` sin reiniciar | `dc.chatbubble.reload` |
| `/chatbubble global` | Activa/desactiva las burbujas para todo el servidor | `dc.chatbubble.global` |

---

## 🔐 Permisos

| Permiso | Descripción | Por defecto |
|---|---|---|
| `dc.chatbubble.toggle` | Permite al jugador desactivar sus propias burbujas | `true` (todos) |
| `dc.chatbubble.reload` | Permite recargar la configuración del plugin | `op` |
| `dc.chatbubble.global` | Permite apagar las burbujas de todo el servidor | `op` |

---

## 🛠️ Configuración (`config.yml`)

| Clave | Tipo | Por defecto | Descripción |
|---|---|---|---|
| `color_fondo` | HEX String | `#FFFFFF` | Color del fondo de la burbuja |
| `opacidad_fondo` | Int (0–255) | `220` | Transparencia del fondo (255 = opaco) |
| `color_texto` | HEX String | `#000000` | Color del texto |
| `sombra_texto` | Boolean | `false` | Sombra detrás del texto |
| `escala` | Double | `1.0` | Tamaño de la burbuja |
| `ancho_linea` | Int | `150` | Ancho en píxeles antes de salto de línea |
| `duracion` | Int (segundos) | `6` | Tiempo que dura la burbuja visible |
| `max_chars` | Int | `80` | Máximo de caracteres por burbuja |
| `altura` | Double | `2.2` | Altura sobre la cabeza del jugador (en bloques) |
| `limite_burbujas` | Int | `5` | Máximo de burbujas por jugador en pantalla |
| `distancia_apilado` | Double | `0.3` | Separación vertical entre burbujas apiladas |

---

## 📋 Requisitos

- Servidor **Paper** 1.21+ (o derivados como Purpur / Folia no soportado aún)
- **Java 21**

---

## 📜 Instalación

1. Descarga el `.jar` desde la sección de [Releases](https://github.com/eliamDev/ChatBubblesHead/releases).
2. Colócalo en la carpeta `plugins/` de tu servidor.
3. Inicia (o reinicia) el servidor.
4. *(Opcional)* Edita `plugins/DCChatBubbles/config.yml` a tu gusto y usa `/chatbubble reload` para aplicar cambios sin reiniciar.

---

## 🏗️ Compilar desde el código fuente

```bash
git clone https://github.com/eliamDev/ChatBubblesHead.git
cd ChatBubblesHead
mvn clean package
```

El `.jar` compilado estará en `target/DCChatBubbles-<versión>.jar`.

---

## 📝 Changelog

### v1.0.2
- Sistema de toggle **per-player**: cada jugador puede apagar sus propias burbujas.
- Persistencia de preferencias en `data.yml`.
- Nuevo subcomando `/chatbubble global` para admins.
- Permisos reorganizados en 3 niveles.
- Autocompletado de Tab en el comando.

### v1.0.1
- Primera versión pública con burbujas flotantes y animaciones pop-in/pop-out.

---

## 🤝 Contribuciones e Issues

¿Encontraste un bug o tienes una idea? Abre un [Issue](https://github.com/eliamDev/ChatBubblesHead/issues) o un Pull Request. ¡Son bienvenidos!

---

<div align="center">
Hecho con ❤️ por <strong>DiaCero</strong>
</div>
