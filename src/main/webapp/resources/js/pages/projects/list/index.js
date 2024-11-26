import React from "react";
import { createRoot } from 'react-dom/client';
import { ProjectsTable } from "./ProjectsTable";

const container = document.getElementById('root');
const root = createRoot(container);
root.render(<ProjectsTable />);
