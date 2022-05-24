import { List, Space, Typography } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { setMetadataColourForTermWithValue } from "../../redux/treeSlice";
import { LegendItem } from "./LegendItem";

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
          <Space direction="vertical" style={{ width: "100%" }}>
            <List.Item.Meta
              title={i18n(
                "visualization.phylogenomics.sidebar.legend.colour-by",
                item
              )}
            />
            {item in metadataColourMap
              ? Object.keys(metadataColourMap[item]).map((key) => (
                  <LegendItem
                    key={item + "-" + key}
                    label={key}
                    colour={metadataColourMap[item][key]}
                    onChange={(colour) =>
                      dispatch(
                        setMetadataColourForTermWithValue({
                          item,
                          key,
                          colour,
                        })
                      )
                    }
                  />
                ))
              : null}
          </Space>
        </List.Item>
      )}
      style={{
        paddingLeft: 14,
        paddingRight: 14,
      }}
    />
  );
}
