package edu.etf.bg.ac.rs.idealgaslawsimulation.geometry;

public interface IOnSimulationListener {
    void OnSimulationStart(PhObject[] objs);
    void OnSimulationIteration(PhObject[] objs, int sim_ite);
    void OnSimulationStep(double pV, double NkBT, int sim_step);
    void OnSimulationEnd(PhObject[] objs);
}
