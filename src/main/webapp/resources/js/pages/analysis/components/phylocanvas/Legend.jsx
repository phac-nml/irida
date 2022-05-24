import { List, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setMetadataColourForTermWithValue } from "../../redux/treeSlice";
import { LegendSection } from "./LegendSection";

export function Legend() {
  const { metadataColourMap, terms } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  return (
    <List
      header={
        <Typography.Text>
          {i18n("visualization.phylogenomics.sidebar.legend.title")}
        </Typography.Text>
      }
      dataSource={terms}
      renderItem={(item) => (
        <List.Item style={{ width: "100%" }}>
          <LegendSection
            title={item}
            sectionColourMap={metadataColourMap[item]}
            onSectionItemColourChange={(key, colour) =>
              dispatch(setMetadataColourForTermWithValue({ item, key, colour }))
            }
          />
        </List.Item>
      )}
      style={{
        paddingLeft: 14,
        paddingRight: 14,
      }}
    />
  );
}
