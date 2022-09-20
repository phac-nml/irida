import { Button, Dropdown, Menu } from "antd";
import React, { useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { updateTreeType } from "../../redux/treeSlice";
import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import {
  PhyloCircularIcon,
  PhyloDiagonalIcon,
  PhyloHierarchicalIcon,
  PhyloRadialIcon,
  PhyloRectangleIcon,
} from "../../../../components/icons/phylocanvas";

export default function PhylocanvasShapeDropDown() {
  const dispatch = useDispatch();
  const [options, setOptions] = React.useState<JSX.Element[]>([]);
  const {
    treeProps: { type },
  } = useSelector((state) => state.tree);

  const types = useMemo(
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
        disabled={key === type}
        style={{ backgroundColor: "transparent" }}
        icon={types[key].icon}
      >
        {types[key].title}
      </Menu.Item>
    ));
    setOptions(current);
  }, [dispatch, type, types]);

  const overlay = (
    <Menu onClick={(item) => dispatch(updateTreeType({ treeType: item.key }))}>
      {options}
    </Menu>
  );

  return (
    <Dropdown overlay={overlay} trigger="click">
      <Button
        title={types[type].title}
        style={{ backgroundColor: `var(--grey-1)` }}
        key="changer"
        shape="circle"
        icon={types[type].icon}
      />
    </Dropdown>
  );
}
