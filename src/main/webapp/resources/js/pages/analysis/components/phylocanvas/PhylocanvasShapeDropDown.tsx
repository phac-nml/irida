import { Button, Dropdown, Menu } from "antd";
import React, { useMemo } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { updateTreeType } from "../../redux/treeSlice";
import { TreeTypes } from "@phylocanvas/phylocanvas.gl";
import styled from "styled-components";
import Icon from "@ant-design/icons";
import { CustomIconComponentProps } from "@ant-design/icons/lib/components/Icon";

const RectangleSVG = () => (
  <svg viewBox="0 0 1024 1024" width="1em" height="1em" fill="currentColor">
    <path d="M468,144c-11.76,0 -22.211,5.651 -28.782,14.384c-4.531,6.021 -7.218,13.507 -7.218,21.616l0,144l-252,0c-4.729,0 -9.246,0.914 -13.382,2.573l-0.017,0.007c0,-0 -0.071,0.029 -0.071,0.029c-13.205,5.339 -22.53,18.285 -22.53,33.391l0,216l-36,0c-19.869,0 -36,16.131 -36,36c0,19.869 16.131,36 36,36l36,0l0,180c0,12.41 6.293,23.362 15.859,29.836l0.019,0.012c-0,0 0.056,0.038 0.056,0.038c5.734,3.86 12.639,6.114 20.066,6.114l720,0c19.869,0 36,-16.131 36,-36c0,-19.869 -16.131,-36 -36,-36l-684,0l0,-396l216,0l0,108c0,8.109 2.687,15.595 7.218,21.616c6.571,8.733 17.022,14.384 28.782,14.384l432,0c19.869,0 36,-16.131 36,-36c0,-19.869 -16.131,-36 -36,-36l-396,0l0,-252l396,0c19.869,0 36,-16.131 36,-36c0,-19.869 -16.131,-36 -36,-36l-432,0Z" />{" "}
  </svg>
);

const RadialSVG = () => (
  <svg viewBox="0 0 1024 1024" width="1em" height="1em" fill="currentColor">
    <path d="M480.996,667.912l257.552,257.551c14.049,14.049 36.862,14.049 50.911,-0c14.05,-14.05 14.05,-36.862 0,-50.912l-277.463,-277.463l0,-124.183l184.905,-184.905l139.095,0c19.869,0 36,-16.131 36,-36c0,-19.869 -16.131,-36 -36,-36l-108,0l0,-108c0,-19.869 -16.131,-36 -36,-36c-19.868,0 -36,16.131 -36,36l0,119.081l-185,185l-257.544,-257.544c-14.049,-14.049 -36.862,-14.049 -50.911,0c-14.05,14.05 -14.05,36.862 -0,50.912l277.455,277.456l0,124.183l-195.455,195.456c-14.05,14.05 -14.05,36.862 -0,50.912c14.049,14.049 36.862,14.049 50.911,-0l185.544,-185.544Z" />
  </svg>
);

const DiagonalSVG = () => (
  <svg viewBox="0 0 1024 1024" width="1em" height="1em" fill="currentColor">
    <path d="M194.973,512c-0.292,-11.525 4.94,-22.988 15.004,-30.206l562.086,-403.098c16.146,-11.579 38.655,-7.871 50.234,8.275c11.58,16.146 7.872,38.656 -8.274,50.235l-522.619,374.794l126.67,90.841l352.71,-252.946c16.146,-11.579 38.656,-7.871 50.235,8.275c11.579,16.146 7.871,38.656 -8.275,50.235l-332.897,238.736l130.766,93.779l159.527,-114.404c16.146,-11.579 38.655,-7.872 50.235,8.275c11.579,16.146 7.871,38.655 -8.275,50.234l-139.714,100.195l141.637,101.574c16.146,11.579 19.854,34.089 8.274,50.235c-11.579,16.146 -34.088,19.854 -50.234,8.275l-562.086,-403.098c-10.064,-7.218 -15.296,-18.681 -15.004,-30.206Z" />
  </svg>
);

const RectangleIcon = (props: Partial<CustomIconComponentProps>) => (
  <Icon component={RectangleSVG} {...props} />
);

const RadialIcon = (props: Partial<CustomIconComponentProps>) => (
  <Icon component={RadialSVG} {...props} />
);

const DiagonalIcon = (props: Partial<CustomIconComponentProps>) => (
  <Icon component={DiagonalSVG} {...props} />
);

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
        icon: <RectangleIcon />,
        title: "Rectangular",
      },
      [TreeTypes.Radial]: {
        icon: <RadialIcon />,
        title: "Radial",
      },
      [TreeTypes.Diagonal]: {
        icon: <DiagonalIcon />,
        title: "Diagonal",
      },
      [TreeTypes.Circular]: {
        icon: setBaseUrl("/resources/img/phylocanvas/circular.svg"),
        title: "Circular",
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
            icon={types[key].icon}
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
        key="changer"
        shape="circle"
        icon={types[type].icon}
      />
    </Dropdown>
  );
}
