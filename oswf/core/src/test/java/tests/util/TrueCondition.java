package tests.util;

import org.informagen.typedmap.TypedMap;

import org.informagen.oswf.Condition;

import java.util.Map; 


public class TrueCondition implements Condition {
    public boolean passesCondition(Map<String,Object> transientVars, Map<String,String> args, TypedMap persistentVars) {
        return true;
    }
}