import React from "react";
import {
  CloudServerOutlined,
  CloudUploadOutlined,
  DeleteOutlined,
  DownloadOutlined,
  DownOutlined,
  ExclamationCircleOutlined,
  FilterOutlined,
  FolderOutlined,
  InfoCircleOutlined,
  LoadingOutlined,
  LockOutlined,
  PlusCircleTwoTone,
  QuestionCircleOutlined,
  QuestionOutlined,
  SearchOutlined,
  ShoppingCartOutlined,
  SyncOutlined,
  TableOutlined
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

export const IconQuestionCircle = ({ ...props }) => (
  <QuestionCircleOutlined {...props} />
);

export const IconQuestion = ({ ...props }) => <QuestionOutlined {...props} />;

export const IconExclamationCircle = ({ ...props }) => (
  <ExclamationCircleOutlined {...props} />
);

export const IconLocked = ({ ...props }) => <LockOutlined {...props} />;

export const IconFolder = ({ ...props }) => <FolderOutlined {...props} />;

export const IconTable = ({ ...props }) => <TableOutlined {...props} />;

export const IconPlusCircle = ({ ...props }) => (
  <PlusCircleTwoTone {...props} />
);

export const IconTrash = ({ ...props }) => <DeleteOutlined {...props} />;
