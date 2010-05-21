package sneat.experiments.skrimish;

import sneat.neuralnetwork.INetwork;
import sneat.neuralnetwork.fastconcurrentnetwork.FloatFastConcurrentNetwork;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class World extends Drawable {
    public List<Prey> removeThese = new ArrayList<Prey>();
    public List<Predator> Player = new ArrayList<Predator>();
    public List<Prey> Enemy = new ArrayList<Prey>();
    public static int step = 5;
    public static float turn = (float) Math.PI / 5.0f;
    public int agentSize = 10;
    public int time = 0;
    public static float size = 0;
    public FloatFastConcurrentNetwork bigBrain;
    public float distanceFromEnemy = 0;

    public World() {
        super(0, 0, 1000, 1000);
//        color = System.Drawing.Brushes.White;
        size = height;
    }

    public World(INetwork network) {
        super(0, 0, 1000, 1000);
//        color = System.Drawing.Brushes.White;
        bigBrain = (FloatFastConcurrentNetwork) network;
        size = height;
    }

    @Override
    public void Draw(Graphics g) {
//        g.FillRectangle(color, x, y, width, height);
    }

    public void addPlayer(Predator player) {
        Player.add(player);
    }

    public void addEnemy(Prey enemy) {
        Enemy.add(enemy);
    }

    public float go(int howLong) {
        time = 0;
        while (timeStep() && time < howLong) {
            time++;
        }
        if (Enemy.size() != 0)
            time = howLong;

        return howLong - time;

    }

    public float goMulti(int howLong) {
        time = 0;
        while (timeStepMulti() && time < howLong) {
            time++;
        }
        if (Enemy.size() != 0)
            time = howLong;

        return howLong - time;

    }

    public boolean timeStepMulti() {
        int predCount = Player.size();
        float[] inputs;
        inputs = new float[Player.size() * 5];
        if (Enemy.size() == 0)
            return false;


//fill the predators' sensors and then copy those inputs to a big array that will input to the big ANN
        for (int pred = 0; pred < predCount; pred++) {
            Player.get(pred).clearSensors();

            Player.get(pred).fillSensorsFront(Enemy);

//Here we assume 5 sensors per predator
            for (int sense = 0; sense < 5; sense++) {
                inputs[sense + pred * 5] = Player.get(pred).sensors[sense];
            }
        }
        bigBrain.setInputSignals(inputs);
        bigBrain.multipleSteps(2);
        float[] outputs = new float[3];


        for (int agent = 0; agent < predCount; agent++) {
            outputs[0] = bigBrain.getOutputSignal(0 + agent * 3);
            outputs[1] = bigBrain.getOutputSignal(1 + agent * 3);
            outputs[2] = bigBrain.getOutputSignal(2 + agent * 3);
            (Player.get(agent)).determineAction(outputs);

//If you're running the visualization, uncomment this line otherwise the sensor displays will lag by 1 timestep
//pred.fillSensorsFront(Enemy);

        }
        int preyCount = Enemy.size();
        for (int prey = 0; prey < preyCount; prey++) {
            //mark the dead guys for deletion
            if (Enemy.get(prey).hitpoints <= 0) {
                removeThese.add(Enemy.get(prey));
                continue;
            }

            Enemy.get(prey).determineAction();
        }

        for (Prey a : removeThese)
            Enemy.remove(a);
        removeThese.clear();
        return true;
    }

    public boolean timeStep() {
        int predatorCount = Player.size();
        if (Enemy.size() == 0)
            return false;
        for (int pred = 0; pred < predatorCount; pred++) {
            Predator a = Player.get(pred);
            a.clearSensors();
            a.fillSensorsFront(Enemy);
        }

        //if (SkirmishNetworkEvaluator.trainingSeed)
        //distanceFromEnemy += Utilities.Distance(Enemy[0], Player[0]);

        for (int pred = 0; pred < predatorCount; pred++)
            Player.get(pred).determineAction();

        int preyCount = Enemy.size();
        for (int prey = 0; prey < preyCount; prey++) {
            //mark the dead guys for deletion
            if (Enemy.get(prey).hitpoints <= 0) {
                removeThese.add(Enemy.get(prey));
                continue;
            }

            Enemy.get(prey).determineAction();

        }

        for (Prey a : removeThese)
            Enemy.remove(a);
        removeThese.clear();
        return true;
    }


    public void drawWorld(Graphics g, boolean drawPie) {
        //TODO  X drawing the world
        System.out.println("World.drawWorld");
/*
        System.Drawing.Brush myBrush = new System.Drawing.SolidBrush(System.Drawing.Color.Black);
        Draw(g);
        foreach(Predator a in Player)
        {
            if (drawPie) {
                float arc = 180F / a.sensors.Length;
                float f = a.heading * (360f / (2 * (float) Math.PI)) - 90;
                for (int i = 0; i < a.sensors.Length; f += arc, i++) {
                    if (a.sensors[i] > 0) {
                        ((SolidBrush) myBrush).Color = Color.FromArgb(255, (byte) (255 * (1 - a.sensors[i])), (byte) (255 * (1 - a.sensors[i])));
                        g.FillPie(myBrush, a.x - a.radius, Utilities.shiftDown + (a.y - a.radius), a.radius * 2, a.radius * 2, f, arc);
//g.DrawPie(Pens.Black, a.x - a.radius, a.y - a.radius, a.radius * 2, a.radius * 2, f, arc);
                    } else
                        g.DrawPie(Pens.Black, a.x - a.radius, Utilities.shiftDown + (a.y - a.radius), a.radius * 2, a.radius * 2, f, arc);


                }
            }
            System.Drawing.Pen p = new Pen(System.Drawing.Color.Red, 5f);
            g.DrawLine(p, a.x, Utilities.shiftDown + a.y, a.x + (float) Math.Cos(a.heading) * (25), Utilities.shiftDown + a.y + (float) Math.Sin(a.heading) * (25));
            a.Draw(g);
        }
        foreach(Prey p in Enemy)
        {
            p.Draw(g);
        }
        */
    }
}
