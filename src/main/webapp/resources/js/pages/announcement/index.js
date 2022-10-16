import { createRoot } from "react-dom/client";
import React from "react";
import { AnnouncementsPage } from "./components/AnnouncementsPage";

const root = createRoot(document.querySelector("#announcements-root"));
root.render(<AnnouncementsPage />);
