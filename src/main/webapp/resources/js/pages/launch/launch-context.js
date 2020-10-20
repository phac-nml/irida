import React from "react";
import { getPipelineDetails } from "../../apis/pipelines/pipelines";

/**
 * @file React Context for providing to access to shared data and actions for the
 * IRIDA Workflow launch system.
 * @type {React.Context<unknown>}
 */

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

const TYPES = {
  LOADED: "launch:loaded",
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      return { ...state, loading: false, ...action.details };
  }
};

function LaunchProvider({ children }) {
  const [id] = React.useState(() => {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
  });

  const [state, dispatch] = React.useReducer(reducer, { loading: true });

  React.useEffect(() => {
    getPipelineDetails({ id }).then((data) => {
      const { name, description, ...details } = data;
      details.pipeline = { name, description };
      dispatch({ type: TYPES.LOADED, details });
    });
  }, [id]);

  return (
    <LaunchStateContext.Provider value={state}>
      <LaunchDispatchContext.Provider value={dispatch}>
        {children}
      </LaunchDispatchContext.Provider>
    </LaunchStateContext.Provider>
  );
}

/*
 * Custom React hooks to get access to the contexts, prevents needing to wrap your child
 * components in context consumers.
 * See: {@link https://kentcdodds.com/blog/how-to-use-react-context-effectively#the-custom-consumer-hook}
 * @returns {unknown}
 */

function useLaunchState() {
  const context = React.useContext(LaunchStateContext);
  if (context === undefined) {
    throw new Error(`useLaunchState must be used with a LaunchProvider`);
  }
  return context;
}

function useLaunchDispatch() {
  const context = React.useContext(LaunchDispatchContext);
  if (context === undefined) {
    throw new Error(`useLaunchDispatch must be used with a LaunchProvider`);
  }
  return context;
}

export { LaunchProvider, useLaunchState, useLaunchDispatch };
