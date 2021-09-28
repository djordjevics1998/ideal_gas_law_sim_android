package edu.etf.bg.ac.rs.idealgaslawsimulation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import edu.etf.bg.ac.rs.idealgaslawsimulation.elements.SimulationView;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.IOnSimulationListener;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.ParticleConfig;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.PhObject;
import edu.etf.bg.ac.rs.idealgaslawsimulation.geometry.Simulation2D;

public class MainActivity extends AppCompatActivity {
    private final Handler HANDLER = new Handler();
    private Simulation2D sim2d;
    private Thread thread;
    private final long fps = 1000 / 60;
    private long before;
    private Date previousTime, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        SimulationView simulationView = findViewById(R.id.activity_main_simulation_view);
        AppCompatTextView actvCounter = findViewById(R.id.activity_main_simulation_counter);


        double kB = 1.3806503e-23, T = 273 + 30,
                hfw = 1e-4, r_1 = 1e-6, r_2 = 3e-6, m_1 = 1, m_2 = 2;
	    int row = 3, col = 3, stack = 12;
	    long sim_step = 100, sim_count = 1000;
        ParticleConfig pc1 = new ParticleConfig(0, r_1, m_1),
            pc2 = new ParticleConfig(1, r_2, m_2);
        sim2d = new Simulation2D(kB, T, hfw, pc1, pc2, 1/*0.5*/, sim_step, sim_count, 0, row * col, row, col);
        sim2d.setIOnSimulationListener(new IOnSimulationListener() {
            @Override
            public void OnSimulationStart(PhObject[] objs) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        simulationView.setObjects(objs);
                    }
                });
            }

            @Override
            public void OnSimulationIteration(PhObject[] objs, int sim_ite) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        //StringBuilder str = new StringBuilder();
                        //for(PhObject phObject: objs) str.append(phObject.toString());
                        //actvCounter.setText("" + sim_ite );//+ " " + str.toString());
                        //actvCounter.invalidate();
                        simulationView.setObjects(objs);
                    }
                });
                /*if(sim_ite == 0) previousTime = Calendar.getInstance().getTime();
                if(sim_ite != 0 && sim_ite % sim_step == 0) {
                    currentTime = Calendar.getInstance().getTime();
                    long toDelay;
                    if(previousTime.getTime() + fps <= currentTime.getTime()) {
                        toDelay = 0;
                        currentTime = previousTime;
                    } else {
                        toDelay = previousTime.getTime() + fps - currentTime.getTime();
                        previousTime.setTime(previousTime.getTime() + fps);
                    }
                    HANDLER.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //StringBuilder str = new StringBuilder();
                            //for(PhObject phObject: objs) str.append(phObject.toString());
                            actvCounter.setText("" + sim_ite );//+ " " + str.toString());
                            actvCounter.invalidate();
                            simulationView.setObjects(objs);
                        }
                    }, toDelay);
                }*/
            }

            @Override
            public void OnSimulationStep(double pV, double NkBT, int sim_step) {
                HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        actvCounter.setText("" + pV + " " + NkBT + " " + sim_step );//+ " " + str.toString());
                        actvCounter.invalidate();
                        //actvStep.setText(actvStep.getText().toString() + "\n" + pV + " " + NkBT + " " + sim_step);
                    }
                });
            }

            @Override
            public void OnSimulationEnd(PhObject[] objs) {
                /*HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        simulationView.setObjects(objs);
                        Toast.makeText(MainActivity.this, "Zavrsio!", Toast.LENGTH_SHORT).show();
                    }
                });*/
            }
        });
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    sim2d.run();
                    simulationDispose();
                } catch (InterruptedException ie) {
                    simulationDispose();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(thread != null) thread.interrupt();
        else simulationDispose();
    }

    private void simulationDispose() {
        if(sim2d != null) {
            sim2d.destroy();
            sim2d = null;
        }
    }
}