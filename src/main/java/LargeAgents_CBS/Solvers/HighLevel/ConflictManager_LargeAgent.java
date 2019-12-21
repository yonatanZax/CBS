package LargeAgents_CBS.Solvers.HighLevel;

import BasicCBS.Instances.Agent;
import BasicCBS.Instances.Maps.I_Location;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictManager;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.ConflictSelectionStrategy;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.AgentAtGoal;
import BasicCBS.Solvers.ConstraintsAndConflicts.ConflictManagement.DataStructures.TimeLocation;
import BasicCBS.Solvers.ConstraintsAndConflicts.SwappingConflict;
import BasicCBS.Solvers.Move;
import BasicCBS.Solvers.SingleAgentPlan;
import GraphMapPackage.GraphMapVertex_LargeAgents;
import LargeAgents_CBS.Instances.Maps.GraphLocationGroup;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConflictManager_LargeAgent extends ConflictManager_Shapes {


    private Map<GraphMapVertex_LargeAgents,Set<AgentAtGoal>> intersectingWithCellAtGoal = new HashMap<>();

    public ConflictManager_LargeAgent(ConflictSelectionStrategy conflictSelectionStrategy) {
        super(conflictSelectionStrategy);
    }

    public ConflictManager_LargeAgent(ConflictManager_Shapes other) {
        super(other);
    }


    @Override
    public void addPlan(SingleAgentPlan singleAgentPlan) {

        /*  = Add methods =  */
        this.agent_plan.put(singleAgentPlan.agent, singleAgentPlan); // Updates if already exists
        this.addAgentNewPlan(singleAgentPlan);
    }


    @Override
    protected void manageGoalLocationFromPlan(int goalTime, SingleAgentPlan singleAgentPlan) {

        GraphLocationGroup goalLocation = (GraphLocationGroup) singleAgentPlan.moveAt(goalTime).currLocation;

        TimeLocation goalTimeLocation = new TimeLocation(goalTime, goalLocation);

        /*  = Check if this agentAtGoal conflicts with other agents =   */
        this.checkAddSwappingConflicts(goalTime, singleAgentPlan);
        this.checkAddVertexConflictsWithGoal(goalTimeLocation, singleAgentPlan);


        /*  = Add goal timeLocation =  */
        for (GraphMapVertex_LargeAgents cell : goalLocation.getAllCells()) {
            this.intersectingWithCellAtGoal.computeIfAbsent(cell, k-> new HashSet<>());
            this.intersectingWithCellAtGoal.get(cell).add(new AgentAtGoal(singleAgentPlan.agent, goalTime));
        }
    }


    @Override
    protected void checkAddConflictsByTimeLocation(TimeLocation timeLocation, SingleAgentPlan singleAgentPlan) {

        Set<Agent> agentsAtTimeLocation = new HashSet<>();
        Set<AgentAtGoal> agentsAtGoal = new HashSet<>();

        Move move = this.agent_plan.get(singleAgentPlan.agent).moveAt(timeLocation.time);
        if( move == null){return;}
        GraphLocationGroup agentLocation = (GraphLocationGroup) move.currLocation;
        for (I_Location cellLocation : (agentLocation.getAllCells())) {
            Set<Agent> agentSet = this.timeLocationTables.getAgentsAtTimeLocation(new TimeLocation(timeLocation.time, cellLocation));
            if(agentSet != null)
                agentsAtTimeLocation.addAll(agentSet);
            agentsAtGoal = this.intersectingWithCellAtGoal.get(cellLocation) == null ? new HashSet<>() : this.intersectingWithCellAtGoal.get(cellLocation) ;
        }
        this.addVertexConflicts(timeLocation, singleAgentPlan.agent, agentsAtTimeLocation);

        /*  = Check conflicts with agents at their goal =    */

        for (AgentAtGoal agentAtGoal : agentsAtGoal) {
            if( agentAtGoal != null ){
                if ( timeLocation.time >= agentAtGoal.time ){
                    // Adds a Vertex conflict if time at location is greater than another agent time at goal
                    this.addVertexConflicts(timeLocation, singleAgentPlan.agent, new HashSet<>(){{add(agentAtGoal.agent);}});
                }
            }
        }


        /*      = Check for swapping conflicts =     */
        this.checkAddSwappingConflicts(timeLocation.time, singleAgentPlan);
    }


    @Override
    protected void addVertexConflicts(TimeLocation timeLocation, Agent agent, Set<Agent> agentsAtTimeLocation) {

        if( agentsAtTimeLocation == null ){ return; }

        for (Agent agentConflictsWith : agentsAtTimeLocation) {
            if( agentConflictsWith.equals(agent) ){ continue; /* Self Conflict */ }

            int agentEndTime = this.agent_plan.get(agent).getEndTime();
            Move currAgentMove = this.agent_plan.get(agent).moveAt(Math.min(timeLocation.time, agentEndTime));
            GraphLocationGroup currentAgentLocation = (GraphLocationGroup) currAgentMove.currLocation;
            Move otherAgentMove = this.agent_plan.get(agentConflictsWith).moveAt(timeLocation.time);
            GraphLocationGroup otherAgentLocation = (GraphLocationGroup) otherAgentMove.currLocation;
            GraphLocationGroup intersection = GraphLocationGroup.findIntersection(currentAgentLocation, otherAgentLocation);
            VertexConflict_LargeAgent vertexConflict = new VertexConflict_LargeAgent(agent,agentConflictsWith, new TimeLocation(timeLocation.time, intersection));

            // Add conflict to both of the agents
            this.allConflicts.add(vertexConflict);
        }
    }



    protected void checkAddSwappingConflicts(int time, SingleAgentPlan singleAgentPlan) {
        if( time < 1 ){ return;}
        I_Location previousLocation = singleAgentPlan.moveAt(time).prevLocation;
        I_Location nextLocation = singleAgentPlan.moveAt(time).currLocation;

        Set<Agent> agentsMovingToPrevLocations = new HashSet<>();
        for (I_Location cellLocation : ((GraphLocationGroup)previousLocation).getAllCells()) {
            Set<Agent> agentsMovingToCell = (this.timeLocationTables.timeLocation_Agents.get(new TimeLocation(time,cellLocation)));
            if (agentsMovingToCell != null)
                agentsMovingToPrevLocations.addAll(agentsMovingToCell);
        }
        if ( agentsMovingToPrevLocations.isEmpty() ){ return; }

        /* Add conflict with all the agents that:
            1. Coming from agent's moveAt(time).currLocation
            2. Going to agent's moveAt(time).prevLocation
        */
        for (Agent agentMovingToPrevPosition : agentsMovingToPrevLocations) {
            if( agentMovingToPrevPosition.equals(singleAgentPlan.agent) ){ continue; /* Self Conflict */ }
            if ( this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).prevLocation.intersectsWith(nextLocation)){

                I_Location agentMovingToPrevPosition_prevLocation = this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).prevLocation;
                I_Location agentMovingToPrevPosition_nextLocation = this.agent_plan.get(agentMovingToPrevPosition).moveAt(time).currLocation;
                I_Location intersectionAtPrevLocation = GraphLocationGroup.findIntersection(previousLocation,agentMovingToPrevPosition_nextLocation);
                I_Location intersectionAtCurLocation = GraphLocationGroup.findIntersection(nextLocation,agentMovingToPrevPosition_prevLocation);
                // Create two conflicts
                SwappingConflict swappingConflict_addedAgentFirst = new SwappingConflict_LargeAgents(   singleAgentPlan.agent,
                                                                                                        agentMovingToPrevPosition,
                                                                                                        time,
                                                                                                        intersectionAtCurLocation,
                                                                                                        intersectionAtPrevLocation);
                SwappingConflict swappingConflict_addedAgentSecond = new SwappingConflict_LargeAgents(  agentMovingToPrevPosition,
                                                                                                        singleAgentPlan.agent,
                                                                                                        time,
                                                                                                        intersectionAtPrevLocation,
                                                                                                        intersectionAtCurLocation);



                // Add conflicts to both of the agents
                this.allConflicts.add(swappingConflict_addedAgentFirst);
                this.allConflicts.add(swappingConflict_addedAgentSecond);
            }
        }
    }




    @Override
    protected void checkAddVertexConflictsWithGoal(TimeLocation timeLocation, SingleAgentPlan singleAgentPlan){

        GraphLocationGroup groupLocation = (GraphLocationGroup) timeLocation.location;
        Set<GraphMapVertex_LargeAgents> allCells = groupLocation.getAllCells();
        Set<Integer> timeList = new HashSet<>();
        // A Set of time that at least one agent is occupying
        for (GraphMapVertex_LargeAgents locationCell : allCells) {
            Set timeOfCell = this.timeLocationTables.getTimeListAtLocation(locationCell);
            if( timeOfCell != null ){
                timeList.addAll(timeOfCell);
            }
        }


        if(timeList.isEmpty()){ return; /* There are no agents at timeLocation */ }

        // Check if other plans are using this location after the agent arrived at goal
        for (int time : timeList) {
            if( time > timeLocation.time){

                Set<Agent> agentsAtTimeLocation = new HashSet<>();
                for (GraphMapVertex_LargeAgents cellLocation : allCells) {
                     agentsAtTimeLocation.addAll(this.timeLocationTables.timeLocation_Agents.get(new TimeLocation(time,cellLocation)));
                }

                // Adds if agent != agentAtTimeLocation
                this.addVertexConflicts(new TimeLocation(time, groupLocation), singleAgentPlan.agent, agentsAtTimeLocation);
            }
        }
    }





}
