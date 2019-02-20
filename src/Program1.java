/*
 * Name: <your name>
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

/**
 * Your solution goes in this class.
 * 
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 * 
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution.
 */
public class Program1 extends AbstractProgram1 {
    /**
     * Determines whether a candidate Matching represents a solution to the
     * Stable Marriage problem. Study the description of a Matching in the
     * project documentation to help you with this.
     */
    public boolean isStableMatching(Matching allocation) {

        ArrayList<Integer> match = allocation.getUserMatching();
        ArrayList<ArrayList<Integer>> servers = allocation.getServerPreference();
        ArrayList<ArrayList<Integer>> users = allocation.getUserPreference();

        //find available server slots
        ArrayList<Integer> serverSlots = allocation.getServerSlots();
        System.out.println(serverSlots);
        for(Integer s : match){
            if(s != -1) serverSlots.set(s, (serverSlots.get(s) - 1));
        }
        System.out.println(match);
        System.out.println(serverSlots);

        //Check that two couples would not swap
        //given pairs u(user) and s(server), u'(nextUser) and s'(nextServer)

        for(Integer user = 0; user < match.size() - 1; user++){
            Integer server = match.get(user);

            //preference lists for u
            ArrayList<Integer> userPreference = users.get(user);

            for(Integer s: userPreference){
                if(s == server) break;
                else if(serverSlots.get(s) > 0) return false;
            }

            for(Integer nextUser = user + 1; nextUser < match.size(); nextUser++){ //compare user to all other users
                Integer nextServer = match.get(nextUser);

                //preference lists for u'
                ArrayList<Integer> nextUserPreference = users.get(nextUser);

                //check that u prefers s' and s' prefers u
                if(server != nextServer) {
                    if (nextServer != -1) {
                        ArrayList<Integer> nextServerPreference = servers.get(nextServer);
                        if ((userPreference.indexOf(server) > userPreference.indexOf(nextServer)) && (nextServerPreference.indexOf(nextUser) > nextServerPreference.indexOf(user)))
                            return false;

                    }
                    //check that unassigned u' is not preferred by s over u
                    else if ((nextServer == -1) && (server != -1)) {
                        ArrayList<Integer> serverPreference = servers.get(server);
                        if (serverPreference.indexOf(user) > serverPreference.indexOf(nextUser)) return false;
                    }
                    //check that u' prefers s and s prefers u'
                    if (server != -1) {
                        ArrayList<Integer> serverPreference = servers.get(server);
                        if ((nextUserPreference.indexOf(nextServer) > nextUserPreference.indexOf(server)) && (serverPreference.indexOf(user) > serverPreference.indexOf(nextUser)))
                            return false;
                    }
                    //check that unassigned u is not preferred by s' over u'
                    else if ((nextServer != -1) && (server == -1)) {
                        ArrayList<Integer> nextServerPreference = servers.get(nextServer);
                        if (nextServerPreference.indexOf(nextUser) > nextServerPreference.indexOf(user)) return false;
                    }
                }
            }
        }
        
        return true;
    }

    /**
     * Determines a solution to the Stable Marriage problem from the given input
     * set. Study the project description to understand the variables which
     * represent the input to your solution.
     * 
     * @return A stable Matching.
     */
    public Matching stableMarriageGaleShapley(Matching allocation) {

        ArrayList<ArrayList<Integer>> users = allocation.getUserPreference();
        ArrayList<ArrayList<Integer>> servers = allocation.getServerPreference();
        ArrayList<Integer> serverSlots = allocation.getServerSlots();
        ArrayList<Integer> userMatching = new ArrayList<Integer>();
        for(int i = 0; i < allocation.getUserCount(); i++) userMatching.add(-1);

        Queue<Integer> userQueue = new LinkedList();
        for(Integer i = 0; i < allocation.getUserCount(); i++) userQueue.add(i);


        while(true){
            Integer user = userQueue.remove();
            ArrayList<Integer> userPreferenceList = users.get(user);
            for(Integer i = 0; i < userPreferenceList.size(); i++){
                boolean stop = false;
                Integer serv = userPreferenceList.get(i);
                //matching for user has been found
                if(serverSlots.get(serv) > 0){
                    userMatching.set(user,serv); //add to matching
                    serverSlots.set(serv, (serverSlots.get(serv) - 1));
                    break;
                }
                //server is full but check if current user is preferred over other users
                else{
                    ArrayList<Integer> serverPreferenceList = servers.get(serv);
                    for(Integer targetUser = 0; targetUser < userMatching.size(); targetUser++){

                        //u is preferred by s over u'
                        if((userMatching.get(targetUser) == serv) && (serverPreferenceList.indexOf(user) < serverPreferenceList.indexOf(targetUser))){ //if
                            //swap users
                            userMatching.set(targetUser, -1);
                            userMatching.set(user,serv);  //add u to matching
                            userQueue.add(targetUser);  //add u' back to queue
                            stop = true;
                            break;
                        }
                    }

                }
                if(stop == true){
                    stop = false;
                    break;
                }
            }
            if(userQueue.isEmpty()) break; //matching completed
        }
        allocation.setUserMatching(userMatching);

        return allocation;
    }
}
