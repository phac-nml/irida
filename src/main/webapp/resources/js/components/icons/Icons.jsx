import React from "react";
import {
  CloudServerOutlined,
  CloudUploadOutlined,
  DownloadOutlined,
  DownOutlined,
  FilterOutlined,
  FolderOutlined,
  InfoCircleOutlined,
  LoadingOutlined,
  LockOutlined,
  SearchOutlined,
  ShoppingCartOutlined,
  SyncOutlined
} from "@ant-design/icons";
import { blue6 } from "../../styles/colors";

export const IconTableFilter = ({ filtered, ...props }) => (
  <FilterOutlined style={{ color: filtered ? blue6 : undefined }} {...props} />
);

export const IconSearch = ({ ...props }) => <SearchOutlined {...props} />;

export const IconDownloadFile = ({ ...props }) => (
  <DownloadOutlined {...props} />
);

export const IconSyncSpin = ({ ...props }) => <SyncOutlined spin {...props} />;

export const IconCloudServer = ({ ...props }) => (
  <CloudServerOutlined {...props} />
);

export const IconCloudUpload = ({ ...props }) => (
  <CloudUploadOutlined {...props} />
);

export const IconLoading = ({ ...props }) => <LoadingOutlined {...props} />;

export const IconDropDown = ({ ...props }) => <DownOutlined {...props} />;

export const IconShoppingCart = ({ ...props }) => (
  <ShoppingCartOutlined {...props} />
);

export const IconInfoCircle = ({ ...props }) => (
  <InfoCircleOutlined {...props} />
);

export const IconLocked = ({ ...props }) => <LockOutlined {...props} />;

export const IconFolder = ({ ...props }) => <FolderOutlined {...props} />;
