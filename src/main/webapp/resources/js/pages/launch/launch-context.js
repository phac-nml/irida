import React from "react";
import {
  getPipelineDetails,
  launchPipeline,
} from "../../apis/pipelines/pipelines";
import {
  formatDefaultPipelineName,
  formatParametersWithOptions,
} from "./launch-utilities";

const LaunchStateContext = React.createContext();
const LaunchDispatchContext = React.createContext();

const TYPES = {
  LOADED: "launch:loaded",
  PARAMETER_SET: "launch:parameters",
  MODIFIED_PARAMETERS: "launch:modified_params",
};

const reducer = (state, action) => {
  switch (action.type) {
    case TYPES.LOADED:
      return { ...state, loading: false, ...action.payload };
    case TYPES.PARAMETER_SET:
      return { ...state, parameterSet: action.payload.set };
    case TYPES.MODIFIED_PARAMETERS:
      return {
        ...state,
        parameterSet: action.payload.set,
        savedPipelineParameters: [
          ...state.savedPipelineParameters,
          action.payload.set,
        ],
      };
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

        // Set the default parameter set
        initial.parameterSet = 0;

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
            parameterSet: JSON.parse(
              JSON.stringify(details.savedPipelineParameters[0])
            ), // This will be the default set of saved parameters
            parameterWithOptions: formattedParameterWithOptions,
          },
        });
      }
    );
  }, [id]);

  function dispatchUseSavedParameterSet(id) {
    const set = state.savedPipelineParameters.find((p) => p.id === id);
    dispatch({
      type: TYPES.PARAMETER_SET,
      payload: { set: JSON.parse(JSON.stringify(set)) },
    });
  }

  function dispatchLaunch(values) {
    launchPipeline(id, values);
  }

  function dispatchUseModifiedParameters(parameters) {
    const set = JSON.parse(JSON.stringify(state.parameterSet));
    set.parameters = set.parameters.map((parameter) => ({
      ...parameter,
      value: parameters[parameter.name],
    }));

    set.id = `${set.id}-1`;
    set.label = `${set.label} *modified`;

    dispatch({
      type: TYPES.MODIFIED_PARAMETERS,
      payload: { set },
    });
  }

  /**
   * Dispatch function for overwriting a saved parameter set.
   *
   * @param {object} parameters - the full listing of parameters to update.
   */
  function dispatchOverwriteParameterSave(parameters) {
    console.log(state.parameterSet.id);

    // TODO: Save parameters......
  }

  return (
    <LaunchStateContext.Provider value={{ ...state, pipeline, initialValues }}>
      <LaunchDispatchContext.Provider
        value={{
          dispatchLaunch,
          dispatchUseSavedParameterSet,
          dispatchUseModifiedParameters,
          dispatchOverwriteParameterSave,
        }}
      >
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
