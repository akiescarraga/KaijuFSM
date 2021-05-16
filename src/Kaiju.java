import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 This class models the intelligence of a kaiju.
 */
class Kaiju {
    // TODO: Place your code here

    /**
     This basic constructor initializes the values for the kaiju intelligence
     model.

     Parameters:
     name          : String
     - name of kaiju
     states        : array-like of size (|Q|, )
     - list of Strings representing the states
     moves         : array-like of size (|M|,3)
     - list of moves. Each move is in the format (name,hpCost,dmg)
     transitions   : array-like of size (|f|,4)
     - list of transitions, each in the format
     (sourceState,moveName,status,destinationState) which denotes
     f(sourceState,moveName,status) = destinationState
     moveRules    : array-like of size (|f|,4)
     - list of transitions, each in the format
     (sourceState,moveName,status,responseMoveName) which denotes
     g(sourceState,moveName,status) = responseMoveName
     initialState : String
     - name of the initial state of the kaiju
     maxHp        : int
     - maximum hit points of the kaiju
     initialMove  : String
     - initial move of the kaiju
     */
    private String name;
    public String [] states;
    public TreeMap<String, Move> moves;
    public TreeMap<String, String[]> transitions;
    public TreeMap<String, String[]> moveRules;
    public String initialState;
    public int maxHp;
    public String initialMove;
    public int currHp;



    public Kaiju(String name, String[] states, Move[] moves,
                 String[][] transitions, String[][] moveRules,
                 String initialState, int maxHp, String initialMove){

        this.name = name;
        this.states = states;
        this.moves = new TreeMap<String, Move>();
        this.transitions = new TreeMap<String, String[]>();
        this.moveRules = new TreeMap<String, String[]>();
        this.initialState = initialState;
        this.maxHp = maxHp;
        this.initialMove = initialMove;
        this.currHp = maxHp;

        for(Move m: moves) {
            this.moves.put(m.name, m);
        }

        for(String[] tr: transitions) {
            this.transitions.put(tr[0] + " " + tr[1] + " " + tr[2], tr);
        }

        for(String[] mr: moveRules) {
            this.moveRules.put(mr[0] + " " + mr[1] + " " + mr[2], mr);
        }
    }

    /**
     Applies a transition based on a move used on this kaiju and the status of
     the opponent kaiju. This changes the state of this kaiju and returns the
     move used.

     Parameters:
     move   : String - name of move used against this kaiju
     status : String - status of opponent kaiju. Could be either "ok" or "hurt"

     Returns name of move to use in response.
     */
    String applyTransition(String move, String status) {
        // TODO: fill this with your own code
        String moves = moveRules.get(initialState + " " + move + " " + status)[3];
        initialState = transitions.get(initialState + " " + move + " " + status)[3];

        return moves;
    }

    /**
     This method returns the status of this kaiju.

     Returns a string indicating whether this kaiju is "ok" or "hurt".
     */
    public String getStatus(){
        // TODO: Place your code here
        if(2 * getHP() > this.maxHp)
            return "ok";
        else
            return "hurt";
    }

    /**
     Returns the current hit points of this kaiju.

     Returns an integer indicating the current hit points of the kaiju.
     */
    public int getHP(){
        // TODO: Place your code here
        return this.currHp;
    }

    /**
     Returns the name of this kaiju.

     Returns a string indicating the name of the kaiju.
     */
    public String getName(){
        // TODO: Place your code here
        return this.name;
    }

    /**
     Uses a move on a target kaiju, updating the hit points of both kaiju.

     Parameters:
     moveName    : string - name of move to use on target kaiju
     targetKaiju : Kaiju  - kaiju to use the move on
     */
    public void useMove(String moveName, Kaiju targetKaiju){
        // TODO: Place your code here

        int damage = 0, hpCost = 0;

        Set<String> set1 = this.moves.keySet();

        for(String key : set1) {
            if(key.equals(moveName))
            {
              damage = this.moves.get(key).dmg;
              hpCost = this.moves.get(key).cost;
            }

        }

        targetKaiju.currHp -= damage;
        this.currHp -= hpCost;

        System.out.println(getName() + "used" + moveName);
        System.out.println(getName() + "HP:" + getHP() + ";" + targetKaiju.getName() + "HP:" + targetKaiju.getHP());
    }
}

/**
 This class represents a kaiju combat simulator.
 */
public class CombatSim {
    public static StringBuilder sb;
    public static BufferedReader br;
    // TODO: Place your code here

