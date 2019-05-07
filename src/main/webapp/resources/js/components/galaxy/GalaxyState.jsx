import React, { createContext, useContext, useReducer } from "react";

export const GalaxyState = createContext();

export const GalaxyStateProvider = ({ reducer, initialState, children }) => (
  <GalaxyState.Provider value={useReducer(reducer, initialState)}>
    {children}
  </GalaxyState.Provider>
);

export const useStateValue = () => useContext(GalaxyState);
