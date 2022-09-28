import React from "react";
import { IJsonModel, Layout, Model, TabNode } from "flexlayout-react";
import { Legend } from "./Legend";
import { PhyloCanvasTree } from "./PhyloCanvasTree";
import "flexlayout-react/style/light.css";

const layoutJson: IJsonModel = {
  global: {
    tabEnableClose: false,
  },
  borders: [
    {
      type: "border",
      location: "right",
      children: [
        {
          type: "tab",
          id: "#d460f47c-6a58-4334-a180-e6ca11e683b9",
          name: "Legend",
          component: "legend",
        },
      ],
    },
  ],
  layout: {
    type: "row",
    id: "#2f5270c0-e722-437b-98e4-745d95c27595",
    children: [
      {
        type: "tabset",
        id: "#fa0d9260-76be-4e84-b26d-d2b486af2cd4",
        weight: 10.681565796779568,
        children: [
          {
            type: "tab",
            id: "#ecfc6ca9-0781-4be7-a5f5-dae946bfdffe",
            name: "Grid 1",
            component: "phylocanvas",
          },
        ],
        active: true,
        enableTabStrip: false,
      },
    ],
  },
};

export default function LayoutComponent(): JSX.Element {
  const factory = (node: TabNode): JSX.Element => {
    const component = node.getComponent();

    if (component === "phylocanvas") {
      const { width, height } = node.getRect();

      return <PhyloCanvasTree height={height} width={width} />;
    } else if (component === "legend") {
      return <Legend />;
    }
    throw new Error(`Unknown component: ${component}`);
  };

  return <Layout model={Model.fromJson(layoutJson)} factory={factory} />;
}
