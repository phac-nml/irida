import { Button, Dropdown, Menu } from "antd";
import React, { useMemo } from "react";
import { getCurrentTreeType, updateTreeType } from "../../redux/treeSlice";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import {
  PhyloCircularIcon,
  PhyloDiagonalIcon,
  PhyloHierarchicalIcon,
  PhyloRadialIcon,
  PhyloRectangleIcon,
} from "../../../../components/icons/phylocanvas";
import { useAppDispatch, useAppSelector } from "../../store";
import { TreeType } from "../../../../types/phylocanvas";

/**
 * React component to render a drop-down menu for selecting the type of phylogenetic tree to display
 * @constructor
 */
export default function PhylocanvasShapeDropDown() {
  const dispatch = useAppDispatch();
  const [options, setOptions] = React.useState<JSX.Element[]>([]);
  const currentTreeType: TreeType = useAppSelector(getCurrentTreeType);

  const types: {
    [key: string]: {
      icon: JSX.Element;
      title: string;
    };
  } = useMemo(
    () => ({
      [TreeTypes.Rectangular]: {
        icon: <PhyloRectangleIcon />,
        title: i18n("PhylocanvasShapeDropDown.rectangular"),
      },
      [TreeTypes.Radial]: {
        icon: <PhyloRadialIcon />,
        title: i18n("PhylocanvasShapeDropDown.radial"),
      },
      [TreeTypes.Diagonal]: {
        icon: <PhyloDiagonalIcon />,
        title: i18n("PhylocanvasShapeDropDown.diagonal"),
      },
      [TreeTypes.Circular]: {
        icon: <PhyloCircularIcon />,
        title: i18n("PhylocanvasShapeDropDown.circular"),
      },
      [TreeTypes.Hierarchical]: {
        icon: <PhyloHierarchicalIcon />,
        title: i18n("PhylocanvasShapeDropDown.hierarchical"),
      },
    }),
    []
  );

  React.useEffect(() => {
    const current = Object.keys(types).map((key) => (
      <Menu.Item
        key={key}
        disabled={key === currentTreeType}
        style={{ backgroundColor: "transparent" }}
        icon={types[key].icon}
      >
        {types[key].title}
      </Menu.Item>
    ));
    setOptions(current);
  }, [dispatch, currentTreeType, types]);

  const overlay = (
    <Menu onClick={(item) => dispatch(updateTreeType({ treeType: item.key }))}>
      {options}
    </Menu>
  );

  return (
    <Dropdown overlay={overlay} trigger={["click"]}>
      <Button
        title={types[currentTreeType].title}
        style={{ backgroundColor: `var(--grey-1)` }}
        key="changer"
        shape="circle"
        icon={types[currentTreeType].icon}
      />
    </Dropdown>
  );
}
