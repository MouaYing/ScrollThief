package scrollthief.model;

import java.util.EventListener;

public interface StateChangedListener extends EventListener {
  public void stateChanged(StateChange evt);
}
