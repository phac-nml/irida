import * as React from "react";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import ListItemIcon from "@mui/material/ListItemIcon";
import AnalyticsIcon from "@mui/icons-material/Analytics";
import PeopleIcon from "@mui/icons-material/People";
import GroupsIcon from "@mui/icons-material/Groups";
import RemoteIcon from "@mui/icons-material/Cable";
import IosShareIcon from "@mui/icons-material/IosShare";
import NotificationsIcon from "@mui/icons-material/Notifications";
import ComputerIcon from "@mui/icons-material/Computer";

export const navigationItems = (
  <>
    <ListItemButton>
      <ListItemIcon>
        <AnalyticsIcon />
      </ListItemIcon>
      <ListItemText primary={"Statistics"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <PeopleIcon />
      </ListItemIcon>
      <ListItemText primary={"Users"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <GroupsIcon />
      </ListItemIcon>
      <ListItemText primary={"User Groups"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <ComputerIcon />
      </ListItemIcon>
      <ListItemText primary={"Clients"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <RemoteIcon />
      </ListItemIcon>
      <ListItemText primary={"Remote Connection"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <IosShareIcon />
      </ListItemIcon>
      <ListItemText primary={"NCBI Exports"} />
    </ListItemButton>
    <ListItemButton>
      <ListItemIcon>
        <NotificationsIcon />
      </ListItemIcon>
      <ListItemText primary={"Announcements"} />
    </ListItemButton>
  </>
);
