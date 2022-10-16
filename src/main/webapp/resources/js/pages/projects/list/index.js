import React from "react";
import { createRoot } from "react-dom/client";
import { ProjectsTable } from "./ProjectsTable";

const root = createRoot(document.querySelector("#root"));
root.render(<ProjectsTable />);
