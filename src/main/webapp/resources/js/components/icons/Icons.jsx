import React from "react";
import {
  CloudServerOutlined,
  DownloadOutlined,
  DownOutlined,
  FilterOutlined,
  LoadingOutlined,
  ShoppingCartOutlined,
  SyncOutlined
} from "@ant-design/icons";
import { blue6 } from "../../styles/colors";

export const IconTableFilter = ({ filtered, ...props }) => (
  <FilterOutlined style={{ color: filtered ? blue6 : undefined }} {...props} />
);

export const IconDownloadFile = ({ ...props }) => (
  <DownloadOutlined {...props} />
);

export const IconSyncSpin = ({ ...props }) => <SyncOutlined spin {...props} />;

export const IconCloudServer = ({ ...props }) => (
  <CloudServerOutlined {...props} />
);

export const IconLoading = ({ ...props }) => <LoadingOutlined {...props} />;

export const IconDropDown = ({ ...props }) => <DownOutlined {...props} />;

export const IconShoppingCart = ({ ...props }) => (
  <ShoppingCartOutlined {...props} />
);
