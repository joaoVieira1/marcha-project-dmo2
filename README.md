# 🚶 Marcha - Monitoramento de Trilhas em Tempo Real

**Marcha** é um aplicativo Android desenvolvido em Kotlin que permite monitorar caminhadas e trilhas em tempo real. Ele registra passos, tempo, altitude e distância percorrida, utilizando sensores nativos do dispositivo e persistência com Firebase.

---

## 📲 Funcionalidades

- ✅ Início e parada do monitoramento com um toque.
- 🦶 Contagem de passos em tempo real com o sensor `TYPE_STEP_COUNTER`.
- ⏱️ Cronômetro da trilha exibindo o tempo total de caminhada.
- ⛰️ Altitude máxima atingida, calculada a partir do sensor de pressão (`TYPE_PRESSURE`).
- 🛣️ Estimativa de distância percorrida com base no número de passos.
- 📱 Exibição dos dados ao final da trilha em uma `CardView` personalizada.
- 🔄 Monitoramento contínuo mesmo com o app fechado via `ForegroundService`.
- ☁️ Upload de dados e imagens para o **Firebase**.

---

## ⚙️ Tecnologias Utilizadas

### 📦 Backend
- **Firebase Firestore** – Armazenamento de trilhas e estatísticas.

### 📱 Android & Kotlin
- `SensorManager` com:
  - `TYPE_STEP_COUNTER` – Contador de passos.
  - `TYPE_PRESSURE` – Cálculo de altitude.
- **ForegroundService** – Serviço ativo em segundo plano para manter a contagem ativa com a tela desligada.
- **ViewBinding** – Acesso seguro aos elementos de layout.

---

## 📌 Requisitos

- Android 7.0 (API 24) ou superior
- Permissões de sensores e armazenamento em tempo de execução

---

## 👨‍💻 Desenvolvedor

**João Vieira**  
Estudante de Análise e Desenvolvimento de Sistemas | Desenvolvimento Android  

---


