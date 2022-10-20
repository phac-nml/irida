import type { MenuProps } from "antd";
import { Menu } from "antd";
import React, { useMemo } from "react";
import { useLocation, useNavigate } from "react-router-dom";

interface SettingNavProps {
  showRemote: boolean;
  canManage: boolean;
}

/**
 * Component to handle navigation within the project settings page
 * @returns {JSX.Element}
 * @constructor
 */
export default function SettingsNav({
  showRemote = false,
  canManage = false,
}: SettingNavProps): JSX.Element {
  const location = useLocation();
  const navigate = useNavigate();

  const [key, setKey] = React.useState(() => {
    const keyRegex = /\/projects\/\d+\/settings\/(?<path>[\w_-]+)/;
    const found = location.pathname.match(keyRegex);
    if (found && found.groups) {
      return `ps:${found.groups.path}`;
    }
    return "ps:details";
  });

  const menuItems: MenuProps["items"] = useMemo(
    () => [
      {
        key: "ps:details",
        label: i18n("project.settings.page.details"),
      },
      {
        key: "ps:processing",
        label: i18n("project.settings.page.processing"),
      },
      {
        key: "ps:members",
        label: i18n("project.settings.page.members"),
      },
      {
        key: "ps:groups",
        label: i18n("project.settings.page.groups"),
      },

      {
        key: "ps:metadata-fields",
        label: i18n("MetadataFields.title"),
      },
      {
        key: "ps:metadata-templates",
        label: i18n("ProjectMetadataTemplates.title"),
      },
      {
        key: "ps:associated",
        label: i18n("project.settings.page.associated"),
      },
      {
        key: "ps:references",
        label: i18n("project.settings.page.referenceFiles"),
      },
      ...(showRemote
        ? [
            {
              key: "ps:remote",
              label: i18n("project.settings.page.remote"),
            },
          ]
        : []),
      ...(canManage
        ? [
            {
              key: "ps:delete",
              label: i18n("DeleteProject.title"),
            },
          ]
        : []),
    ],
    [canManage, showRemote]
  );

  const onClick: MenuProps["onClick"] = ({ key }) => {
    setKey(key);
    navigate(key.substring(key.indexOf(":") + 1));
  };

  return (
    <Menu
      selectedKeys={[key]}
      onClick={onClick}
      items={menuItems}
      mode="inline"
    />
  );
}
