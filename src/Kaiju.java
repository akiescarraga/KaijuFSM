import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

/**
 This class models the intelligence of a kaiju.
 */
class Kaiju {


    StringBuilder kb = new StringBuilder();
    Map<String, String[]> transitions = new HashMap<>();
    Map<String, String[]> moveRules = new HashMap<>();
    Map<String, Move> moves = new HashMap<>();
    String name;
    String currentState;
    String nextMove;
    String[] states;
    int currHp;
    int maxHp;

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
    public Kaiju(String name, String[] states, Move[] moves,
                 Map<String, String[]> transitions, Map<String, String[]> moveRules,
                 String initialState, int maxHp, String initialMove){

        this.name = name;
        this.states = states;
        this.currentState = initialState;
        this.maxHp = currHp = maxHp;
        this.nextMove = initialMove;
        this.transitions = transitions;
        this.moveRules = moveRules;

        for(int i = 0; i < moves.length; i++)
            this.moves.put(moves[i].name, moves[i]);

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
    String applyTransition(String move, String status){
        kb.append(currentState).append(move).append(status);
        String key = kb.toString();
        String[] t = transitions.get(key);
        String[] m = moveRules.get(key);

        currentState = t[3];
        nextMove = m[3];
        kb.setLength(0);
        return nextMove;
    }

    /**
     This method returns the status of this kaiju.

     Returns a string indicating whether this kaiju is "ok" or "hurt".
     */
    public String getStatus(){
        if( 2 * currHp > maxHp)
            return "ok";
        else return "hurt";
    }

    /**
     Returns the current hit points of this kaiju.

     Returns an integer indicating the current hit points of the kaiju.
     */
    public int getHP(){
        return currHp;
    }

    /**
     Returns the name of this kaiju.

     Returns a string indicating the name of the kaiju.
     */
    public String getName(){
        return name;
    }

    /**
     Uses a move on a target kaiju, updating the hit points of both kaiju.

     Parameters:
     moveName    : string - name of move to use on target kaiju
     targetKaiju : Kaiju  - kaiju to use the move on
     */
    public void useMove(String moveName, Kaiju targetKaiju, StringBuilder sb){
        // You may print in this function.
        String key = moveName;
        Move m = moves.get(key);

        targetKaiju.currHp -= m.dmg;
        currHp -= m.cost;

        if(currHp > maxHp)
            currHp = maxHp;
        else if(currHp < 0)
            currHp = 0;

        if(targetKaiju.currHp > targetKaiju.maxHp)
            targetKaiju.currHp = targetKaiju.maxHp;
        else if(targetKaiju.currHp < 0)
            targetKaiju.currHp = 0;

        sb.append(String.format("%s used %s\n%s HP: %d; %s HP: %d\n", name, m.name, name, currHp, targetKaiju.name, targetKaiju.currHp));
    }
}

/**
 This class represents a kaiju combat simulator.
 */
public class CombatSim {
    public static StringBuilder sb;
    public static BufferedReader br;
    public Kaiju k1;
    public Kaiju k2;

    boolean initial = true;

    /**
     This constructor initializes the values of the kaiju combat simulator

     Parameters:
     kaiju1 : Kaiju - first kaiju in battle
     kaiju2 : Kaiju - second kaiju in battle
     */
    public CombatSim(Kaiju kaiju1, Kaiju kaiju2) {
        k1 = kaiju1;
        k2 = kaiju2;
    }

    /**
     This method steps one round in the combat.

     Returns:
     winner : string - name of the winner of the round. If this round results in
     a draw, return "DRAW". If there is no winner yet,
     value is "NONE"
     */
    public String stepRound(){
        String res;

        res = stepTurn(1);
        switch(res) {

            case "WIN":  return k1.name;
            case "LOSS": return k2.name;
            case "DRAW": return "DRAW";
        }
        res = stepTurn(2);
        switch(res) {

            case "WIN":  return k2.name;
            case "LOSS": return k1.name;
            case "DRAW": return "DRAW";
        }
        return "NONE";
    }

    /**
     This method steps one turn in the combat.

     Parameters:
     kaijuId : int - this value is 1 if it is kaiju1's turn and 2 otherwise

     Returns "WIN" if the kaiju eliminated the opponent this turn, "DRAW" if
     both kaiju got knocked out, "LOSS" if only the kaiju taking their turn was
     knocked out, and "NONE" otherwise
     */
    public String stepTurn(int kaijuId){


        Kaiju player;
        Kaiju opponent;


        if(kaijuId == 1)
        {
            player = this.k1;
            opponent = this.k2;
        }

        else // k2's turn
        {
            player = this.k2;
            opponent = this.k1;

        }




        if(initial)
        {
            player.useMove(player.nextMove, opponent, sb);
            initial = false;

        }


        else
        {

            player.useMove(player.applyTransition(opponent.nextMove, opponent.getStatus()), opponent, sb);

            if(player.currHp <= 0 && opponent.currHp <= 0)
                return "DRAW";
            else if(opponent.currHp <= 0)
                return "WIN";
            else if(player.currHp <= 0)
                return "LOSS";


        }



        return "NONE";






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

        Map<String, String[]> t = new HashMap<>();
        Map<String, String[]> mF = new HashMap<>();
        String key;
        StringBuilder kb = new StringBuilder();

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

                    kb.append(transitions[ind][0]).append(transitions[ind][1]).append(transitions[ind][2]);
                    key = kb.toString();

                    t.put(key, transitions[ind]);
                    mF.put(key, moveFunc[ind]);

                    ind++;
                    kb.setLength(0);
                }
            }
        }

        // read initial values
        String[] parts = br.readLine().trim().split(" ");
        String initState = parts[0];
        int maxHp = Integer.parseInt(parts[1]);
        String initMove = br.readLine().trim();

        return new Kaiju(name,states,moves,t,mF,initState,
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