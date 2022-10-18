import React from "react";
import { UserOutlined } from "@ant-design/icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { isAdmin } from "../../utilities/role-utilities";
import type { ItemType } from "antd/es/menu/hooks/useItems";

export const accountMenu: ItemType = {
  key: "account",
  label: <UserOutlined />,
  children: [
    {
      key: "account:user",
      label: (
        <a href={setBaseUrl(`/users/current`)}>{i18n("nav.main.account")}</a>
      ),
    },
    {
      key: "account:help",
      label: i18n("nav.main.help"),
      children: [
        {
          key: "account:help:guide",
          label: (
            <a
              href="https://phac-nml.github.io/irida-documentation/user/user/"
              target="_blank"
              rel="noreferrer"
            >
              {i18n("nav.main.userguide")}
            </a>
          ),
        },
        ...(isAdmin()
          ? [
              {
                key: "account:help:admin",
                label: (
                  <a
                    href="https://phac-nml.github.io/irida-documentation/user/administrator/"
                    target="_blank"
                    rel="noreferrer"
                  >
                    {i18n("nav.main.adminguide")}
                  </a>
                ),
              },
            ]
          : []),
        {
          type: "divider",
        },
        {
          key: "account:help:website",
          label: (
            <a
              href="http://www.irida.ca"
              target="_blank"
              rel="noopener noreferrer"
            >
              {i18n("generic.irida.website")}
            </a>
          ),
        },
        {
          type: "divider",
        },
        {
          key: "account:help:version",
          label: i18n("irida.version"),
          disabled: true,
        },
      ],
    },
    {
      key: "account:logout",
      label: <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>,
    },
  ],
};
