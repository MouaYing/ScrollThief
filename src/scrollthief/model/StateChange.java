package scrollthief.model;

import java.util.EventObject;

public class StateChange extends EventObject {
  public StateChange(Object source) {
    super(source);
  }
}