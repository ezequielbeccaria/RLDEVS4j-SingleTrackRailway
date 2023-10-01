# RLDEVS4j - Single Track Railway Environment
Implementation of the environment described in [Reinforcement learning approach for train rescheduling on a single-track railway](https://www.sciencedirect.com/science/article/abs/pii/S0191261516000084)

## Dependencies

1. [DEVS-Suite-facade](https://github.com/ezequielbeccaria/Devs-Suite-Facade.git)
2. [RLDEVS4j-Base](https://github.com/ezequielbeccaria/RLDEVS4j-Base.git)
3. [RLDEVS4j-Agents](https://github.com/ezequielbeccaria/RLDEVS4j-Agents.git) 

## Usage

### Compile

It is necessary to compile each of the dependencies previously to use the project. 
In each dependency root folder execute:

```bash
javac -classpath . app/Main.java
```

After that, do the same with the current proyect.

### Agent Training

To execute an agent training en one of the possible scenarios:

```bash
java -jar RLDEVS4j-SingleTrackRailway-jar-with-dependencies.jar [SCENARIO]_[AGENT]
```

Possible scenarios:
1. `10min_delay`
2. `random_delay`

Possible agents:
1. `ppo`
2. `a3c`
3. `ddqn`

PPO agent training in 10 minutes delay scenario example

```bash
java -jar RLDEVS4j-SingleTrackRailway-jar-with-dependencies.jar 10min_delay_ppo
```