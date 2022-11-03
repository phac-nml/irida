import React from "react";
import { createRoot } from "react-dom/client";
import { ProjectsTable } from "./ProjectsTable";

const ROOT_ELEMENT = document.querySelector("#root");
const root = createRoot(ROOT_ELEMENT);
root.render(<ProjectsTable />);
