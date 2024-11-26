import { createRoot } from 'react-dom/client';
import React from "react";
import { AnnouncementsPage } from "./components/AnnouncementsPage";

const container = document.getElementById('announcements-root');
const root = createRoot(container);
root.render(<AnnouncementsPage />);
