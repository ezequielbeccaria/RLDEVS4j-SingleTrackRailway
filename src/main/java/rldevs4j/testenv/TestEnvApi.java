package rldevs4j.testenv;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import facade.DevsSuiteFacade;
import rldevs4j.api.ApiAgent;
import rldevs4j.base.agent.preproc.NoPreprocessing;
import rldevs4j.base.env.Environment;
import rldevs4j.base.env.RLEnvironment;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.EventType;
import rldevs4j.base.env.msg.Step;
import spark.ResponseTransformer;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * RESTful API to expose DEVS environments to external agents.
 *
 * @author Ezequiel Beccaria
 */
public class TestEnvApi implements Runnable {
    private DevsSuiteFacade facade;
    private ApiAgent agent;
    private final Map<String, Object> agentParams;
    private final double EPISODE_MAX_TIME = 100.5;
    private boolean finish;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ApiAgent agent = new ApiAgent("TEST_API_2", new NoPreprocessing());
        TestEnvApi exp = new TestEnvApi(agent);
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
                        agent.setAction(new Event(0, "action", EventType.action));
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
            Integer actionId = Integer.parseInt(req.queryParams("id"));
            Event action = new Event(actionId, "action", EventType.action);
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
                    agent.setAction(new Event(0, "action", EventType.action));
                    agent.notifyAll();
                }
            }
            simThread.join();
            stop();
            System.exit(0);
            return "";
        }, new JsonTransformer());
    }

    public TestEnvApi(ApiAgent agent) {
        this.agent = agent;
        this.facade = new DevsSuiteFacade();
        this.agentParams = new HashMap<>();
        float[][] actionSpace = new float[][]{{0}, {1}, {2}};
        this.agentParams.put("ACTION_SIZE", actionSpace.length);
        this.agentParams.put("ACTION_SPACE", actionSpace);
        this.agentParams.put("INPUT_SIZE", 9);
        this.finish = false;
    }

    @Override
    public void run() {
        TestEnvFactory factory = new TestEnvFactory();
        Environment env = factory.createInstance();
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

class JsonTransformer implements ResponseTransformer {

    private ObjectMapper jsonMapper;

    public JsonTransformer() {
        this.jsonMapper = new ObjectMapper();
        this.jsonMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        this.jsonMapper.setAnnotationIntrospector(new Step.IgnoreInheritedIntrospector());
    }

    @Override
    public String render(Object model) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(model);
    }

}
