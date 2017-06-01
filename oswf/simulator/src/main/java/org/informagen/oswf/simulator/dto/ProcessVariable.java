package org.informagen.oswf.simulator.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProcessVariable implements IsSerializable {
    
    public String name;
    public String type;
    public String value;

    public ProcessVariable() {}

    public ProcessVariable(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public ProcessVariable name(String name) {this.name = name; return this; }
    public ProcessVariable type(String type) {this.type = type; return this; }
    public ProcessVariable value(String value) {this.value = value; return this; }
   
}
