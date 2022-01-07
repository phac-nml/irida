import React from "react";
import {
  getFastQCDetails,
  getOverRepresentedSequences,
} from "../../apis/files/sequence-files";

const FastQCStateContext = React.createContext();
const FastQCDispatchContext = React.createContext();

const TYPES = {
  LOADING: "FASTQC:LOADING",
  LOADED: "FASTQC:LOADED",
  DETAILS: "FASTQC:DETAILS",
};

function reducer(state, action) {
  switch (action.type) {
    case TYPES.LOADED:
      return { ...state, ...action.payload, loading: false };
    case TYPES.LOADING:
      return { ...state, loading: true };
    default:
      throw new Error(`Unhandled action type: ${action.type}`);
  }
}

function FastQCProvider({ children, sequenceObjectId, fileId }) {
  const [state, dispatch] = React.useReducer(reducer, { loading: true });

  React.useEffect(() => {
    getFastQCDetails(sequenceObjectId, fileId).then(
      ({ analysisFastQC, sequenceFile, sequencingObject }) => {
        dispatch({
          type: TYPES.LOADED,
          payload: {
            loading: false,
            fastQC: analysisFastQC,
            file: sequenceFile,
            processingState: sequencingObject.processingState,
          },
        });
      }
    );
  }, []);

  async function getOverrepresentedDetails() {
    dispatch({ type: TYPES.LOADING });
    getOverRepresentedSequences(sequenceObjectId, fileId).then(
      (analysisFastQC) => {
        dispatch({
          type: TYPES.LOADED,
          payload: {
            analysisFastQC,
          },
        });
      }
    );
  }

  return (
    <FastQCStateContext.Provider value={state}>
      <FastQCDispatchContext.Provider value={{ getOverrepresentedDetails }}>
        {children}
      </FastQCDispatchContext.Provider>
    </FastQCStateContext.Provider>
  );
}

function useFastQCState() {
  const context = React.useContext(FastQCStateContext);
  if (context === undefined) {
    throw new Error("useFastQCState must be used within a FastQCProvider");
  }
  return context;
}

function useFastQCDispatch() {
  const context = React.useContext(FastQCDispatchContext);
  if (context === undefined) {
    throw new Error("useFastQCDispatch must be used within a FastQCProvider");
  }
  return context;
}

export { FastQCProvider, useFastQCState, useFastQCDispatch };
