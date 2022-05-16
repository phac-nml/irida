// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require("prism-react-renderer/themes/github");
const darkCodeTheme = require("prism-react-renderer/themes/dracula");

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: "IRIDA Documentation",
  tagline: "Integrated Rapid Infectious Disease Analysis",
  url: "https://phac-nml.github.io",
  baseUrl: "/irida-documentation/",
  onBrokenLinks: "throw",
  onBrokenMarkdownLinks: "warn",
  favicon: "img/favicon.ico",

  organizationName: "phac-nml",
  projectName: "irida-documentation",

  i18n: {
    defaultLocale: "en",
    locales: ["en"],
  },

  presets: [
    [
      "classic",
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          routeBasePath: "/",
          sidebarPath: require.resolve("./sidebars.js"),
          sidebarCollapsible: false,
          lastVersion: "current",
          versions: {
            current: {
              label: "22.05-SNAPSHOT",
            },
          },
        },
        blog: false,
      }),
    ],
    [
      "redocusaurus",
      {
        specs: [
          {
            spec: "static/open-api.json",
            route: "/api/",
          },
        ],
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: "IRIDA Documentation",
        logo: {
          alt: "IRIDA Logo",
          src: "img/IRIDA_LIGHT_300px.png",
        },
        items: [
          {
            type: "doc",
            docId: "introduction/getting-started",
            label: "Docs",
            position: "left",
          },
          { to: "/api", label: "API", position: "left" },
          {
            to: "/javadoc",
            label: "JAVADOC",
            position: "left",
          },
          {
            type: "docsVersionDropdown",
            position: "right",
            dropdownItemsAfter: [{ to: "/versions", label: "All versions" }],
            dropdownActiveClassDisabled: true,
          },
          {
            href: "https://github.com/phac-nml/irida",
            label: "GitHub",
            position: "right",
          },
        ],
      },
      footer: {
        style: "dark",
        links: [
          {
            title: "Docs",
            items: [
              { label: "API Reference", to: "/api" },
              { label: "JAVADOC", to: "/javadoc" },
            ],
          },
          {
            title: "Community",
            items: [
              {
                label: "Gitter",
                href: "https://gitter.im/irida-project",
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} IRIDA`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};

module.exports = config;
