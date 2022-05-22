import { Button, List, Popover, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { BlankIcon } from "./BlankIcon";
import { HexColorPicker } from "react-colorful";
import { setMetadataColourForTermWithValue } from "../../redux/treeSlice";

export function Legend() {
  const { metadataColourMap, terms } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  console.log(metadataColourMap);

  return (
    <List
      header={<Typography.Text>{i18n("visualization.phylogenomics.sidebar.legend.title")}</Typography.Text>}
      dataSource={terms}
      renderItem={(item) => (
        <List.Item style={{width: "100%"}}>
          <Space direction="vertical" style={{width: "100%"}}>
            <List.Item.Meta title={i18n("visualization.phylogenomics.sidebar.legend.colour-by", item)} />
            {item in metadataColourMap ? Object.keys(metadataColourMap[item]).map((key) => (
              <div key={item + "-" + key}>
                <Space direction="horizontal">
                  <Popover
                    content={
                      <HexColorPicker color={metadataColourMap[item][key]} onChange={(color) => dispatch(setMetadataColourForTermWithValue({item, key, color}))}/>
                    }
                    placement="left"
                  >
                    <Button
                      icon={<BlankIcon />}
                      size="small"
                      style={{
                        background: metadataColourMap[item][key],
                        borderColor: metadataColourMap[item][key]
                      }}
                    />
                  </Popover>
                  <Typography.Text>{key}</Typography.Text>
                </Space>
              </div>
            )) : null }
          </Space>
        </List.Item>
      )}
      style={{
        paddingLeft: "14px",
        paddingRight: "14px"
      }}
    />
  );
}