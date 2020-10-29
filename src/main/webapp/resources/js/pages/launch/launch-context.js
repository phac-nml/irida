import React from "react";
import {
  getPipelineDetails,
  launchPipeline,
  saveNewPipelineParameters,
} from "../../apis/pipelines/pipelines";
import {
  deepCopy,
  formatDefaultPipelineName,
  formatParametersWithOptions,
  formatSavedParameterSets,
} from "./launch-utilities";
import { Tag } from "antd";

/**
 * @file React Context for providing to access to shared data and actions for the
 * IRIDA Workflow launch system.
 * @type {React.Context<unknown>}
 */

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
        parameterSets: action.payload.sets,
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
      ({
        name,
        description,
        type,
        parameterWithOptions,
        savedPipelineParameters,
        ...details
      }) => {
        /*
        These set up immutable page state for the header.
         */
        setPipeline({ name, description });

        const formattedParameterWithOptions = formatParametersWithOptions(
          parameterWithOptions
        );

        const formattedParameterSets = formatSavedParameterSets(
          savedPipelineParameters
        );

        const initial = {
          name: formatDefaultPipelineName(type, Date.now()),
          parameterSet: 0,
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
            parameterSet: deepCopy(formattedParameterSets[0]), // This will be the default set of saved parameters
            parameterWithOptions: formattedParameterWithOptions,
            parameterSets: formattedParameterSets,
          },
        });
      }
    );
  }, [id]);

  function dispatchUseSavedParameterSet(id) {
    const set = state.parameterSets.find((p) => p.id === id);
    dispatch({
      type: TYPES.PARAMETER_SET,
      payload: { set: deepCopy(set) },
    });
  }

  function dispatchLaunch(values) {
    launchPipeline(id, values);
  }

  /**
   * Dispatch function called when a user modifies the current saved parameter
   * set parameter values, and wants to use them without saving.
   *
   * @param {array} parameters - list of key value pairs for the parameters ({mame: value})
   */
  function dispatchUseModifiedParameters(parameters) {
    /*
    Suffix to be added to the identifier to identify when it is modified
     */
    const SUFFIX = `-MODIFIED`;

    /*
    Get a copy of the currently display parameter set.
     */
    const currentSet = deepCopy(state.parameterSet);

    /*
    Get a copy of all the sets
     */
    const sets = deepCopy(state.parameterSets);

    /*
    Update the parameters to the new values
     */
    currentSet.parameters = currentSet.parameters.map((parameter) => ({
      ...parameter,
      value: parameters[parameter.name],
    }));

    /*
    Three different states:
      1. Set that has not been modified before
      2. Set that has been modified before and currently selected
      3. Set that has been modified before but the original is selected
     */

    if (`${currentSet.key}`.endsWith(SUFFIX)) {
      // Previously modified and selected.
      // Remove the modified one from the list and add the updated one
      const updatedSets = sets.filter((a) => a.id !== currentSet.id);
      updatedSets.push(currentSet);

      dispatch({
        type: TYPES.MODIFIED_PARAMETERS,
        payload: {
          sets: updatedSets,
          set: currentSet,
        },
      });
    } else if (!currentSet.modified) {
      // First time modified
      currentSet.modified = true;
      // Set modified to the original set
      sets.find((s) => s.id === currentSet.id).modified = true;

      // Update the id to show that it has been modified
      currentSet.id = `${currentSet.id}${SUFFIX}`;
      currentSet.key = `set-${currentSet.id}`;

      // Add the current set to the list
      sets.push(currentSet);

      // Update the data model
      dispatch({
        type: TYPES.MODIFIED_PARAMETERS,
        payload: {
          sets,
          set: currentSet,
        },
      });
    } else {
      // Need to find the actual modified set
      const index = sets.findIndex((s) => s.id === `${currentSet.id}${SUFFIX}`);
      // Remove the current set.
      const [item] = sets.splice(index, 1);

      // Use the new modified parameters
      item.parameters = currentSet.parameters;
      sets.push(item);

      dispatch({
        type: TYPES.MODIFIED_PARAMETERS,
        payload: {
          sets,
          set: item,
        },
      });
    }
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

  async function dispatchUseSaveAs(name, parameters) {
    /*
    Get a copy of the currently display parameter set.
     */
    const currentSet = deepCopy(state.parameterSet);

    /*
    Update the parameters to the new values
     */
    const params = currentSet.parameters.map((parameter) => ({
      ...parameter,
      value: parameters[parameter.name],
    }));

    const data = await saveNewPipelineParameters({
      label: name,
      parameters: params,
      id,
    });
    console.log(data);
  }

  return (
    <LaunchStateContext.Provider value={{ ...state, pipeline, initialValues }}>
      <LaunchDispatchContext.Provider
        value={{
          dispatchLaunch,
          dispatchUseSavedParameterSet,
          dispatchUseModifiedParameters,
          dispatchOverwriteParameterSave,
          dispatchUseSaveAs,
        }}
      >
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
