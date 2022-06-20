import React from 'react';
import { render } from "react-dom";
import ProjectSPA from "../../ProjectSPA";

export { default } from "./CreateNcbiExport";

// TODO: This will need to be moved up as the project SPA gets created.
render(<ProjectSPA />, document.getElementById("root"));