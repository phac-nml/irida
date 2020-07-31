import React from "react";

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

function launchReducer(state, action) {
  switch (action.type) {
    case "fetch":
      return { ...state, loading: true };
    default:
      throw new Error(`Unhandled action type: ${action.type}`);
  }
}

function LaunchProvider({ children }) {
  const [state, dispatch] = React.useReducer(launchReducer, { loading: true });

  return (
    <LaunchStateContext.Provider value={state}>
      <LaunchDispatchContext value={dispatch}>{children}</LaunchDispatchContext>
    </LaunchStateContext.Provider>
  );
}
