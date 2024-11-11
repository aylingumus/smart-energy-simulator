# 🔋 Smart Energy Simulator

I built this application as a simple simulator to model energy production, consumption, and storage in a household
environment. The app calculates energy output from solar and wind sources, simulates energy use for lighting and
heating, and keeps track of battery levels to monitor energy balance and battery health.

## 🚀 Getting Started

### Prerequisites

- Java 22 or higher
- Maven
- Docker (for running PostgreSQL as a container)

### Setup Instructions

1. Clone the repository:
    ```bash
    git clone https://github.com/aylingumus/smart-energy-simulator
    cd smart-energy-simulator
    ```
2. Build the application:
    ```bash
    mvn clean install
    ```
3. Start the database with Docker Compose:
    ```bash
    docker-compose up -d
    ```
4. Run the application:
    ```bash
    mvn spring-boot:run
    ```
5. Access the API at `http://localhost:8080/api`.

## 🛠️ Technology Stack

- **Kotlin**: Main language for the application.
- **Spring Boot**: Framework for creating RESTful APIs.
- **PostgreSQL**: Database for data storage.
- **Docker Compose**: Used to set up and run PostgreSQL in a container.

## 📐 Formulas and Calculation Explanations

### 1. 🌞 Solar Energy Production Formula

  ```kotlin
    val efficiency = sin((hoursSinceDawn.toDouble() / totalDaylightHours) * Math.PI)
  ```

- **Scientific Basis**: Solar irradiance follows a sinusoidal pattern, peaking around noon and decreasing towards
  sunrise and sunset.
- **Simplified Approach**: The sine function scales `baseOutput` according to the time of day to simulate natural
  sunlight intensity variations.

**Reference**:
[Practical approach for sub-hourly and hourly prediction of PV power output](https://www.researchgate.net/publication/224188697_Practical_approach_for_sub-hourly_and_hourly_prediction_of_PV_power_output)

---

### 2. 💨 Wind Energy Production Formula

  ```kotlin
  when {
    windSpeed < 3.0 -> 0.0  // Too slow
    windSpeed > 25.0 -> 0.0 // Too fast
    else -> {
        val efficiency = (windSpeed - 3.0) / 22.0
        baseOutput * efficiency * BATTERY_EFFICIENCY
    }
}
  ```

- **Scientific Basis**: Wind turbines work only within a specific wind speed range. They start generating power at
  around **3 m/s** (cut-in speed) and shut down at **25 m/s** (cut-out speed) to prevent damage.

- **Formula Explanation**:
    - The value `22.0` in `(windSpeed - 3.0) / 22.0` represents the difference between cut-in (3 m/s) and cut-out (25
      m/s) speeds.
    - This formula scales power output linearly, going from 0 to full power as wind speed increases within this range,
      making it a simple model for simulation.

- **Simplified Approach**: This linear scaling helps approximate efficiency without complex calculations, simulating
  realistic turbine output across the operational wind range.

**Reference**:
[Wind Turbine Design - Wikipedia](https://en.wikipedia.org/wiki/Wind_turbine_design#:~:text=A%20wind%20turbine%20must%20produce,power%20has%20to%20be%20limited)

---

### 3. 💡 Lighting Energy Consumption Formula

  ```kotlin
  if (currentHour !in DAYLIGHT_START..<DAYLIGHT_END) {
    baseConsumption
} else {
    0.0
}
  ```

- **Scientific Basis**: Daylighting reduces the need for artificial lighting during daylight hours, saving energy.
- **Simplified Approach**: Lights are off during the day and consume `baseConsumption` only at night.

**Reference**:
[The Energy Savings of Daylighting - Danpal](https://danpal.com/environmental/the-energy-savings-of-daylighting/)

---

### 4. 🔥 Heating System Energy Consumption Formula

  ```kotlin
  val tempDifference = TARGET_TEMPERATURE - currentTemp
if (tempDifference > 0) {
    baseConsumption * (1 + tempDifference / 10)
} else {
    0.0
}
  ```

- **Scientific Basis**: The energy required for heating is proportional to the temperature difference between the indoor
  and outdoor environments, as described by the heat transfer equation:

  ```
    q = U * A * ΔT
  ```

    - **q:** heat transfer rate (W)
    - **U:** heat transfer coefficient, which depends on insulation and materials
    - **A:** area through which heat is transferred
    - **ΔT:** temperature difference

- **Simplified Approach**: Multiplies `baseConsumption` by a factor based on `tempDifference`, simulating an increase in
  heating demand with larger temperature differences. Dividing by 10 scales the adjustment to a manageable range.

**Reference**:
[Heat Transfer - Engineering Toolbox](https://www.engineeringtoolbox.com/overall-heat-transfer-coefficient-d_434.html)

---

### 5. 🔋 Battery Management System Formula

  ```kotlin
  when {
    battery.currentLevel < battery.capacity * LOW_BATTERY_THRESHOLD ->
        EnergyStatus.LOW_BATTERY
    totalConsumption > totalProduction ->
        EnergyStatus.ENERGY_DEFICIT
    else ->
        EnergyStatus.NORMAL
}
  ```

- **Scientific Basis**: To extend battery lifespan, the system maintains the state of charge (SOC) within a moderate
  range of 20%-80%, avoiding deep discharges.
- **Simplified Approach**: Tracks basic battery levels and energy production vs. consumption to determine any energy
  deficit.

**Reference**:
[Battery State of Charge and Depth of Discharge - ScienceDirect](https://www.sciencedirect.com/science/article/pii/S2352152X23025422)

---

## 💡 Assumptions and Trade-offs

To keep calculations straightforward, I made some assumptions and simplifications:

- **Solar Energy**: I used a sine function for solar energy output without accounting for seasonal changes or weather.
- **Wind Energy**: The wind output scales linearly, even though real turbines have more complex power curves.
- **Lighting**: The lighting model assumes lights are either fully on or off, without simulating dimming.
- **Heating**: The heating model uses a basic relationship with temperature difference rather than detailed
  thermodynamic equations.
- **Battery Management**: Tracks simple battery states without detailed health metrics.

These simplifications allow for a basic yet functional simulation of household energy use.