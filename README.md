### 🧱 Architecture
- **Clean Architecture (the new approach)**  
  - `data` – Remote data sources, DTOs, and models  
  - `domain` – Use cases and business logic  
  - `presentation` – UI state and logic (ViewModel + Orbit MVI)  
- **MVI (Model-View-Intent)** via [Orbit MVI](https://github.com/orbit-mvi/orbit-mvi)  
- **Flows, StateFlow, SharedFlow** for reactive UI and background updates

### ⚙️ Frameworks & Libraries
- **Jetpack Compose** – Declarative UI
- **Compose Navigation (NavHost)** – For screen navigation
- **WorkManager** – Background polling of new Flickr data
- **Hilt** – Dependency injection
- **Coil** – Async image loading with caching
- **Kotlin Coroutines** – Async work & stream handling
- **Flickr API** – Public feed via REST
