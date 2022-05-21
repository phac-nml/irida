import { List, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import styled from "styled-components";

const ColourBlock = styled.div`
  width: 16px;
  height: 16px;
`

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
                  <ColourBlock style={{backgroundColor: metadataColourMap[item][key]}}/>
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