package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *  Use to transfer both a CurrentStep and HistoryStep
 *
 */

public class Step implements IsSerializable{

    public long id;
    public String name;
    public String status;
    public String owner;
    
    public String actor;
    public String action;
    public String startDate;
    public String dueDate;
    public String finishDate;

    public Step() {}
    
    // Current and History Step
    public Step id(long id) {this.id = id; return this;}
    public Step name(String name) {this.name = name; return this;}
    public Step status(String status) {this.status = status; return this;}
    public Step owner(String owner) {this.owner = owner; return this;}

    // History Step only
    public Step actor(String actor) {this.actor = actor; return this;}
    public Step action(String action) {this.action = action; return this;}
    public Step startDate(String startDate) {this.startDate = startDate; return this;}
    public Step dueDate(String dueDate) {this.dueDate = dueDate; return this;}
    public Step finishDate(String finishDate) {this.finishDate = finishDate; return this;}

   
}
