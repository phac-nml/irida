import React, { useEffect } from "react";
import { setBaseUrl } from "../../utilities/url-utilities";

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

  // Get the pipeline id
  const url = new URL(window.location.href);
  const params = new URLSearchParams(url.search);
  const id = params.get("pipeline");

  useEffect(() => {
    fetch(setBaseUrl(`/ajax/pipelines/${id}`), {})
      .then((response) => response.json())
      .then((json) => console.log(json));
  }, [id]);

  return (
    <LaunchStateContext.Provider value={state}>
      <LaunchDispatchContext.Provider value={dispatch}>
        {children}
      </LaunchDispatchContext.Provider>
    </LaunchStateContext.Provider>
  );
}

export { LaunchProvider };
