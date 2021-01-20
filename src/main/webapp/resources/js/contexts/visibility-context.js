import React from "react";

/**
 * The context passes the visibility state, so it is accessible by components at different nesting levels.
 */
const VisibilityContext = React.createContext();

VisibilityContext.name = "VisibilityContext";

/**
 *  The provider exposes a component to pass the visibility state.
 */
function VisibilityProvider({ children }) {
  const [visible, setVisibility] = React.useState(false);
  return (
    <VisibilityContext.Provider value={[visible, setVisibility]}>
      {children}
    </VisibilityContext.Provider>
  );
}

/**
 * The consumer gets the provided context from within a VisibilityProvider.
 */
function useVisibility() {
  const context = React.useContext(VisibilityContext);

  if (context === undefined) {
    throw new Error("useVisibility should be used within a VisibilityProvider");
  }

  return context;
}

export { VisibilityProvider, useVisibility };
