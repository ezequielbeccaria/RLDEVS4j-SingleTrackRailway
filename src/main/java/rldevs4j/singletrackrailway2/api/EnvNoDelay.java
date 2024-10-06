package rldevs4j.singletrackrailway2.api;

import facade.DevsSuiteFacade;
import rldevs4j.api.ApiAgent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;

import java.util.HashMap;
import java.util.Map;
import rldevs4j.api.JsonTransformer;
import rldevs4j.base.env.factory.EnvironmentFactory;
import rldevs4j.base.env.msg.Continuous;
import rldevs4j.base.env.msg.DiscreteEvent;
import rldevs4j.singletrackrailway2.SimpleThreeStopsRailwayFactory;

import static spark.Spark.*;

/**
 * RESTful API to expose DEVS environments to external agents.
 *
 * @author Ezequiel Beccaria
 */
public class EnvNoDelay implements Runnable {
    private DevsSuiteFacade facade;
    private final ApiAgent agent;
    private final Map<String, Object> agentParams;
    private final double EPISODE_MAX_TIME=3000;
    private boolean finish;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {        
        ApiAgent agent = new ApiAgent("API_AGENT", new NoPreprocessing());
        EnvNoDelay exp = new EnvNoDelay(agent);
        Thread simThread = new Thread(exp, "sim_thread");

        int defaultPort = 8080;
        if (args.length > 1) {
            try {
                //Each agent instance must set their particular env port
                defaultPort = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        port(defaultPort);

        get("/hello", (req, res) -> "Hello World");

        get("/reset", "application/json", (req, res) -> {
            if(!simThread.isAlive()) {
                simThread.start();
            } else {
                while (!exp.facade.isSimulationDone()) {
                    // if resets is requested while current simulation is running
                    // a default action is used till current DEVS simulation ends
                    synchronized (agent) {
                        agent.setAction(new Continuous(0, "action", EventType.action, new float[]{0}));
                        agent.notifyAll();
                    }
                }
            }
            synchronized (agent) {
                while (agent.getStep() == null) {
                    agent.wait();
                }
                return agent.getStep();
            }

        }, new JsonTransformer());

        get("/action", "application/json", (req, res) -> {
            int value = Integer.parseInt(req.queryParams("value"));
            Event action = new DiscreteEvent(0, "action", EventType.action, value);
            synchronized (agent) {
                agent.setAction(action);
                agent.notifyAll();
            }
            synchronized (agent) {
                while (agent.getStep() == null) {
                    agent.wait();
                }
                return agent.getStep();
            }

        }, new JsonTransformer());

        get("/close", "application/json", (req, res) -> {
            exp.finish = true;
            while (!exp.facade.isSimulationDone()) {
                synchronized (agent) {
                    agent.setAction(new Continuous(0, "action", EventType.action, new float[]{0}));
                    agent.notifyAll();
                }
            }
            simThread.join();
            stop();
            System.exit(0);
            return "";
        }, new JsonTransformer());
    }

    public EnvNoDelay(ApiAgent agent) {
        this.agent = agent;
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        float[][] actionSpace = new float[][]{{0}, {1}};
        this.agentParams.put("ACTION_SIZE", actionSpace.length);
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("INPUT_SIZE", 9);
        this.finish = false;
    }

    @Override
    public void run() {
        EnvironmentFactory envFactory = new SimpleThreeStopsRailwayFactory(EPISODE_MAX_TIME, new double[]{0D,0D,0D}, false,false);
        Environment env = envFactory.createInstance();
        while (!finish) {
            env.initialize(); //initialize model state
            agent.initialize();
            RLEnvironment rlEnv = new RLEnvironment(agent, env);

            //Inititalize environment and simulator
            facade = new DevsSuiteFacade(rlEnv);
            facade.reset();
            //Simulate during "episodeTime" t (minuts of the day)
            facade.simulateToTime(EPISODE_MAX_TIME);
        }
        System.out.println("Training simulation finish.");
    }
}