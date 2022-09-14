import { Button, Dropdown, Menu } from "antd";
import React, { useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { updateTreeType } from "../../redux/treeSlice";
import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import styled from "styled-components";

const ClearMenu = styled(Menu)`
  background-color: transparent;
  box-shadow: none;
`;

const ShadowButton = styled(Button)`
  box-shadow: 0 3px 6px -4px rgb(0 0 0 / 12%), 0 6px 16px 0 rgb(0 0 0 / 8%),
    0 9px 28px 8px rgb(0 0 0 / 5%);
`;

export default function PhylocanvasShapeDropDown() {
  const dispatch = useDispatch();
  const [options, setOptions] = React.useState<JSX.Element[]>([]);
  const {
    treeProps: { type },
  } = useSelector((state) => state.tree);

  const types = useMemo(
    () => ({
      [TreeTypes.Rectangular]: {
        icon: setBaseUrl("/resources/img/phylocanvas/rectangular.svg"),
        title: "Rectangular",
      },
      [TreeTypes.Radial]: {
        icon: setBaseUrl("/resources/img/phylocanvas/radial.svg"),
        title: "Radial",
      },
      [TreeTypes.Diagonal]: {
        icon: setBaseUrl("/resources/img/phylocanvas/diagonal.svg"),
        title: "Diagonal",
      },
    }),
    []
  );

  React.useEffect(() => {
    console.log("IN EFFECT");
    const current = Object.keys(types)
      .filter((key) => key !== type)
      .map((key) => (
        <Menu.Item key={key} style={{ backgroundColor: "transparent" }}>
          <ShadowButton
            title={types[key].title}
            onClick={() => dispatch(updateTreeType({ treeType: key }))}
            icon={<img src={types[key].icon} height={20} width={20} />}
            shape="circle"
          />
        </Menu.Item>
      ));
    setOptions(current);
  }, [dispatch, type, types]);

  const overlay = <ClearMenu>{options}</ClearMenu>;

  return (
    <Dropdown overlay={overlay} placement="bottom" trigger="click">
      <Button
        title={types[type].title}
        style={{ backgroundColor: `var(--grey-1)` }}
        icon={<img src={types[type].icon} height={20} width={20} />}
        key="changer"
        shape="circle"
      />
    </Dropdown>
  );
}
