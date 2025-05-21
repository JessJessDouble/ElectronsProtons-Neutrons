# Electron-Proton-Nuetron Simulation

## Overview
`EPNSimulation` is a Java-based particle physics simulation that models charged and neutral particles, including their corresponding antiparticles. It visualizes particle interactions, collisions, and forces in a 2D environment.

## Particle Types

| Particle       | Charge       | Color           | Mass Behavior                    | Description                                  |
|----------------|--------------|-----------------|---------------------------------|----------------------------------------------|
| **Proton (P)**       | +1           | Red             | Standard mass (1.008 units)      | Positively charged particle                   |
| **Electron (E)**     | -1           | Blue            | Small mass (0.0005446 units)     | Negatively charged particle                   |
| **Neutron (N)**      | 0            | Gray            | Standard mass (1.008 units)      | Neutral particle                              |

## Controls

| Key         | Action                                                                                 |
|-------------|----------------------------------------------------------------------------------------|
| **E**       | Select **Electron** (negative charge, blue)                                           |
| **P**       | Select **Proton** (positive charge, red)                                              |
| **N**       | Select **Neutron** (neutral, gray)                                                    |
| Mouse Click | Spawn a particle of selected type at cursor position                                  |

## Particle Interaction Summary
- Charges determine force interactions: opposite charges attract, like charges repel.
- Elastic collisions with bounce and overlap correction.
- Gravity and wall bounce effects.
- Particle mass and size adjusted based on type.
- 
## Running the Simulation
1. Compile and run `Basic.EPNSimulation`.
2. Press keys **E, P, N to select particle type.
3. Click mouse to spawn selected particle at the cursor.
4. Press **R** to spawn random pairs of particles and their antiparticles.

