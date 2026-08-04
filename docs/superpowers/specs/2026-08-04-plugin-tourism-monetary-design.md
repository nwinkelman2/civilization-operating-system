# Plugin SDK, Tourism & Monetary Economy Engine Design Specification (Issues #57, #51, #49)

## Objective
Implement three core backend simulation engines:
1. **Plugin SDK Engine (Issue #57)**: Extension hook system for custom rule packs, external mod loading, and plugin lifecycle callbacks.
2. **Tourism & Pilgrimage Engine (Issue #51)**: Regional landmark attraction calculation, agent pilgrimage routes, and cultural tourism revenue.
3. **Monetary Economy Engine (Issue #49)**: Currency emission (M1), central bank interest rate policy adjustments, and inflation index tracking.

## Component Architecture

### 1. Plugin SDK Service (`io.github.opencivilizationplatform.modules.nexus.application.PluginSdkService`)
- Manages plugin lifecycle:
  - `CivilizationPlugin(pluginId, pluginName, version, active)`
  - Registering and executing custom simulation hook listeners.

### 2. Tourism & Pilgrimage Service (`io.github.opencivilizationplatform.modules.social.application.TourismPilgrimageService`)
- Landmark attraction:
  - `Landmark(landmarkId, name, regionId, attractionPower)`
  - Agent tourist visits and cultural tourism economy flow.

### 3. Monetary Economy Service (`io.github.opencivilizationplatform.modules.trade.application.MonetaryEconomyService`)
- Central bank monetary policy:
  - `moneySupply` (M1 currency in circulation)
  - `interestRate` (base interest rate %)
  - `inflationIndex` calculation based on transaction velocity.

## Verification Plan
1. Unit tests for `PluginSdkServiceTest`, `TourismPilgrimageServiceTest`, and `MonetaryEconomyServiceTest`.
2. `./mvnw test` ensuring 100% green build.
