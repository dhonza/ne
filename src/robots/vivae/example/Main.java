/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package robots.vivae.example;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JFrame;
import robots.vivae.arena.parts.Active;
import robots.vivae.arena.Arena;






public class Main{
    
   
    public static void main(String[] args) {
//        try {
//            Logger.getLogger("vivae").addHandler(new FileHandler("log.txt"));
//        }
//        catch(Exception e) {
//            e.printStackTrace();
//        }
//        Logger.getLogger("vivae").info("zacatek programu");
        JFrame f = new JFrame("Arena");
        Arena arena = new Arena(f);
        arena.loadScenario("cfg/vivae/scenarios/arena2.svg");
        Vector<Active> agents = arena.getActives();
        for (Iterator<Active> it = agents.iterator(); it.hasNext();) {
            Active agent = it.next();
            arena.registerController(agent, new MyController());
//            arena.registerController(agent, new KeyboardVivaeController());
        }
        arena.setAllArenaPartsAntialiased(true);
        f.setBounds(50, 0, arena.screenWidth, arena.screenHeight+30);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(arena);
        f.setVisible(true);
        arena.init();
        arena.run();
        
    }
}