    public Kaiju k1;
    public Kaiju k2;
    /**
     This constructor initializes the values of the kaiju combat simulator

     Parameters:
     kaiju1 : Kaiju - first kaiju in battle
     kaiju2 : Kaiju - second kaiju in battle
     */
    public CombatSim(Kaiju kaiju1, Kaiju kaiju2) {
        // TODO: Place your code here
        this.k1 = kaiju1;
        this.k2 = kaiju2;
    }

    /**
     This method steps one round in the combat.

     Returns:
     winner : string - name of the winner of the round. If this round results in
     a draw, return "DRAW". If there is no winner yet,
     value is "NONE"
     */
    public String stepRound() {
        // TODO: Place your code here

        // kaiji1 first turn
        stepTurn(1);

        if (this.k2.getHP() > 0)
            stepTurn(2);

        if (this.k1.getHP() > 0 || this.k2.getHP() > 0)
            return "NONE";
        else if (this.k2.getHP() == 0 && this.k1.getHP() == 0) // enemy kaiju
            return "DRAW";
        else if (this.k1.getHP() < 0)
            return this.k2.getName();
        else if (this.k2.getHP() < 0)
            return this.k1.getName();
    }

    /**
     This method steps one turn in the combat.

     Parameters:
     kaijuId : int - this value is 1 if it is kaiju1's turn and 2 otherwise

     Returns "WIN" if the kaiju eliminated the opponent this turn, "DRAW" if
     both kaiju got knocked out, "LOSS" if only the kaiju taking their turn was
     knocked out, and "NONE" otherwise
     */
    public String stepTurn(int kaijuId) {
        // TODO: Place your code here

    }

    public static void main(String[] args) throws Exception{
        br = new BufferedReader(new InputStreamReader(System.in));
        sb = new StringBuilder();

        int moveCtr;
        String initState, initMove, dump, name;
        String[] parts;
        Move[] moves;

        // read moves
        moveCtr = Integer.parseInt(br.readLine().trim());
        moves = new Move[moveCtr];

        for(int cc = 0; cc < moveCtr; cc++){
            name = br.readLine().trim();
            int cost, dmg;
            parts = br.readLine().trim().split(" ");
            cost = Integer.parseInt(parts[0]);
            dmg = Integer.parseInt(parts[1]);
            moves[cc] = new Move(name,cost,dmg);
        }

        // read kaiju
        Kaiju kaiju1 = readKaiju(moves);
        Kaiju kaiju2 = readKaiju(moves);

        // initialize combat simulator
        CombatSim combatSim = new CombatSim(kaiju1,kaiju2);

        String winner = "NONE";

        while(winner.equals("NONE")){
            winner = combatSim.stepRound();
            if(winner.equals("DRAW")){
                sb.append("It's a draw!\n");
                continue;
            }
            if(!winner.equals("NONE")) {
                sb.append(String.format("%s wins!\n",winner));
            }
        }

        System.out.print(sb);
    }

    public static Kaiju readKaiju(Move[] moves) throws Exception {
        // declare variables
        String[] statuses = {"ok", "hurt"}; // kaiju status

        // read name
        String name = br.readLine().trim();

        // read states
        int stateCtr = Integer.parseInt(br.readLine().trim());
        String[] states = br.readLine().trim().split(" ");

        String[][] transitions = new String[stateCtr * moves.length * 2][4];
        String[][] moveFunc = new String[stateCtr * moves.length * 2][4];
        int ind = 0;

        // read transition and move functions
        for(String state : states) {
            for(Move move : moves) {
                for(String status : statuses) {
                    String targState = br.readLine().trim();
                    String targMove = br.readLine().trim();
                    transitions[ind][0] = moveFunc[ind][0] = state;
                    transitions[ind][1] = moveFunc[ind][1] = move.name;
                    transitions[ind][2] = moveFunc[ind][2] = status;
                    transitions[ind][3] = targState;
                    moveFunc[ind][3] = targMove;
                    ind++;
                }
            }
        }

        // read initial values
        String[] parts = br.readLine().trim().split(" ");
        String initState = parts[0];
        int maxHp = Integer.parseInt(parts[1]);
        String initMove = br.readLine().trim();

        return new Kaiju(name,states,moves,transitions,moveFunc,initState,
                maxHp,initMove);
    }
}

class Move {
    public String name;
    public int cost;
    public int dmg;

    Move(String name, int cost, int dmg) {
        this.name = name;
        this.cost = cost;
        this.dmg = dmg;
    }
}