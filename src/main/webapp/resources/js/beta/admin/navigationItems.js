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
import { NavLink } from "react-router-dom";

const RouterLink = ({ to, icon, text }) => {
  const CustomLink = React.useMemo(
    () =>
      React.forwardRef((navLinkProps, ref) => {
        const { className: prevClasses, ...rest } = navLinkProps;
        const elementClasses = prevClasses?.toString() ?? "";

        return (
          <NavLink
            to={to}
            {...rest}
            ref={ref}
            className={({ isActive }) =>
              isActive ? elementClasses + " Mui-selected" : elementClasses
            }
          />
        );
      }),
    [to]
  );

  return (
    <ListItemButton component={CustomLink}>
      <ListItemIcon
        sx={{
          ".Mui-selected > &": {
            color: (theme) => theme.palette.primary.main,
          },
        }}
      >
        {icon}
      </ListItemIcon>
      <ListItemText primary={text} />
    </ListItemButton>
  );
};

export const navigationItems = (
  <>
    <RouterLink to="/" text={"Statistics"} icon={<AnalyticsIcon />} />
    <RouterLink to="/users" text={"Users"} icon={<PeopleIcon />} />
    <RouterLink to="/user-groups" text={"User Groups"} icon={<GroupsIcon />} />
    <RouterLink to="/clients" text={"Client"} icon={<ComputerIcon />} />
    <RouterLink
      to="/remote-connections"
      text={"Client"}
      icon={<RemoteIcon />}
    />
    <RouterLink
      to="/ncbi-exports"
      text={"Statistics"}
      icon={<IosShareIcon />}
    />
    <RouterLink
      to="/announcements"
      text={"Announcements"}
      icon={<NotificationsIcon />}
    />
  </>
);
