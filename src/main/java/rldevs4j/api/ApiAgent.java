package rldevs4j.api;

import rldevs4j.base.agent.Agent;
import rldevs4j.base.agent.preproc.Preprocessing;
import rldevs4j.base.env.msg.Event;
import rldevs4j.base.env.msg.Step;

public class ApiAgent extends Agent {
    public Step step;
    public Event action;
    public boolean isWaiting;

    public ApiAgent(String name, Preprocessing preprocessing) {
        super(name, preprocessing, 0);
        isWaiting = true;
        step = null;
    }

    @Override
    public Event observation(Step step) {
        this.step = step;
        this.action = null;
        synchronized (this) {
            this.notifyAll();
            while (this.action == null){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return action;
    }

    public void setAction(Event action) {
        this.action = action;
        this.step = null;
    }

    public Step getStep() {
        return step;
    }

    @Override
    public double getTotalReward() {
        return 0;
    }

    @Override
    public void trainingFinished() {   }

    @Override
    public void clear() { }

    @Override
    public void saveModel(String path) {    }

    @Override
    public void loadModel(String path) {   }

}
