import React from "react";
import { FilterOutlined } from "@ant-design/icons";
import { blue6 } from "../../../styles/colors";

export const FilterIcon = ({ filtered, ...props }) => (
         <div
           style={{
             display: "flex",
             alignItems: "center",
             justifyContent: "center",
             height: "100%",
             width: "100%"
           }}
         >
           <FilterOutlined
             style={{ color: filtered ? blue6 : undefined }}
             {...props}
           />
         </div>
       );
