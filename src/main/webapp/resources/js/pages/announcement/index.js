import { createRoot } from "react-dom/client";
import React from "react";
import { AnnouncementsPage } from "./components/AnnouncementsPage";

const ROOT_ELEMENT = document.querySelector("#announcements-root");
const root = createRoot(ROOT_ELEMENT);
root.render(<AnnouncementsPage />);
