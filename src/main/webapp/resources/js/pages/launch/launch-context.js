import React from "react";
import {
  getPipelineDetails,
  launchPipeline,
} from "../../apis/pipelines/pipelines";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import {
  formatDefaultPipelineName,
  formatParametersWithOptions,
} from "./launch-utilities";

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

const TYPES = {
  LOADED: "launch:loaded",
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      return { ...state, loading: false, ...action.payload };
  }
};

function LaunchProvider({ children }) {
  /*
  IRIDA Workflow identifier can be found as a query parameter within the URL.
  Here we grab it and hold onto it so that we can use it to gather all the
  details about the pipeline.
   */
  const [id] = React.useState(() => {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
  });

  /*
  This will hold the initial values for the form, the "default values".
   */
  const [initialValues, setInitialValues] = React.useState({});

  /*
  Pipeline state is for non-user modifiable data such as the name of the official
  name of the pipeline and it's description.
   */
  const [pipeline, setPipeline] = React.useState({});

  /*
  Using a reducer to hold all user data that the user can modify and must be
  sent to the server to launch the workflow pipeline.
   */
  const [state, dispatch] = React.useReducer(reducer, { loading: true });

  React.useEffect(() => {
    getPipelineDetails({ id }).then(
      ({ name, description, type, parameterWithOptions, ...details }) => {
        setPipeline({ name, description });

        const formattedParameterWithOptions = formatParametersWithOptions(
          parameterWithOptions
        );

        const initial = {
          name: formatDefaultPipelineName(type, Date.now()),
        };

        // Get initial values for parameters with options.
        formattedParameterWithOptions.forEach((parameter) => {
          initial[parameter.name] =
            parameter.value || parameter.options[0].value;
        });

        setInitialValues(initial);
        dispatch({
          type: TYPES.LOADED,
          payload: {
            ...details,
            parameterWithOptions: formattedParameterWithOptions,
          },
        });
      }
    );
  }, [id]);

  function dispatchLaunch(values) {
    launchPipeline(id, values);
  }

  return (
    <LaunchStateContext.Provider value={{ ...state, pipeline, initialValues }}>
      <LaunchDispatchContext.Provider value={{ dispatchLaunch }}>
        {children}
      </LaunchDispatchContext.Provider>
    </LaunchStateContext.Provider>
  );
}

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
