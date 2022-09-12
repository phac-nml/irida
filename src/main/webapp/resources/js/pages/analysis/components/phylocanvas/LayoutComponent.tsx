import React, { useRef } from "react";
import { Layout, Model, IJsonModel, ILayoutProps } from "flexlayout-react";
import { useDispatch } from "react-redux";
import { resize } from "../../redux/treeSlice";
import { Legend } from "./Legend";
import { PhylocanvasTree } from "./PhylocanvasTree";
import "flexlayout-react/style/light.css";

const layoutJson: IJsonModel = {
  global: {
    // tabEnableFloat: true,
    // tabSetMinWidth: 100,
    // tabSetMinHeight: 100,
    // borderMinSize: 100,
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
  const dispatch = useDispatch();

  const factory = (node): JSX.Element => {
    const component = node.getComponent();

    if (component === "phylocanvas") {
      console.log("PHYLOCANVAS");
      const { width, height } = node._rect;

      return <PhylocanvasTree height={height} width={width} />;
    } else if (component === "legend") {
      return <Legend />;
    }
  };

  return <Layout model={Model.fromJson(layoutJson)} factory={factory} />;
}
